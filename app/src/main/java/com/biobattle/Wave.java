package com.biobattle;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Wave {
    private int enemiesSpawned;
    private int totalEnemies;
    private Context context; // Reference to the application context
    private List<Enemy> enemiesInWave; // List to keep track of enemies in the wave
    private int waveNumber; // Track the wave number
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
        int animationResource = R.drawable.enemybanim;
        int enemyHealth = 0;
        int enemySpeed = 0;
        // Determine the image resource based on enemy type
        if (type == 1) {
            imageResource = R.drawable.enemyb;
            animationResource = R.drawable.enemybanim;
            enemyHealth = 200;
            enemySpeed = 3500;
        } else if (type == 2) {
            imageResource = R.drawable.enemyy;
            animationResource = R.drawable.enemyyanim;
            enemyHealth = 500;
            enemySpeed = 1500;
        } else if (type == 3) {
            imageResource = R.drawable.enemyr;
            animationResource = R.drawable.enemyranim;
            enemyHealth = 1000;
            enemySpeed = 6000;
        } else {
            imageResource = R.drawable.enemyb; // Default to a type if unspecified
            animationResource = R.drawable.enemybanim;
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
        Enemy enemy = new Enemy(newEnemyImageView, enemyHealth, enemySpeed);

        enemy.setMainActivity(mainActivity);
        enemiesInWave.add(enemy);

        containerLayout.addView(newEnemyImageView);

        enemy.startPath(newEnemyImageView, containerLayout.getWidth(), containerLayout.getHeight(), animationResource);

    }

    private void spawnBoss() {
        int bossType = 4;
        int imageResource = R.drawable.boss;
        int animationResource = R.drawable.bossanim;
        int bossHealth = 3000;
        int bossSpeed = 4500;
        int bossWidth = 350;
        int bossHeight = 350;

        ImageView newBossImageView = new ImageView(context);
        newBossImageView.setImageResource(imageResource);

        // Adjusting the position of the boss enemy
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bossWidth, bossHeight);
        params.leftMargin = 0; // Set left margin to 0
        params.topMargin = 0; // Set top margin to 0
        newBossImageView.setLayoutParams(params);

        // Create a Boss Enemy object and add it to the wave
        Enemy bossEnemy = new Enemy(newBossImageView, bossHealth, bossSpeed);

        bossEnemy.setMainActivity(mainActivity);
        enemiesInWave.add(bossEnemy);

        containerLayout.addView(newBossImageView);

        bossEnemy.startPath(newBossImageView, containerLayout.getWidth(), containerLayout.getHeight(), animationResource);
    }

    // Getter for accessing the list of enemies in the wave
    public List<Enemy> getEnemiesInWave() {
        return enemiesInWave;
    }

    // Method to calculate the number of enemies for the wave based on wave number
    private int calculateNumberOfEnemies(int waveNumber) {
        // Define an equation for increasing difficulty
        return (int) (waveNumber * 1.5) + 5;
    }

    public void startWave(int waveNumber) {
        if (waveNumber % 10 == 0) {
            startBossWave(waveNumber);
        } else {
            startRegularWave(waveNumber);
        }
    }

    // Method to spawn a wave of enemies
    public void startRegularWave(int waveNumber) {
        int numberOfEnemies = calculateNumberOfEnemies(waveNumber);
        Random random = new Random();
        mainActivity.startEnemyCheck();
        final Handler handler = new Handler();
        final long delayBetweenEnemies = 1000; //Sets delay in milliseconds (about 1 second)

        for (int i = 0; i < numberOfEnemies; i++) {
            final int enemyType = random.nextInt(3) + 1; // Randomly select enemy type

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spawnEnemy(enemyType);
                }
            }, i * delayBetweenEnemies);
        }
        // Checking for remaining enemies
        checkEnemiesInWave();
    }

    private void startBossWave(int waveNumber) {
        // Show boss incoming message
        if (mainActivity != null) {
            mainActivity.showBossIncomingMessage();
        }

        spawnBoss();
        checkEnemiesInWave();
    }

    // Periodically checks enemies in wave at an interval
    private void checkEnemiesInWave() {
        final Handler handler = new Handler();
        final int delay = 5000; // Check every 5 seconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mainActivity != null) {
                    mainActivity.onWaveEnd();
                    handler.postDelayed(this, delay); // Schedule next check
                }
            }
        }, delay);
    }

    // Method to mark the end of the wave
    public void endWave() {
        for (Enemy enemy : enemiesInWave) {
            mainActivity.stopEnemyCheck(); //Stop checking enemy movements
            mainActivity.addGold(25 * waveNumber);

            // Update button visibility at end of wave
            if (mainActivity != null) {
                mainActivity.onWaveEnd();
            }
        }
        enemiesInWave.clear(); // Clear the list of enemies in the wave
    }

    // Checks if there are enemies in current wave
    public boolean hasEnemiesInWave() {
        return !enemiesInWave.isEmpty();
    }
}
