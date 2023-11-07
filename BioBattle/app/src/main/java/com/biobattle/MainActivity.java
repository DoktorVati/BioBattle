package com.biobattle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout container;
    private int initialX;
    private int initialY;
    private int offsetX;
    private int offsetY;
    private boolean isDragging = false;
    private boolean towerSelected = false;
    private ImageView selectedTower; // Store the selected tower
    private int selectedTowerResource = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        // Find and associate ImageViews with buttons
        ImageView dragBasic = findViewById(R.id.dragBasic);
        ImageView dragTac = findViewById(R.id.dragTac);
        ImageView dragCannon = findViewById(R.id.dragCannon);
        ImageView dragKillerTom = findViewById(R.id.dragKillerTom);

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
                    // Spawn the selected tower
                    spawnDragTower(selectedTowerResource);
                    // Reset tower selection
                    towerSelected = false;
                    selectedTowerResource = 0;
                }
            }
        });

        // Find the "Sell" button
        ImageButton sellButton = findViewById(R.id.sellButton);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the selected tower if it's not null
                if (selectedTower != null) {
                    deleteSelectedTower(selectedTower);
                }
            }
        });
    }

    private void setupTowerSelection(final ImageView towerImageView, final int imageResource) {
        towerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle tower selection
                selectedTowerResource = imageResource;
                towerSelected = true;
            }
        });
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

        // Optionally, you can set the initial position:
        initialX = (container.getWidth() - scaledWidth) / 2;
        initialY = (container.getHeight() - scaledHeight) / 2;
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
            }
        });

        // Find the target view where you want to add the new ImageView
        View targetView = findViewById(R.id.linearLayout);

        if (targetView instanceof ViewGroup) {
            // Add the new ImageView to the target view
            ((ViewGroup) targetView).addView(newDragImageView);
        } else {
            // Handle the case where the target view is not a ViewGroup
            // You can display an error message or perform other actions.
        }
    }

    private void deleteSelectedTower(ImageView towerImageView) {
        if (towerImageView != null) {
            ViewGroup parentView = (ViewGroup) towerImageView.getParent();
            if (parentView != null) {
                parentView.removeView(towerImageView);
                // Reset the selected tower
                selectedTower = null;
            }
        }
    }
}
