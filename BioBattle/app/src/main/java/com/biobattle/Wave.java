package com.biobattle;

import android.content.Context;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Wave {
    private Context context; // Reference to the application context
    private List<Enemy> enemiesInWave; // List to keep track of enemies in the wave
    private int waveNumber; // Track the wave number
    //private int enemyType; // Track the type of enemies in this wave
    private FrameLayout containerLayout;
    private MainActivity mainActivity;
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public void removeEnemy(Enemy enemy) {
        enemiesInWave.remove(enemy);
    }
    public Wave(Context context, int waveNumber, FrameLayout containerLayout) {
        this.context = context;
        this.waveNumber = waveNumber;
        this.enemiesInWave = new ArrayList<>();
        this.containerLayout = containerLayout;
    }
    public FrameLayout getScreen()
    {
        return containerLayout;
    }
    // Method to spawn a single enemy of the specified type
    private void spawnEnemy(int type) {
        int imageResource;
        // Determine the image resource based on enemy type
        if (type == 1) {
            imageResource = R.drawable.enemyb;
        } else if (type == 2) {
            imageResource = R.drawable.enemyy;
        } else if (type == 3) {
            imageResource = R.drawable.enemyr;
        } else {
            imageResource = R.drawable.enemyb; // Default to a type if unspecified
        }

        ImageView newEnemyImageView = new ImageView(context);
        newEnemyImageView.setImageResource(imageResource);

        // Adjusting the position of the enemy
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = 0; // Set left margin to 0
        params.topMargin = 0; // Set top margin to 0
        newEnemyImageView.setLayoutParams(params);

        // Create an Enemy object and add it to the wave
        Enemy enemy = new Enemy(newEnemyImageView);
        enemiesInWave.add(enemy);

        containerLayout.addView(newEnemyImageView);

        enemy.startPath(newEnemyImageView, containerLayout.getWidth(), containerLayout.getHeight());
    }

    // Getter for accessing the list of enemies in the wave
    public List<Enemy> getEnemiesInWave() {
        return enemiesInWave;
    }

    // Method to calculate the number of enemies for the wave based on wave number
    private int calculateNumberOfEnemies(int waveNumber) {
        // Define an exponential equation for increasing difficulty
        return (int) Math.pow(waveNumber, 2) + 5; // Equation is: waveNumber^2 + 5
    }

    // Method to spawn a wave of enemies
    public void startWave() {
        int numberOfEnemies = calculateNumberOfEnemies(1);
        Random random = new Random();
        mainActivity.startEnemyCheck();
        final Handler handler = new Handler();
        final long delayBetweenEnemies = 2000; //Sets delay in milliseconds

        for (int i = 0; i < numberOfEnemies; i++) {
            final int enemyType = random.nextInt(3) + 1; // Randomly select enemy type (1, 2, or 3)

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spawnEnemy(enemyType);
                }
            }, i * delayBetweenEnemies);
        }
    }

    // Method to mark the end of the wave
    public void endWave() {
        for (Enemy enemy : enemiesInWave) {
            //enemy.stopMovement();
            mainActivity.stopEnemyCheck();
        }
        enemiesInWave.clear(); // Clear the list of enemies in the wave
    }

    // Method to handle player defeat
    public void playerDefeated() {
        // Stop all enemy movements
        for (Enemy enemy : enemiesInWave) {
            //enemy.stopMovement();
        }

        // Clear the list of enemies in the wave
        enemiesInWave.clear();

        // Additional actions or logic for player defeat can be added here
        // For example:
        // - Show game over screen
        // - Reset player status
        // - Perform other game-related actions when the player is defeated

        // To show the game over screen, you can invoke a method in MainActivity
        if (context instanceof MainActivity) {
            ((MainActivity) context).showGameOverScreen();
        }
    }
}
