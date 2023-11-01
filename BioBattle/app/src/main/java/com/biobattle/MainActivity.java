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
        //Change the R drawables to the new towers names

        // Find and set image resources for ImageButtons
        setupSpawnButton(R.id.spawnButton1, dragBasic, R.drawable.map);
        setupSpawnButton(R.id.spawnButton2, dragTac, R.drawable.playbutton);
        setupSpawnButton(R.id.spawnButton3, dragCannon, R.drawable.frame_tower);
        setupSpawnButton(R.id.spawnButton4, dragKillerTom, R.drawable.killertcellp);
    }

    private void setupSpawnButton(int spawnButtonId, final ImageView dragImageView, final int imageResource) {
        ImageButton spawnButton = findViewById(spawnButtonId);

        spawnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spawnDragTower(dragImageView, imageResource);
            }
        });

        dragImageView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isDragging = true;
                        offsetX = (int) event.getX();
                        offsetY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            int x = (int) event.getRawX();
                            int y = (int) event.getRawY();

                            dragImageView.setX(x - offsetX);
                            dragImageView.setY(y - offsetY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        break;
                }
                return true;
            }
        });
    }


    public void spawnDragTower(ImageView dragImageView, int imageResource) {
        // Create a new ImageView with the specified image resource
        final ImageView newDragImageView = new ImageView(this);
        newDragImageView.setImageResource(imageResource);

        // Optionally, you can set the initial position:
        initialX = (container.getWidth() - newDragImageView.getWidth()) / 2;
        initialY = (container.getHeight() - newDragImageView.getHeight()) / 2;
        newDragImageView.setX(initialX);
        newDragImageView.setY(initialY);

        // Add a click listener to the new ImageView
        newDragImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your JavaScript code here to handle the click event
                // For example, you can open a web page in a WebView or perform other actions.
                // You may need to set up a WebView or define your JavaScript code accordingly.
            }
        });

        // Implement touch event handling for dragging
        newDragImageView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = (int) event.getRawX() - lastX;
                        int deltaY = (int) event.getRawY() - lastY;

                        float newX = v.getX() + deltaX;
                        float newY = v.getY() + deltaY;

                        // Update the position of the ImageView
                        v.setX(newX);
                        v.setY(newY);

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    default:
                        break;
                }
                return true;
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

}
