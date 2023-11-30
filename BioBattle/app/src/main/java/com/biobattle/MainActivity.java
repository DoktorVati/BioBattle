package com.biobattle;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;

import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;

public class MainActivity extends AppCompatActivity {
    private int initialX;
    private int initialY;
    public int animationResource;
    public float setAttackRange, setAttackDamage, setAttackSpeed;
    private boolean towerSelected = false;
    private ImageView selectedTower; // Store the selected tower
    private int selectedTowerResource;
    private Map<ImageView, AttackRangeView> attackRangeMap = new HashMap<>();
    private Map<ImageView, ImageView> towerAnimationMap = new HashMap<>();
    private Map<ImageView, Tower> imageViewTowerMap = new HashMap<>();

    private List<Tower> purchasedTowers = new ArrayList<>(); // Store purchased towers

    private Tower basicTower;
    private Tower tacTower;
    private Tower cannonTower;
    private Tower killerTomTower;

    private AttackRangeView attackRangeView;
    //These are the buyable towers in a list
    private List<Tower> towers = new ArrayList<>();
    private MediaPlayer selectMediaPlayer;
    private MediaPlayer buyMediaPlayer;
    private MediaPlayer sellMediaPlayer;
    private MediaPlayer upgradeMediaPlayer;
    private MediaPlayer bossMediaPlayer;
    private MediaPlayer backgroundMediaPlayer;
    private Wave wave; //Declaring a Wave instance
    private FrameLayout enemyContainerLayout;
    float setMultiplier;
    private int playerHealth = 100;
    private String stringPlayerHealth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attackRangeView = findViewById(R.id.attackRangeCircleView);
        View backgroundView = findViewById(R.id.map);

        // Find and associate ImageViews with buttons
        ImageView dragBasic = findViewById(R.id.dragBasic);
        ImageView dragTac = findViewById(R.id.dragTac);
        ImageView dragCannon = findViewById(R.id.dragCannon);
        ImageView dragKillerTom = findViewById(R.id.dragKillerTom);

        //adds button functionality
        basicTower = new Tower(dragBasic, 0, 0, 0, null, 0);
        tacTower = new Tower(dragTac, 0, 0, 0, null, 0);
        cannonTower = new Tower(dragCannon, 0, 0, 0, null, 0);
        killerTomTower = new Tower(dragKillerTom, 0, 0, 0, null, 0);

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
        setupTowerSelection(dragBasic, R.drawable.simpletower);
        setupTowerSelection(dragTac, R.drawable.golgitower);
        setupTowerSelection(dragCannon, R.drawable.cannontower);
        setupTowerSelection(dragKillerTom, R.drawable.killertframe1);

