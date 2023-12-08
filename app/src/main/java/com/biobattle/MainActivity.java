package com.biobattle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int initialX;
    private int initialY;
    private Toast currentToast;
    public int totalGold = 25500;
    public int animationResource;
    public RelativeLayout textBox;
    public float setAttackRange, setAttackDamage, setAttackSpeed;
    private boolean towerSelected = false;
    private ImageView selectedTower; // Store the selected tower
    private int selectedTowerResource;
    private Map<ImageView, AttackRangeView> attackRangeMap = new HashMap<>();
    private Map<ImageView, ImageView> towerAnimationMap = new HashMap<>();
    private Map<ImageView, Tower> imageViewTowerMap = new HashMap<>();
    private List<Tower> purchasedTowers = new ArrayList<>(); // Store purchased towers


    private boolean hasPlaced;
    private Tower basicTower;
    private Tower tacTower;
    private Tower cannonTower;
    private Tower killerTomTower;
    private int waveNumber = 1;
    private TextView waveTextView;
    private AttackRangeView attackRangeView;
    //These are the buyable towers in a list
    private List<Tower> towers = new ArrayList<>();
    private MediaPlayer selectMediaPlayer;
    private MediaPlayer buyMediaPlayer;
    private MediaPlayer sellMediaPlayer;
    private MediaPlayer upgradeMediaPlayer;
    private MediaPlayer bossMediaPlayer;
    private MediaPlayer backgroundMediaPlayer;
    private Wave wave;
    private FrameLayout enemyContainerLayout;
    float setMultiplier;
    private int playerHealth = 100;
    private String stringPlayerHealth;
    private ImageButton startWaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attackRangeView = findViewById(R.id.attackRangeCircleView);
        View backgroundView = findViewById(R.id.map);
        boolean changeImage = getIntent().getBooleanExtra("CHANGE_IMAGE", false);

        if (changeImage == true) {
            ImageView backgroundImageView = findViewById(R.id.map);
            // Set the image resource to map2
            backgroundImageView.setImageResource(R.drawable.map2);
        }else {
            ImageView backgroundImageView = findViewById(R.id.map);
            backgroundImageView.setImageResource(R.drawable.map);
        }
        TextView cashMoneyTextView = findViewById(R.id.money);
        if (cashMoneyTextView != null) {
            cashMoneyTextView.setText("Gold " + totalGold);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        purchasedTowers.clear();

        // Find and associate ImageViews with buttons
        ImageView dragBasic = findViewById(R.id.dragBasic);
        ImageView dragTac = findViewById(R.id.dragTac);
        ImageView dragCannon = findViewById(R.id.dragCannon);
        ImageView dragKillerTom = findViewById(R.id.dragKillerTom);

        //adds button functionality
        basicTower = new Tower(dragBasic, 0, 0, 0, null, 0, false, 0);
        tacTower = new Tower(dragTac, 0, 0, 0, null, 0, false, 0);
        cannonTower = new Tower(dragCannon, 0, 0, 0, null, 0, false, 0);
        killerTomTower = new Tower(dragKillerTom, 0, 0, 0, null, 0, false, 0);

        //adds frames
        towers.add(basicTower);
        towers.add(tacTower);
        towers.add(cannonTower);
        towers.add(killerTomTower);

        backgroundMediaPlayer = MediaPlayer.create(this, R.raw.main);
        backgroundMediaPlayer.setLooping(true);
        backgroundMediaPlayer.start();
        backgroundMediaPlayer.setVolume(0.5f,0.5f);
        selectMediaPlayer = MediaPlayer.create(this, R.raw.selection);
        selectMediaPlayer.setVolume(0.2f, 0.2f);
        buyMediaPlayer = MediaPlayer.create(this, R.raw.place);
        sellMediaPlayer = MediaPlayer.create(this, R.raw.sell);
        upgradeMediaPlayer = MediaPlayer.create(this, R.raw.upgrade);
        // Set up the tower button selection
        setupTowerSelection(dragBasic, R.drawable.simpletower, 250);
        setupTowerSelection(dragTac, R.drawable.golgitower, 450);
        setupTowerSelection(dragCannon, R.drawable.cannontower, 650);
        setupTowerSelection(dragKillerTom, R.drawable.killertframe1, 3250);

        // Set up the "Buy" button
        ImageButton buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (towerSelected) {
                    buyMediaPlayer.start();
                    // Buy the tower only when the "Buy" button is clicked
                    spawnDragTower(selectedTowerResource, 0, 0, false);
                    // Reset tower selection
                    towerSelected = false;

                    hideUpgradeMenus(true);
                }
            }
        });

        ImageButton upgradeButton = findViewById(R.id.upgradeMenu);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTower != null) {
                    Tower tower = getTowerByImageView(selectedTower);
                    if (tower != null) {
                        if(tower.hasReachedMaxUpgrades(true) == true) {
                            hideUpgradeMenus(false);
                        }

                        TextView upgradeMenuTextView = findViewById(R.id.UpgradeMenu);
                        if (upgradeMenuTextView != null) {
                            float upgrades = tower.getTotalUpgrades();
                            int upgradeCost = calculateUpgradeCost(upgrades);
                            upgradeMenuTextView.setText(String.valueOf(upgradeCost));

                            if(totalGold >= upgradeCost) {
                                subtractGold(upgradeCost);
                                tower.upgrade(selectedTowerResource);
                                upgrades = tower.getTotalUpgrades();
                                upgradeCost = calculateUpgradeCost(upgrades);
                                upgradeMenuTextView.setText(String.valueOf(upgradeCost));
                                //upgrade sound
                                upgradeMediaPlayer.start();
                                upgradeMediaPlayer.seekTo(0);
                                showAttackRange(tower.getAttackRange());
                            }
                            else {
                                if (currentToast != null) {
                                    currentToast.cancel(); // Cancel the currently displayed toast
                                }
                                // Show the toast message for when not enough money
                                currentToast = Toast.makeText(MainActivity.this, "Insufficient funds", Toast.LENGTH_SHORT);
                                currentToast.show();
                            }
                        }

                    }
                }
            }
        });

        // Find the "Sell" button
        ImageButton sellButton = findViewById(R.id.sellButton);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the selected tower if it's not null
                if (selectedTower != null)
                {
                    Tower tower = getTowerByImageView(selectedTower);
                    float upgrades = tower.getTotalUpgrades();
                    int upgradeCost = calculateUpgradeCost(upgrades);
                    if(selectedTowerResource == R.drawable.simpletower)
                    {
                        if(upgradeCost == 125) {
                            addGold(125);
                        }else if (upgradeCost == 250) {
                            addGold(286);
                        } else if (upgradeCost == 500) {
                            addGold(412);
                        } else if (upgradeCost == 1000) {
                            addGold(562);
                        } else if (upgradeCost == 2000) {
                            addGold(1062);
                        }
                        else if (upgradeCost == 4000){
                            addGold(2062);
                        }
                        else {
                            addGold(4062);
                        }
                    } else if (selectedTowerResource == R.drawable.golgitower) {
                        if(upgradeCost == 125) {
                            addGold(225);
                        } else if(upgradeCost == 250) {
                            addGold(286);
                        } else if (upgradeCost == 1000) {
                            addGold(412);
                        } else if (upgradeCost == 2750) {
                            addGold(912);
                        }
                        else if(upgradeCost == 8000){
                            addGold(2288);
                        }
                        else {
                            addGold(4288);
                        }
                    } else if(selectedTowerResource == R.drawable.cannontower) {
                        if(upgradeCost == 125) {
                            addGold(325);
                        } else if (upgradeCost == 500) {
                            addGold(386);
                        } else if (upgradeCost == 2750) {
                            addGold(638);
                        }
                        else if (upgradeCost == 13375){
                            addGold(2012);
                        }
                        else {
                            addGold(8700);
                        }
                    } else if (selectedTowerResource == R.drawable.killertframe1) {
                        if (upgradeCost == 125)
                        {
                            addGold(1625);
                        } else if (upgradeCost == 1000) {
                            addGold(1688);
                        } else if (upgradeCost == 8000) {
                            addGold(2188);
                        }else {
                            addGold(6188);
                        }
                    }

                    //sell sound
                    sellMediaPlayer.start();
                    hideUpgradeMenus(true);
                    deleteSelectedTower(selectedTower);
                }
            }
        });
        // This turns menu invisible when selecting other stuff
        backgroundView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Hide the upgrade menu when the background is touched
                hideUpgradeMenus(true);

                if (towerSelected && event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Place the tower at the touch location
                    spawnDragTower(selectedTowerResource, event.getRawX(), event.getRawY(), true);
                    // Reset tower selection after placing
                    towerSelected = false;
                }

                return false;
            }
        });
        textBox = findViewById(R.id.textbox);

        // Call this method where you want the opacity transition to begin
        manipulateOpacity(textBox);

        waveTextView = findViewById(R.id.wave);

        startWaveButton = findViewById(R.id.startWave);
        startWaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting wave when the Start Wave button is clicked
                //waveMediaPlayer.start();
                wave.startWave(waveNumber);
                startWaveButton.setVisibility(View.GONE);
                // Update TextView with current wave number
                waveTextView.setText("Wave: " + waveNumber);

                // Incrementing wave number
                waveNumber ++;
            }
        });


        FrameLayout enemyContainerLayout = findViewById(R.id.enemyContainerLayout);
        wave = new Wave (MainActivity.this, waveNumber, enemyContainerLayout);
        wave.setMainActivity(this);
    }




    private void setupTowerSelection(final ImageView towerImageView, final int imageResource, final int towerCostAmount) {
        towerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalGold >= towerCostAmount) {
                    for (Tower tower : towers) {
                        if (tower.getImageView() == towerImageView) {
                            selectedTowerResource = imageResource; // Store the selected tower resource
                            towerSelected = true; // Flag to indicate tower selection
                            setSelectFrameVisibility(towerImageView);
                            break;
                        }
                    }
                    hideUpgradeMenus(false);
                } else {
                    if (currentToast != null) {
                        currentToast.cancel(); // Cancel the currently displayed toast
                    }
                    // Show the toast message for when not enough funds
                    currentToast = Toast.makeText(MainActivity.this, "Insufficient funds", Toast.LENGTH_SHORT);
                    currentToast.show();
                }
            }
        });


        // Set up touch event listener for individual towers to prevent the upgrade menu from showing up when the shop tower is touched
        towerImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                selectMediaPlayer.start();
                return false;
            }
        });
    }

    private void showAttackRange(float attackRange) {
        attackRangeView.invalidate();
        attackRangeView.setRadius(attackRange);
        if (selectedTower != null) {
            attackRangeView.setCenter(selectedTower.getX() + selectedTower.getWidth() / 2,
                    selectedTower.getY() + selectedTower.getHeight() / 2);
            attackRangeView.setVisibility(View.VISIBLE);
        }

    }

    private void setSelectFrameVisibility(ImageView towerImageView)
    {
        // Find the select frames
        ImageView selectFrame1 = findViewById(R.id.selectframe1);
        ImageView selectFrame2 = findViewById(R.id.selectframe2);
        ImageView selectFrame3 = findViewById(R.id.selectframe3);
        ImageView selectFrame4 = findViewById(R.id.selectframe4);

        // Hide all select frames
        selectFrame1.setVisibility(View.INVISIBLE);
        selectFrame2.setVisibility(View.INVISIBLE);
        selectFrame3.setVisibility(View.INVISIBLE);
        selectFrame4.setVisibility(View.INVISIBLE);

        // Determine which shop tower was clicked and show the corresponding select frame
        if (towerImageView.getId() == R.id.dragBasic) {
            selectFrame1.setVisibility(View.VISIBLE);
            hideUpgradeMenus(false);
        } else if (towerImageView.getId() == R.id.dragTac) {
            selectFrame2.setVisibility(View.VISIBLE);
            hideUpgradeMenus(false);
        } else if (towerImageView.getId() == R.id.dragCannon) {
            selectFrame3.setVisibility(View.VISIBLE);
            hideUpgradeMenus(false);
        } else if (towerImageView.getId() == R.id.dragKillerTom) {
            selectFrame4.setVisibility(View.VISIBLE);
            hideUpgradeMenus(false);
        }
    }
    // Method to make all assets with the tag "UpgradeMenu" visible
    private void showUpgradeMenus()
    {
        //this hides the select frame
        ImageView map = findViewById(R.id.map);
        attackRangeView.setVisibility(View.VISIBLE);

        // Find the container view where UpgradeMenu assets are located
        GridLayout upgradeContainer = findViewById(R.id.gridLayout);

        // Iterate through child views of the container to set them visible
        for (int i = 0; i < upgradeContainer.getChildCount(); i++) {
            View child = upgradeContainer.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("UpgradeMenu")) {
                child.setVisibility(View.VISIBLE);
            }
        }
    }
    // Method to hide all assets with the tag "UpgradeMenu"
    private void hideUpgradeMenus(boolean isSelectFrame) {
        // Find the container view where UpgradeMenu assets are located
        GridLayout upgradeContainer = findViewById(R.id.gridLayout);
        attackRangeView.setVisibility(View.INVISIBLE);

        //this hides the select frame
        if(isSelectFrame) {
            ImageView map = findViewById(R.id.map);
            setSelectFrameVisibility(map);
        }
        // Iterate through child views of the container to make them invisible
        for (int i = 0; i < upgradeContainer.getChildCount(); i++) {
            View child = upgradeContainer.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("UpgradeMenu")) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }
    public boolean canPlace;
    private void createAttackRange(ImageView towerImageView) {
        if (!attackRangeMap.containsKey(towerImageView)) {
            AttackRangeView attackRangeView = new AttackRangeView(this);
            attackRangeView.setVisibility(View.INVISIBLE);
            attackRangeMap.put(towerImageView, attackRangeView);
        }
    }
    public float towerRadius = 80f;

    public void spawnDragTower(final int imageResource, float touchX, float touchY, boolean isMapPress) {
        int projectile = 0;
        // Creates a new ImageView with the wanted image resource
        final ImageView newDragImageView = new ImageView(this);
        newDragImageView.setImageResource(imageResource);
        // variables track initial touch position
        float initialTouchX = touchX;
        float initialTouchY = touchY;

        // Get the original image's dimensions
        int originalWidth = newDragImageView.getDrawable().getIntrinsicWidth();
        int originalHeight = newDragImageView.getDrawable().getIntrinsicHeight();
        float scale = 1f;

        // Sets the scaling factor for the tower based on imageResource
        if (imageResource == R.drawable.simpletower) {
            scale = 0.85f;
            animationResource = R.drawable.simpleidleanim;
            setAttackRange = 325;
            setAttackDamage = 100;
            setAttackSpeed = 60;
            towerRadius = 100;
            setMultiplier = 1.14f;
            projectile = R.drawable.singleshot1;
            subtractGold(250);
        } else if (imageResource == R.drawable.golgitower) {
            scale = 1.7f;
            animationResource = R.drawable.golgiidleanim;
            setAttackRange = 250;
            setAttackDamage = 50;
            setAttackSpeed = 6;
            towerRadius = 170;
            setMultiplier = 1.25f;
            projectile = 0;
            subtractGold(450);
        } else if (imageResource == R.drawable.cannontower) {
            scale = 1f;
            animationResource = R.drawable.cannonidleanim;
            setAttackRange = 375;
            setAttackDamage = 220;
            setAttackSpeed = 15;
            towerRadius = 100;
            setMultiplier = 1.2f;
            projectile = R.drawable.cannonshot1;
            subtractGold(650);
        } else if (imageResource == R.drawable.killertframe1) {
            scale = 1f;
            animationResource = R.drawable.killeridleanim;
            setAttackRange = 450;
            setAttackDamage = 300;
            setAttackSpeed = 150;
            towerRadius = 170;
            setMultiplier = 1.25f;
            projectile = R.drawable.killershot1;
            subtractGold(3250);
        }
        TowerScript towerScript = new TowerScript();
        Tower tower = new Tower(newDragImageView, setAttackRange, setAttackDamage, setAttackSpeed, this, setMultiplier, false, projectile);
        tower.setTowerNumber(purchasedTowers.size() + 1); // Assign a unique number to the tower
        tower.setTowerScript(towerScript);

        // Calculate the scaled width and height
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        attackRangeView.setCenter(newDragImageView.getX() + scaledWidth / 2, newDragImageView.getY() + scaledHeight / 2);
        attackRangeView.setRadius(0);
        // Hide the attack range circle initially
        attackRangeView.setVisibility(View.INVISIBLE);
        newDragImageView.setTag(imageResource); // Use a unique identifier as the tag
        // Resize the new ImageView
        newDragImageView.setLayoutParams(new ViewGroup.LayoutParams(scaledWidth, scaledHeight));

        // Calculate the initial position in the center of the screen
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        imageViewTowerMap.put(newDragImageView, tower);

        initialX = (screenWidth - scaledWidth) / 2;
        initialY = (screenHeight - scaledHeight) / 2;
        // Find the target view where you want to add the new ImageView
        View targetView = findViewById(R.id.linearLayout);

        if (targetView instanceof ViewGroup) {
            ((ViewGroup) targetView).addView(newDragImageView);
            createAttackRange(newDragImageView); // Associate AttackRangeView with the tower
        }

        int cloud = R.drawable.cloudanim;
        newDragImageView.setOnClickListener(new View.OnClickListener() {
            boolean animationRun = false; // Flag to track if animation has run for this tower
            int projectile = 0;
            @Override
            public void onClick(View v) {
                float scale = 1f;

                // Set the scaling factor for the tower based on imageResource
                if (imageResource == R.drawable.simpletower) {
                    scale = 0.85f;
                    animationResource = R.drawable.simpleidleanim;
                    setAttackRange = 325;
                    setAttackDamage = 100;
                    setAttackSpeed = 110;
                    towerRadius = 100;
                    setMultiplier = 1.14f;
                    projectile = R.drawable.singleshot1;
                } else if (imageResource == R.drawable.golgitower) {
                    scale = 1.7f;
                    animationResource = R.drawable.golgiidleanim;
                    setAttackRange = 250;
                    setAttackDamage = 110;
                    setAttackSpeed = 90;
                    towerRadius = 170;
                    setMultiplier = 1.25f;
                    projectile = 0;
                } else if (imageResource == R.drawable.cannontower) {
                    scale = 1f;
                    animationResource = R.drawable.cannonidleanim;
                    setAttackRange = 375;
                    setAttackDamage = 220;
                    setAttackSpeed = 75;
                    towerRadius = 100;
                    setMultiplier = 1.5f;
                    projectile = R.drawable.cannonshot1;
                } else if (imageResource == R.drawable.killertframe1) {
                    scale = 1f;
                    animationResource = R.drawable.killeridleanim;
                    setAttackRange = 450;
                    setAttackDamage = 300;
                    setAttackSpeed = 300;
                    towerRadius = 170;
                    setMultiplier = 2f;
                    projectile = R.drawable.killershot1;
                }
                if (!animationRun && isMapPress/*add so that it checks if not on path in future*/) {
                    selectMediaPlayer.start();
                    // Create the animation ImageView
                    ImageView animationImageView = new ImageView(v.getContext());
                    ImageView cloudView = new ImageView(v.getContext());
                    cloudView.setLayoutParams(new ViewGroup.LayoutParams(scaledWidth, scaledHeight));
                    cloudView.setX(v.getX());
                    cloudView.setY(v.getY()+50f);
                    cloudView.setImageResource(cloud);

                    animationImageView.setImageResource(animationResource);
                    // Apply the same scaling factor as the original ImageView
                    animationImageView.setLayoutParams(new ViewGroup.LayoutParams(scaledWidth, scaledHeight));

                    // Set the new ImageView's position to match the original ImageView
                    animationImageView.setX(v.getX());
                    animationImageView.setY(v.getY());

                    animationImageView.setImageResource(animationResource);

                    // Add the new ImageView to the same parent as the original view
                    ViewGroup parentView = (ViewGroup) v.getParent();
                    parentView.addView(animationImageView);
                    parentView.addView(cloudView);
                    // Set tags and manage the tower animation map
                    newDragImageView.setTag(imageResource); // Use a unique identifier as the tag
                    animationImageView.setTag(imageResource); // Use the same tag
                    towerAnimationMap.put(newDragImageView, animationImageView);
                    Drawable cloudDrawable = cloudView.getDrawable();


                    if (cloudDrawable != null && cloudDrawable instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) cloudDrawable;
                        animationDrawable.start();

                        // Calculate the total duration of the animation
                        int totalDuration = 0;
                        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                            totalDuration += animationDrawable.getDuration(i);
                        }

                        // Use a Handler to remove the cloudView after the animation completes
                        new Handler().postDelayed(() -> {
                            parentView.removeView(cloudView);
                            // Add code here if you want to perform any actions after the animation is removed
                        }, totalDuration);
                    }
                    // Get the drawable from the ImageView and start the animation
                    Drawable animationDrawable = animationImageView.getDrawable();
                    if (animationDrawable != null && animationDrawable instanceof AnimationDrawable) {
                        ((AnimationDrawable) animationDrawable).start();
                    }

                    // Store the selected tower and disable further selection
                    selectedTower = newDragImageView;
                    // Hide attack ranges and redraws
                    attackRangeView.invalidate();
                    if(tower.hasReachedMaxUpgrades(false) == false) {
                        showUpgradeMenus();
                        TextView upgradeMenuTextView = findViewById(R.id.UpgradeMenu);
                        if (upgradeMenuTextView != null) {
                            float upgrades = tower.getTotalUpgrades();
                            int upgradeCost = calculateUpgradeCost(upgrades);

                            upgradeMenuTextView.setText(String.valueOf(upgradeCost));

                        }
                    }
                    showAttackRange(tower.getAttackRange());
                    // Set the flag to true so that the animation runs only once
                    animationRun = true;
                } else {
                    //play tower selection noise
                    selectMediaPlayer.start();
                    // Store the selected tower and disable further selection
                    selectedTower = newDragImageView;
                    // Hide attack ranges and redraws
                    attackRangeView.invalidate();
                    if(tower.hasReachedMaxUpgrades(false) == false) {
                        showUpgradeMenus();
                        TextView upgradeMenuTextView = findViewById(R.id.UpgradeMenu);
                        if (upgradeMenuTextView != null) {
                            float upgrades = tower.getTotalUpgrades();
                            int upgradeCost = calculateUpgradeCost(upgrades); // Calculate upgrade cost based on tower and upgrade count
                            upgradeMenuTextView.setText(String.valueOf(upgradeCost));
                        }
                    }
                    else {
                        hideUpgradeMenus(false);
                    }
                    showAttackRange(tower.getAttackRange());
                }
            }
        });


        if (isMapPress) {
            // For map press, place the tower at the touched location
            newDragImageView.setX(touchX - scaledWidth / 2);
            newDragImageView.setY(touchY - scaledHeight / 2);
            buyMediaPlayer.start();
            purchasedTowers.add(tower);
            tower.setPlacedDown();
            addTower(newDragImageView.getX() + scaledWidth / 2, newDragImageView.getY() + scaledHeight / 2);
        }

        else {
            newDragImageView.setX(initialX);
            newDragImageView.setY(initialY);
            purchasedTowers.add(tower);

            // Implement touch event handling for dragging
            newDragImageView.setOnTouchListener(new View.OnTouchListener() {
                int lastX, lastY;
                boolean isBeingDragged = false;


                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();
                            isBeingDragged = true;
                            attackRangeView.setVisibility(View.VISIBLE);
                            createAttackRange(newDragImageView);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (isBeingDragged) {

                                int deltaX = (int) event.getRawX() - lastX;
                                int deltaY = (int) event.getRawY() - lastY;

                                float newX = v.getX() + deltaX;
                                float newY = v.getY() + deltaY;

                                // Update the position of the ImageView
                                v.setX(newX);
                                v.setY(newY);

                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                attackRangeView.setCenter(v.getX() + scaledWidth / 2, v.getY() + scaledHeight / 2);
                                attackRangeView.setVisibility(View.VISIBLE);

                                // Calculate the center of the tower
                                float centerX = newX + (scaledWidth / 2);
                                float centerY = newY + (scaledHeight / 2);
                                Tower tower = getTowerByImageView((ImageView) v);
                                if (tower != null) {
                                    tower.setAttackRange(setAttackRange);
                                    tower.setDamage(setAttackDamage);
                                    tower.setAttackSpeed(setAttackSpeed);
                                }

                                // Update the position of the AttackRangeView
                                attackRangeView.setCenter(centerX, centerY);
                                attackRangeView.setRadius(setAttackRange); // Apply the tower's attack range
                                attackRangeView.setVisibility(View.VISIBLE);
                                attackRangeView.invalidate(); // Force redraw
                                if(canPlaceTower(v.getX(), v.getY(), towerRadius))
                                {
                                    canPlace = true;
                                    attackRangeView.setColor(v.getContext(), R.color.transGrey);

                                }
                                else
                                {
                                    canPlace = false;
                                    //add color change to attackrangeView
                                    attackRangeView.setColor(v.getContext(), R.color.transRed);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (canPlace) {
                                tower.setPlacedDown();
                                buyMediaPlayer.start();
                                isBeingDragged = false;
                                attackRangeView.setVisibility(View.INVISIBLE);
                                // Create a new ImageView for the animation
                                ImageView animationImageView = new ImageView(v.getContext());
                                ImageView cloudView = new ImageView(v.getContext());
                                cloudView.setLayoutParams(new ViewGroup.LayoutParams(scaledWidth, scaledHeight));
                                cloudView.setX(v.getX());
                                cloudView.setY(v.getY()+5f);
                                cloudView.setImageResource(cloud);
                                // Apply the same scaling factor as the original ImageView
                                animationImageView.setLayoutParams(new ViewGroup.LayoutParams(scaledWidth, scaledHeight));

                                // Set the new ImageView's position to match the original ImageView
                                animationImageView.setX(v.getX());
                                animationImageView.setY(v.getY());

                                animationImageView.setImageResource(animationResource);

                                // Add the new ImageView to the same parent as the original view
                                ViewGroup parentView = (ViewGroup) v.getParent();
                                parentView.addView(animationImageView);
                                parentView.addView(cloudView);
                                Drawable cloudDrawable = cloudView.getDrawable();

                                if (cloudDrawable != null && cloudDrawable instanceof AnimationDrawable) {
                                    AnimationDrawable animationDrawable = (AnimationDrawable) cloudDrawable;
                                    animationDrawable.start();

                                    // Calculate the total duration of the animation
                                    int totalDuration = 0;
                                    for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                                        totalDuration += animationDrawable.getDuration(i);
                                    }

                                    // removes the cloudView after the animation completes
                                    new Handler().postDelayed(() -> {
                                        parentView.removeView(cloudView);
                                    }, totalDuration);
                                }
                                newDragImageView.setTag(imageResource);
                                animationImageView.setTag(imageResource);
                                towerAnimationMap.put(newDragImageView, animationImageView);
                                addTower(newDragImageView.getX() + scaledWidth / 2, newDragImageView.getY() + scaledHeight / 2);

                                // Get the drawable from the ImageView and start the animation
                                Drawable animationDrawable = animationImageView.getDrawable();
                                if (animationDrawable != null && animationDrawable instanceof AnimationDrawable) {
                                    ((AnimationDrawable) animationDrawable).start();
                                }
                                v.setOnTouchListener(null); // Removes the touch listener
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

        }

    }
    private int calculateUpgradeCost(float upgrades) {
        int baseUpgradeCost = 125; // Initial upgrade cost
        int costMultiplier = 2; // Cost multiplier for each upgrade
        // Calculate upgrade cost based on upgrades
        int upgradeCost = baseUpgradeCost * (int) Math.pow(costMultiplier, upgrades);
        return upgradeCost;
    }

    public Tower getTowerFromImageView(ImageView imageView) {
        return imageViewTowerMap.get(imageView);
    }
    public List<Pair<Float, Float>> towerPositions = new ArrayList<>();
    private void delTower(float x, float y) {
        Pair<Float, Float> towerToRemove = null;
        float tolerance = 0.001f;

        for (Pair<Float, Float> position : towerPositions) {
            if (Math.abs(position.first - x) < tolerance && Math.abs(position.second - y) < tolerance) {
                towerToRemove = position;
                break;
            }
        }

        if (towerToRemove != null) {
            towerPositions.remove(towerToRemove);
        }
    }

    private void addTower(float x, float y) {
        Pair<Float, Float> towerPosition = new Pair<>(x, y);
        towerPositions.add(towerPosition);
    }




    public boolean canPlaceTower(float x, float y, float radius) {
        if (towerPositions.isEmpty()) {
            return true; // If there are no towers placed, allow placement
        }
        for (Pair<Float, Float> position : towerPositions) {
            float existingX = position.first;
            float existingY = position.second;
            float distanceSquared = (x - existingX) * (x - existingX) + (y - existingY) * (y - existingY);
            float minDistanceSquared = (radius + towerRadius) * (radius + towerRadius); // Total allowed radius

            if (distanceSquared < minDistanceSquared) {
                return false; // Overlaps with an existing tower
            }
        }
        return true; // No overlap, can place the tower
    }

    public void deleteSelectedTower(ImageView towerImageView) {
        if (towerImageView != null) {
            float x = towerImageView.getX();
            float y = towerImageView.getY();
            Tower tower = getTowerFromImageView(towerImageView);

            ViewGroup parentView = (ViewGroup) towerImageView.getParent();
            if (parentView != null) {
                // Removes the tower ImageView instantly
                purchasedTowers.remove(towerImageView);
                parentView.removeView(towerImageView);
                tower.cleanupScriptIfImageViewDeleted();
                selectedTower = null;
                delTower(towerImageView.getX() + towerImageView.getWidth() / 2, towerImageView.getY() + towerImageView.getHeight() / 2);

                // Checks if the tower has an associated animation
                ImageView animationImageView = towerAnimationMap.get(towerImageView);
                if (animationImageView != null) {
                    parentView.removeView(animationImageView);
                    // Sets layout parameters for cloudView
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    // Removes the tower's animation view from the map
                    towerAnimationMap.remove(towerImageView);
                }
            }
        }
    }

    //make towers unique
    private Tower getTowerByImageView(ImageView imageView) {
        for (Tower tower : purchasedTowers) {
            if (tower.getImageView() == imageView) {
                return tower;
            }
        }
        return null;
    }
    public void deleteEnemyView(Enemy enemy)
    {
        wave.removeEnemy(enemy);
        wave.decrementEnemies();
        if (wave.getTotalEnemies() <= 0) {
            wave.endWave();
        }
    }
    private void manipulateOpacity(RelativeLayout textbox) {
        final Handler handler = new Handler();
        View textBox = findViewById(R.id.textbox);
        textBox.setVisibility(View.VISIBLE);
        final int delay = 500; // 0.5-second delay for each iteration
        final int totalIterationsIncrease = 6; // 3 seconds divided by 0.5 seconds per iteration
        final int totalIterationsDecrease = 14; // 7 seconds divided by 0.5 seconds per iteration

        // Gradually increase the opacity
        handler.postDelayed(new Runnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (iterations < totalIterationsIncrease) {
                    float alphaValue = iterations * 1.0f / totalIterationsIncrease;
                    textBox.setAlpha(alphaValue);
                    iterations++;
                    handler.postDelayed(this, delay);
                } else {
                    textBox.setAlpha(1);
                    // Maintains the opacity at 100% for 3 seconds so that it can be read
                    handler.postDelayed(() -> reverseOpacity(textbox), 3000);
                }
            }
        }, delay);
    }
    private Handler handler = new Handler();
    private final int DELAY = 500;

    private Runnable enemyCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkEnemiesInRangeForAllTowers();
            handler.postDelayed(this, DELAY);
        }
    };

    // Method to start checking enemies every 100 milliseconds
    public void startEnemyCheck() {
        handler.postDelayed(enemyCheckRunnable, DELAY);
    }

    // Method to stop checking enemies
    public void stopEnemyCheck() {
        handler.removeCallbacks(enemyCheckRunnable);
    }
    public void checkEnemiesInRangeForAllTowers() {
        List<Enemy> enemiesInWave = wave.getEnemiesInWave(); // Get the list of enemies in the wave
        // Get the list of towers
        if(enemiesInWave != null) {
            for (Tower tower : purchasedTowers) {
                if(tower.mainActivity != null && tower.dontrun == false) {
                    tower.checkEnemyInRange(enemiesInWave); // Call the attackEnemies method for each tower
                }
            }
        }
    }
    private void reverseOpacity(RelativeLayout textbox) {
        final Handler handler = new Handler();
        View textBox = findViewById(R.id.textbox);
        final int delay = 500; // 0.5-second delay for each iteration
        final int totalIterationsDecrease = 14; // 7 seconds divided by 0.5 seconds per iteration

        // Gradually decreases the opacity
        handler.postDelayed(new Runnable() {
            int iterations = totalIterationsDecrease;

            @Override
            public void run() {
                if (iterations > 0) {
                    float alphaValue = iterations * 1.0f / totalIterationsDecrease;
                    textBox.setAlpha(alphaValue);
                    iterations--;
                    handler.postDelayed(this, delay);
                } else {
                    // Sets the textbox to invisible after 7 seconds
                    textBox.setVisibility(View.INVISIBLE);
                }
            }
        }, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMediaPlayer != null && backgroundMediaPlayer.isPlaying()) {
            backgroundMediaPlayer.pause();
        }
        pauseTowerSounds();

    }

    private void pauseTowerSounds() {
        for (Tower tower : purchasedTowers) {
            tower.pauseMediaPlayers();
        }
    }
    private void resumeTowerSounds() {
        for (Tower tower : purchasedTowers) {
            tower.resumeMediaPlayers();        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMediaPlayer != null && !backgroundMediaPlayer.isPlaying()) {
            backgroundMediaPlayer.start();
        }
        resumeTowerSounds();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMediaPlayer != null) {
            backgroundMediaPlayer.stop();
            backgroundMediaPlayer.release();
            selectMediaPlayer.release();
            backgroundMediaPlayer = null;
        }


    }

    public void showGameOverScreen() {
        View gameOverLayout = getLayoutInflater().inflate(R.layout.game_over, null);

        // overlays the game over layout
        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(gameOverLayout);

        // Hides the original map and things
        View activityMainLayout = findViewById(R.id.rootLayout);
        activityMainLayout.setVisibility(View.GONE);

        // the restart button
        ImageButton playAgainButton = gameOverLayout.findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        ImageButton quitButton = gameOverLayout.findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public void addGold(int amount)
    {
        TextView cashMoneyTextView = findViewById(R.id.money);
        if (cashMoneyTextView != null) {
            totalGold += amount;
            cashMoneyTextView.setText("Gold " + totalGold);
        }
    }

    public void subtractGold(int amount)
    {
        TextView cashMoneyTextView = findViewById(R.id.money);
        if (cashMoneyTextView != null) {
            totalGold -= amount;
            cashMoneyTextView.setText("Gold " + totalGold);
        }
    }
    public void losePlayerHealth(int damage) {
        final int minPlayerHealth = 0; // Player Health shouldn't go below 0

        // Decreasing playerHealth by 1, but not letting it go below 0
        playerHealth = Math.max(playerHealth - damage, minPlayerHealth);

        stringPlayerHealth = "Health " + playerHealth;
        TextView playerHealthView = (TextView) findViewById(R.id.health);
        playerHealthView.setText(stringPlayerHealth);

        // Check if playerHealth is zero
        if (playerHealth <= minPlayerHealth) {
            showGameOverScreen();
        }
    }

    //this prevents an accidental back button destroying the game
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    public void onWaveEnd() {
        if (wave != null && wave.getTotalEnemies() <= 0) {
            startWaveButton.setVisibility(View.VISIBLE);
            int waveToMatch = waveNumber;
            List<Integer> wavesToCheck = Arrays.asList(4,5,10,11,26,51,101,116);

            if (wavesToCheck.contains(waveToMatch)) {
                TextView textView = findViewById(R.id.tipBox);
                int textToShow = 0;

                switch (waveToMatch) {
                    case 4:
                        textToShow = (R.string.level3);
                        break;
                    case 5:
                        textToShow = R.string.level4;
                        break;
                    case 10:
                        textToShow = R.string.level9;
                        break;
                    case 11:
                        textToShow = R.string.level10;
                        break;
                    case 26:
                        textToShow = (R.string.level25);
                        break;
                    case 51:
                        textToShow = R.string.level50;
                        break;
                    case 101:
                        textToShow = R.string.level100;
                        break;
                    case 116:
                        textToShow = R.string.level115;
                        break;

                }

                if (textView != null) {
                    textView.setText(textToShow);
                }
                textBox = findViewById(R.id.textbox);
                manipulateOpacity(textBox);
            }

            addGold(25 * wave.getWave());
        }
    }
    public void showBossIncomingMessage() {
        TextView bossIncomingTextView = findViewById(R.id.bossIncomingTextView);
        bossIncomingTextView.setVisibility(View.VISIBLE);
    }

}
