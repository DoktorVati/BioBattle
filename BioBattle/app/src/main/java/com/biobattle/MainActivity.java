package com.biobattle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout container;
    private int initialX;
    private int initialY;
    private boolean isDragging = false;
    private boolean towerSelected = false;
    private ImageView selectedTower; // Store the selected tower
    private int selectedTowerResource = 0;

    private Tower basicTower;
    private Tower tacTower;
    private Tower cannonTower;
    private Tower killerTomTower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        setupTowerSelection(dragKillerTom, R.drawable.killertcellp);

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
        } else if (imageResource == R.drawable.golgitower) {
            scale = 1.7f;
        } else if (imageResource == R.drawable.cannontower) {
            scale = 1f;
        } else if (imageResource == R.drawable.killertcellp) {
            scale = 0.07f;
        }

        // Calculate the scaled width and height
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

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
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isBeingDragged = false;
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

    private void deleteSelectedTower(ImageView towerImageView)
    {
        if (towerImageView != null) {
            ViewGroup parentView = (ViewGroup) towerImageView.getParent();
            if (parentView != null) {
                parentView.removeView(towerImageView);
                //reset the selected tower
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

}