        // Set up the "Buy" button
        ImageButton buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (towerSelected) {
                    // Buy the tower only when the "Buy" button is clicked
                    spawnDragTower(selectedTowerResource, 0, 0, false);
                    // Reset tower selection
                    towerSelected = false;
                    buyMediaPlayer.start();
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
                        // Pass the tower's current attack range to the upgrade method
                        tower.upgrade(selectedTowerResource);
                        TextView upgradeMenuTextView = findViewById(R.id.UpgradeMenu);
                        if (upgradeMenuTextView != null) {
                            float upgrades = tower.getTotalUpgrades();
                            int upgradeCost = calculateUpgradeCost(upgrades);
                            upgradeMenuTextView.setText(String.valueOf(upgradeCost));
                        }
                        //upgrade sound
                        upgradeMediaPlayer.start();
                        upgradeMediaPlayer.seekTo(0);
                        showAttackRange(tower.getAttackRange());
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
                    //sell sound
                    sellMediaPlayer.start();
                    hideUpgradeMenus(true);
                    deleteSelectedTower(selectedTower);
                }
            }
        });

        // This should turn menu invisible when selecting other stuff
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
                    //selectedTowerResource = 0;
                }

                return false;
            }
        });
        RelativeLayout textBox;
        textBox = findViewById(R.id.textbox);

        // Call this method where you want the opacity transition to begin
        manipulateOpacity(textBox);

        ImageButton startWaveButton = findViewById(R.id.startWave);
        startWaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting wave when the Start Wave button is clicked
                wave.startWave();
            }
        });


        FrameLayout enemyContainerLayout = findViewById(R.id.enemyContainerLayout);
        wave = new Wave (MainActivity.this, 1, enemyContainerLayout);
        wave.setMainActivity(this);

    }




    private void setupTowerSelection(final ImageView towerImageView, final int imageResource) {
        towerImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //selectMediaPlayer.start();
                for (Tower tower : towers) {
                    if (tower.getImageView() == towerImageView) {
                        selectedTowerResource = imageResource; // Store the selected tower resource
                        towerSelected = true; // Flag to indicate tower selection
                        setSelectFrameVisibility(towerImageView);
                        break;
                    }
                }

                hideUpgradeMenus(false);
                 // Hide attack range and upgrade menus
            }

        });

        // Set up touch event listener for individual towers to prevent the upgrade menu from showing up when the parent is touched
        towerImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Consume touch events for the parent tower
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
    //future Check if is in path so that towers cant be placed here
    /*public boolean isWithinPath(float towerX, float towerY) {
        // Define your path boundaries here
        float pathStartX =  Start X coordinate of the path ;
        float pathStartY =  Start Y coordinate of the path ;
        float pathEndX =  End X coordinate of the path ;
        float pathEndY =  End Y coordinate of the path ;

        // Check if the tower's position intersects with the path boundaries
        if (towerX >= pathStartX && towerX <= pathEndX &&
                towerY >= pathStartY && towerY <= pathEndY) {
            return true; // Tower spawn position is within the path boundaries
        } else {
            return false; // Tower spawn position is outside the path boundaries
        }
    } */
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

        // Determine which parent tower was clicked and show the corresponding select frame
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
        //setSelectFrameVisibility(map);
        // Find the container view where UpgradeMenu assets are located
        GridLayout upgradeContainer = findViewById(R.id.gridLayout);

        // Iterate through child views of the container
        for (int i = 0; i < upgradeContainer.getChildCount(); i++) {
            View child = upgradeContainer.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("UpgradeMenu")) {
                // Set visibility to VISIBLE for UpgradeMenu assets
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
        // Iterate through child views of the container
        for (int i = 0; i < upgradeContainer.getChildCount(); i++) {
            View child = upgradeContainer.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("UpgradeMenu")) {
                // Set visibility to GONE for UpgradeMenu assets
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


        // Create a new ImageView with the specified image resource
        final ImageView newDragImageView = new ImageView(this);
        newDragImageView.setImageResource(imageResource);
        // Initialize variables to track initial touch position
        float initialTouchX = touchX;
        float initialTouchY = touchY;

        // Get the original image's dimensions
        int originalWidth = newDragImageView.getDrawable().getIntrinsicWidth();
        int originalHeight = newDragImageView.getDrawable().getIntrinsicHeight();
        float scale = 1f;
        // Create a new Tower instance for this spawned tower

        // Set the scaling factor for the tower based on imageResource
        if (imageResource == R.drawable.simpletower) {
            scale = 0.85f; // Adjust the scale factor as needed
            animationResource = R.drawable.simpleidleanim;
            setAttackRange = 325;
            setAttackDamage = 100;
            setAttackSpeed = 110;
            towerRadius = 100;
            setMultiplier = 1.14f;
        } else if (imageResource == R.drawable.golgitower) {
            scale = 1.7f;
            animationResource = R.drawable.golgiidleanim;
            setAttackRange = 250;
            setAttackDamage = 110;
            setAttackSpeed = 90;
            towerRadius = 170;
            setMultiplier = 1.25f;
        } else if (imageResource == R.drawable.cannontower) {
            scale = 1f;
            animationResource = R.drawable.cannonidleanim;
            setAttackRange = 375;
            setAttackDamage = 220;
            setAttackSpeed = 75;
            towerRadius = 100;
            setMultiplier = 1.5f;
        } else if (imageResource == R.drawable.killertframe1) {
            scale = 1f;
            animationResource = R.drawable.killeridleanim;
            setAttackRange = 450;
            setAttackDamage = 300;
            setAttackSpeed = 300;
            towerRadius = 170;
            setMultiplier = 2f;
        }

        TowerScript towerScript = new TowerScript();
        Tower tower = new Tower(newDragImageView, setAttackRange, setAttackDamage, setAttackSpeed, this, setMultiplier);
        tower.setTowerNumber(purchasedTowers.size() + 1); // Assign a unique number to the tower
        tower.setTowerScript(towerScript);

        // Calculate the scaled width and height
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        attackRangeView.setCenter(newDragImageView.getX() + scaledWidth / 2, newDragImageView.getY() + scaledHeight / 2);
        attackRangeView.setRadius(0);
        // Hide the attack range circle initially
        attackRangeView.setVisibility(View.INVISIBLE);

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

            @Override
            public void onClick(View v) {
                float scale = 1f;

                if (imageResource == R.drawable.simpletower) {
                    scale = 0.85f; // Adjust the scale factor as needed
                    animationResource = R.drawable.simpleidleanim;
                    setAttackRange = 325;
                    setAttackDamage = 100;
                    setAttackSpeed = 110;
                    towerRadius = 100;
                } else if (imageResource == R.drawable.golgitower) {
                    scale = 1.7f;
                    animationResource = R.drawable.golgiidleanim;
                    setAttackRange = 250;
                    setAttackDamage = 110;
                    setAttackSpeed = 90;
                    towerRadius = 170;
                } else if (imageResource == R.drawable.cannontower) {
                    scale = 1f;
                    animationResource = R.drawable.cannonidleanim;
                    setAttackRange = 375;
                    setAttackDamage = 220;
                    setAttackSpeed = 75;
                    towerRadius = 100;
                } else if (imageResource == R.drawable.killertframe1) {
                    scale = 1f;
                    animationResource = R.drawable.killeridleanim;
                    setAttackRange = 450;
                    setAttackDamage = 300;
                    setAttackSpeed = 300;
                    towerRadius = 170;
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
                            int upgradeCost = calculateUpgradeCost(upgrades); // Calculate upgrade cost based on tower and upgrade count
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

                                    // Use a Handler to remove the cloudView after the animation completes
                                    new Handler().postDelayed(() -> {
                                        parentView.removeView(cloudView);
                                        // Add code here if you want to perform any actions after the animation is removed
                                    }, totalDuration);
                                }
                                newDragImageView.setTag(imageResource); // Use a unique identifier as the tag
                                animationImageView.setTag(imageResource); // Use the same tag
                                towerAnimationMap.put(newDragImageView, animationImageView);
                                addTower(newDragImageView.getX() + scaledWidth / 2, newDragImageView.getY() + scaledHeight / 2);

                                // Get the drawable from the ImageView and start the animation
                                Drawable animationDrawable = animationImageView.getDrawable();
                                if (animationDrawable != null && animationDrawable instanceof AnimationDrawable) {
                                    ((AnimationDrawable) animationDrawable).start();
                                }
                                v.setOnTouchListener(null); // Remove the touch listener
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
        float tolerance = 0.001f; // Define a tolerance level for float comparisons

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
        // Additional tower placement checks (overlap with existing towers, initial tower placement)
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
            float x = towerImageView.getX(); // Get X position
            float y = towerImageView.getY(); // Get Y position
            Tower tower = getTowerFromImageView(towerImageView);

            ViewGroup parentView = (ViewGroup) towerImageView.getParent();
            if (parentView != null) {
                // Remove the tower ImageView instantly
                purchasedTowers.remove(towerImageView);
                parentView.removeView(towerImageView);
                tower.cleanupScriptIfImageViewDeleted();
                selectedTower = null;
                delTower(towerImageView.getX() + towerImageView.getWidth() / 2, towerImageView.getY() + towerImageView.getHeight() / 2);

                // Check if the tower has an associated animation
                ImageView animationImageView = towerAnimationMap.get(towerImageView);
                if (animationImageView != null) {
                    parentView.removeView(animationImageView);
                    // Set layout parameters for cloudView
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    // Remove the tower's animation view from the map
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
                    // Maintain opacity at 100% for 3 seconds
                    handler.postDelayed(() -> reverseOpacity(textbox), 3000);
                }
            }
        }, delay);
    }
    private Handler handler = new Handler();
    private final int DELAY = 500; // Delay in milliseconds

    private Runnable enemyCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkEnemiesInRangeForAllTowers();
            handler.postDelayed(this, DELAY); // Re-post this runnable with a delay
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

        // Gradually decrease the opacity
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
                    // Set the textbox to invisible after 7 seconds
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
            backgroundMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMediaPlayer != null && !backgroundMediaPlayer.isPlaying()) {
            backgroundMediaPlayer.start();
        }
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
        // Inflate the game over layout
        View gameOverLayout = getLayoutInflater().inflate(R.layout.game_over, null);

        // Add the game over layout as an overlay
        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(gameOverLayout);

        // Hide the original map and/or any other elements
        View activityMainLayout = findViewById(R.id.rootLayout);
        activityMainLayout.setVisibility(View.GONE);

        // Maybe add restart button
        Button playAgainButton = gameOverLayout.findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to restart the game or perform necessary actions
                // For example:
                // - Restart the game by reloading initial state
                // - Clear game data and reset scores
                // - Show initial game screen

                // Remove the game over layout
                rootView.removeView(gameOverLayout);

                // Show the original map and/or any other elements
                activityMainLayout.setVisibility(View.VISIBLE);

                // Call a method to restart the game or perform necessary actions
                // restartGame();
            }
        });
    }

    public void losePlayerHealth(){
        playerHealth = playerHealth - 1;
        stringPlayerHealth = "Health " + playerHealth;
        TextView playerHealthView = (TextView) findViewById(R.id.health);
        playerHealthView.setText(stringPlayerHealth);
    }
}
