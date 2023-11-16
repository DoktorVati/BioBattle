package com.biobattle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.HashMap;
import android.util.DisplayMetrics;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout container;
    private int initialX;
    private int initialY;
    public int animationResource;
    private boolean isDragging = false;
    private boolean towerSelected = false;
    private ImageView selectedTower; // Store the selected tower
    private int selectedTowerResource = 0;
    private Map<ImageView, AttackRangeView> attackRangeMap = new HashMap<>();

    private Tower basicTower;
    private Tower tacTower;
    private Tower cannonTower;
    private Tower killerTomTower;

    private AttackRangeView attackRangeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attackRangeView = findViewById(R.id.attackRangeCircleView);
        attackRangeView.setVisibility(View.INVISIBLE);

        container = findViewById(R.id.container);
        View backgroundView = findViewById(R.id.map);

        // Find and associate ImageViews with buttons
        ImageView dragBasic = findViewById(R.id.dragBasic);
        ImageView dragTac = findViewById(R.id.dragTac);
        ImageView dragCannon = findViewById(R.id.dragCannon);
        ImageView dragKillerTom = findViewById(R.id.dragKillerTom);

        basicTower = new Tower(dragBasic);
        tacTower = new Tower(dragTac);
        cannonTower = new Tower(dragCannon);
        killerTomTower = new Tower(dragKillerTom);

        // Set up the tower selection
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
                    spawnDragTower(selectedTowerResource);
                    // Reset tower selection
                    towerSelected = false;
                    selectedTowerResource = 0;
                    hideUpgradeMenus(true);
                }
            }
        });

        // This is the upgrade button
        ImageButton upgradeButton = findViewById(R.id.upgradeMenu);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Upgrade the selected tower
                upgradeSelectedTower(selectedTower);
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
                return false;
            }
        });
    }

    private void setupTowerSelection(final ImageView towerImageView, final int imageResource) {
        towerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If no tower is selected, set the selected tower resource
                selectedTowerResource = imageResource;
                towerSelected = true;

                // Set visibility for select frames based on the clicked tower
                setSelectFrameVisibility(towerImageView);
                createAttackRange(towerImageView);
            }
        });

        // Set up touch event listener for individual towers to prevent the upgrade menu from showing up when the parent is touched
        towerImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
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
        setSelectFrameVisibility(map);
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
                child.setVisibility(View.GONE);
            }
        }
    }
    private void createAttackRange(ImageView towerImageView) {
        // Create a new AttackRangeView for the tower
        AttackRangeView attackRangeView = new AttackRangeView(this);
        attackRangeView.setVisibility(View.INVISIBLE);
        attackRangeMap.put(towerImageView, attackRangeView);

        // Show the attack range for the selected tower
        showAttackRange(towerImageView);
    }



    private void showAttackRange(ImageView towerImageView) {
        // Create a new AttackRangeView for the tower if it doesn't exist
        if (!attackRangeMap.containsKey(towerImageView)) {
            createAttackRange(towerImageView);
        }

        AttackRangeView attackRangeView = attackRangeMap.get(towerImageView);
        if (attackRangeView != null) {
            // Show the attack range circle for the touched tower
            attackRangeView.setVisibility(View.VISIBLE);
            attackRangeView.setRadius(250); // Set the radius (adjust as needed)
            attackRangeView.invalidate(); // Force redraw
        }
    }
    private Map<ImageView, ImageView> towerAnimationMap = new HashMap<>();

    public void spawnDragTower(final int imageResource) {
        // Create a new ImageView with the specified image resource
        final ImageView newDragImageView = new ImageView(this);
        newDragImageView.setImageResource(imageResource);

        // Get the original image's dimensions
        int originalWidth = newDragImageView.getDrawable().getIntrinsicWidth();
        int originalHeight = newDragImageView.getDrawable().getIntrinsicHeight();
        float scale = 1f;

        // Set the scaling factor for the tower based on imageResource
        if (imageResource == R.drawable.simpletower) {
            scale = 0.85f; // Adjust the scale factor as needed
            animationResource = 0;
        } else if (imageResource == R.drawable.golgitower) {
            scale = 1.7f;
            animationResource = 0;
        } else if (imageResource == R.drawable.cannontower) {
            scale = 1f;
            animationResource = 0;
        } else if (imageResource == R.drawable.killertframe1) {
            scale = 1f;
            animationResource = R.drawable.killeridleanim;
        }

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

        initialX = (screenWidth - scaledWidth) / 2;
        initialY = (screenHeight - scaledHeight) / 2;

        newDragImageView.setX(initialX);
        newDragImageView.setY(initialY);

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
                        // Show the attack range circle on ACTION_DOWN

                        // Hide the attack range circle initially
                        attackRangeView.setVisibility(View.VISIBLE);

                        // Create a new AttackRangeView for the selected tower
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

                            //Make this set radius to attack range from tower class
                            attackRangeView.setRadius(250);
                            attackRangeView.invalidate(); // Force redraw
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isBeingDragged = false;
                        attackRangeView.setVisibility(View.INVISIBLE);
                        // Create a new ImageView for the animation
                        ImageView animationImageView = new ImageView(v.getContext());

                        // Apply the same scaling factor as the original ImageView
                        animationImageView.setLayoutParams(new ViewGroup.LayoutParams(scaledWidth, scaledHeight));

                        // Set the new ImageView's position to match the original ImageView
                        animationImageView.setX(v.getX());
                        animationImageView.setY(v.getY());

                        animationImageView.setImageResource(animationResource);

                        // Add the new ImageView to the same parent as the original view
                        ViewGroup parentView = (ViewGroup) v.getParent();
                        parentView.addView(animationImageView);
                        newDragImageView.setTag(imageResource); // Use a unique identifier as the tag
                        animationImageView.setTag(imageResource); // Use the same tag
                        towerAnimationMap.put(newDragImageView, animationImageView);

                        // Get the drawable from the ImageView and start the animation
                        Drawable animationDrawable = animationImageView.getDrawable();
                        if (animationDrawable != null && animationDrawable instanceof AnimationDrawable) {
                            ((AnimationDrawable) animationDrawable).start();
                        }
                        v.setOnTouchListener(null); // Remove the touch listener
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Add an individual click listener to freeze and sell the tower
        newDragImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the selected tower and disable further selection
                selectedTower = newDragImageView;
                showAttackRange(newDragImageView);
                showUpgradeMenus();
            }
        });


        // Find the target view where you want to add the new ImageView
        View targetView = findViewById(R.id.linearLayout);

        if (targetView instanceof ViewGroup)
        {
            // Add the new ImageView to the target view
            ((ViewGroup) targetView).addView(newDragImageView);
        }
    }

    private ImageView animationImageView;

    private void deleteSelectedTower(ImageView towerImageView) {
        if (towerImageView != null) {
            ViewGroup parentView = (ViewGroup) towerImageView.getParent();
            if (parentView != null) {
                // Check if the tower has an associated animation
                ImageView animationImageView = towerAnimationMap.get(towerImageView);
                if (animationImageView != null) {
                    parentView.removeView(animationImageView);
                    towerAnimationMap.remove(towerImageView); // Remove the mapping
                }

                // Remove the tower ImageView
                parentView.removeView(towerImageView);
                // Reset the selected tower
                selectedTower = null;
            }
        }
    }



    //make towers unique
    private Tower getTowerByImageView(ImageView imageView)
    {
        if (imageView == basicTower.getImageView()) {
            return basicTower;
        } else if (imageView == tacTower.getImageView()) {
            return tacTower;
        } else if (imageView == cannonTower.getImageView()) {
            return cannonTower;
        } else if (imageView == killerTomTower.getImageView()) {
            return killerTomTower;
        } else {
            return null;
        }
    }
    private void upgradeSelectedTower(ImageView selectedTower)
    {
        if (selectedTower != null) {
            // upgrades the selected tower
            Tower tower = getTowerByImageView(selectedTower);
            if (tower != null) {
                tower.upgrade();
            }
        }
    }
    public void spawnEnemy(final int type) {
        int imageResource = 0;

        if (type == 1) {
            imageResource = R.drawable.enemyb;
        } else if (type == 2) {
            imageResource = R.drawable.enemyy;
        } else if (type == 3) {
            imageResource = R.drawable.enemyr;
        } else {
            imageResource = R.drawable.enemyb;
        }

        final ImageView newEnemyImageView = new ImageView(this);
        newEnemyImageView.setImageResource(imageResource);
        // create ID for new enemy
        newEnemyImageView.generateViewId();
        // start path method
        Enemy.Path(newEnemyImageView);

        // variable for display metrics.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        // get metrics for our display
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //get height and width in dp
        float screenHeight = displayMetrics.ydpi;
        float screenWidth = displayMetrics.xdpi;

        newEnemyImageView.setX(screenWidth);
        newEnemyImageView.setY(screenHeight);

    }
}
