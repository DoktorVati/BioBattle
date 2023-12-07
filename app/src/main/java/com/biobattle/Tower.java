package com.biobattle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    private int towerNumber, projectileResource;
    private int totalUpgrades = 0;
    private ImageView imageView;
    private float attackDamage,attackSpeed,attackRange;
    private float newAttackRange, newAttackDamage, newAttackSpeed;
    private float upgradePercentage = 1.15f;
    public MainActivity mainActivity;
    private TowerScript towerScript;
    private boolean isCannon, isGolgi, hasShot, hasPlacedDown;
    private boolean isOnCooldown = false;
    private boolean isAnimating = false;
    private float GOLGI_ATTACK_DURATION = 100;
    private float GOLGI_COOLDOWN_DURATION = 3000;
    private boolean isInfiniteAttack = false;
    private MediaPlayer deathMediaPlayer;
    private MediaPlayer cannonFireMediaPlayer;
    private MediaPlayer cannonHitMediaPlayer;
    private MediaPlayer golgiFireMediaPlayer;
    private MediaPlayer killertFireMediaPlayer;

    public Tower(ImageView imageView, float attackRange, float attackDamage, float attackSpeed, MainActivity mainActivity, float upgradePercentages, boolean hasPlaced, int projectile)
    {
        this.hasPlacedDown = hasPlaced;
        this.imageView = imageView;
        this.attackRange = attackRange;
        this.newAttackRange = attackRange;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.mainActivity = mainActivity;
        this.dontrun = false;
        this.upgradePercentage = upgradePercentages;
        this.hasShot = true;
        this.projectileResource = projectile;
        if(mainActivity != null) {
            deathMediaPlayer = MediaPlayer.create(mainActivity, R.raw.death);
            cannonFireMediaPlayer = MediaPlayer.create(mainActivity, R.raw.cannon_f);
            cannonFireMediaPlayer.setVolume(0.2f,0.2f);
            cannonHitMediaPlayer = MediaPlayer.create(mainActivity, R.raw.cannon_h);
            cannonHitMediaPlayer.setVolume(0.5f, 0.5f);
            golgiFireMediaPlayer = MediaPlayer.create(mainActivity, R.raw.golgishoot);
            killertFireMediaPlayer = MediaPlayer.create(mainActivity, R.raw.killshoot);
            killertFireMediaPlayer.setVolume(0.2f, 0.2f);
        }
    }
    public void setTowerScript(TowerScript script) {
        this.towerScript = script;
    }

    public void cleanupScriptIfImageViewDeleted() {
        towerScript.stop();
        towerScript = null; // Clear the reference to the script
    }
    public void setTowerNumber(int towerNumber) {
        this.towerNumber = towerNumber;
    }

    public void upgrade(int imageResource) {
        newAttackRange = attackRange * upgradePercentage;
        newAttackDamage = attackDamage * upgradePercentage;
        newAttackSpeed = attackSpeed * upgradePercentage;

        attackRange = (newAttackRange += upgradePercentage);
        attackDamage = (newAttackDamage *= upgradePercentage);
        attackSpeed = (newAttackSpeed *= upgradePercentage);
        GOLGI_COOLDOWN_DURATION -= upgradePercentage;
        totalUpgrades ++;
    }
    public ImageView getImageView()
    {
        return imageView;
    }
    public float getAttackRange()
    {
        return this.attackRange;
    }
    public void setAttackRange(float attackRange)
    {
        this.attackRange = attackRange;
    }
    public float getDamage()
    {
        return this.attackDamage;
    }
    public void setDamage(float attackDamage)
    {
        this.attackDamage = attackDamage;
    }
    public float getAttackSpeed()
    {
        return attackSpeed;
    }
    public void setAttackSpeed(float attackSpeed)
    {
        this.attackSpeed = attackSpeed;
    }
    public float getTotalUpgrades()
    {
        if (imageView == null) {
            return 0; // Return default value for towers without an image view
        } else if (imageView.getTag().equals(R.drawable.simpletower)) {
            return totalUpgrades * 1;
        } else if (imageView.getTag().equals(R.drawable.golgitower)) {
            return totalUpgrades * 1.5f;
        } else if (imageView.getTag().equals(R.drawable.cannontower)) {
            return totalUpgrades * 2.25f;
        } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
            return totalUpgrades * 3;
        } else {
            return totalUpgrades; // Default multiplier for unrecognized towers
        }
    }
    public boolean hasReachedMaxUpgrades(boolean upgradeMenu) {
        int maxUpgrades = 3; // Maximum upgrades allowed
        if (imageView == null) {
            return totalUpgrades >= maxUpgrades; // Return true if upgrades reach the limit for towers without an image view
        } else if (imageView.getTag().equals(R.drawable.simpletower)) {
            if(upgradeMenu)
                return totalUpgrades >= 5;
            return totalUpgrades >= 6;
        } else if (imageView.getTag().equals(R.drawable.golgitower)) {
            if(upgradeMenu)
                return totalUpgrades >= 4;
            return totalUpgrades >= 5;
        } else if (imageView.getTag().equals(R.drawable.cannontower)) {
            if(upgradeMenu)
                return totalUpgrades >= 3;
            return totalUpgrades >= 4;
        } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
            if(upgradeMenu)
                return totalUpgrades >= 2;
            return totalUpgrades >= 3;
        }
        else
        {
            return totalUpgrades >= 3;
        }
    }
    public boolean dontrun;
    public void checkEnemyInRange(List<Enemy> enemiesInWave) {
        if (towerScript != null && towerScript.isRunning() == false) {
            dontrun = true;
        }
        if (!isOnCooldown && dontrun == false && hasPlacedDown == true) {
            Enemy targetEnemy = null;
            float closestDistance = Float.MAX_VALUE;

            for (Enemy enemy : enemiesInWave) {
                float enemyX = enemy.getCenterX();
                float enemyY = enemy.getCenterY();
                ImageView enemyImageView = enemy.getImageView();
                FrameLayout containerLayout = (FrameLayout) enemyImageView.getParent();
                float towerX = imageView.getX() + imageView.getWidth() / 2 - 120;
                float towerY = imageView.getY() + imageView.getHeight() / 2 - 80;
                float distance = calculateDistance(towerX, towerY, enemyX, enemyY);

                if (distance <= this.newAttackRange) {
                    // Check if the enemy is closer than the current target
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        targetEnemy = enemy;
                        if(targetEnemy != null && towerScript != null && !isOnCooldown && hasShot == false) {
                            hasShot = false;
                            ImageView projectile = new ImageView(containerLayout.getContext());
                            float projectileStartX = towerX + imageView.getWidth() / 2 - projectile.getWidth() / 2;
                            float projectileStartY = towerY + imageView.getHeight() / 2 - projectile.getHeight() / 2;
                            projectile.setX(projectileStartX);
                            projectile.setY(projectileStartY);
                            ObjectAnimator projectileAnimatorX = null;
                            ObjectAnimator projectileAnimatorY = null;

                            if (imageView.getTag() != null && targetEnemy != null && !isOnCooldown) {
                                if (imageView.getTag().equals(R.drawable.simpletower)) {
                                    projectileResource = R.drawable.singleshot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1060, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                                    projectile.setScaleX(0.5f);
                                    projectile.setScaleY(0.5f);
                                } else if (imageView.getTag().equals(R.drawable.cannontower)) {
                                    projectileResource = R.drawable.cannonshot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1055, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                                    projectile.setScaleX(0.2f);
                                    projectile.setScaleY(0.2f);
                                } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
                                    projectileResource = R.drawable.killershot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1150, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 550, enemyY - 440);
                                    projectile.setScaleX(0.1f);
                                    projectile.setScaleY(0.1f);

                                    if (killertFireMediaPlayer.isPlaying()) {
                                        killertFireMediaPlayer.seekTo(0);
                                    } else {
                                        killertFireMediaPlayer.start();
                                    }

                                } else if (imageView.getTag().equals(R.drawable.golgitower)) {
                                    projectileResource = 0;
                                    if(!isOnCooldown) {
                                        if (golgiFireMediaPlayer.isPlaying()) {
                                            golgiFireMediaPlayer.seekTo(0);
                                        } else {
                                            golgiFireMediaPlayer.start();
                                        }
                                        handleGolgiAttack(containerLayout, enemiesInWave, towerX, towerY);
                                        isInfiniteAttack = true;
                                    }
                                    break;
                                }
                            } else {
                                projectileResource = R.drawable.cannonshot1;
                                projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1050, enemyX - 1040);
                                projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                            }
                            if (projectileResource != 0 && !isOnCooldown) {
                                projectile.setImageResource(projectileResource);
                                containerLayout.addView(projectile);

                                AnimatorSet projectileAnimation = new AnimatorSet();
                                projectileAnimation.playTogether(projectileAnimatorX, projectileAnimatorY);
                                projectileAnimation.setDuration(50);
                                // animation duration(adjust as needed)
                                Enemy finalTargetEnemy = targetEnemy;
                                projectileAnimation.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if(projectileResource != R.drawable.cannonshot1 && projectileResource != 0) {
                                            containerLayout.removeView(projectile);
                                            hasShot = true;
                                            isAnimating = false;

                                        }
                                        else if(projectileResource == R.drawable.cannonshot1){
                                            doSplashDamage(enemy.getCenterX(), enemy.getCenterY(), 50, enemiesInWave, true);
                                            containerLayout.removeView(projectile);

                                            ImageView explosion = new ImageView(containerLayout.getContext());
                                            explosion.setX(finalTargetEnemy.getCenterX() - 1040);
                                            explosion.setY(finalTargetEnemy.getCenterY() - 440);
                                            explosion.setScaleX(0.3f);
                                            explosion.setScaleY(0.3f);
                                            explosion.setImageResource(R.drawable.explosionanim);
                                            containerLayout.addView(explosion);
                                            AnimationDrawable explosionAnimation = (AnimationDrawable) explosion.getDrawable();
                                            explosionAnimation.start();
                                            int explosionDuration = 1500;
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    containerLayout.removeView(explosion);
                                                }
                                            }, explosionDuration);
                                            hasShot = true;
                                            isAnimating = false;
                                            isCannon = true;
                                        }
                                        else {
                                            startCooldown();
                                            hasShot = true;
                                            isAnimating = false;
                                            isGolgi = true;
                                        }
                                    }
                                });
                                if(isAnimating == false) {
                                    projectileAnimation.start();
                                    hasShot = true;
                                }
                            }
                            else{
                                hasShot = true;
                            }
                        }
                    }
                }
            }
            if (targetEnemy != null && hasShot == true && !isGolgi) {
                startCooldown();
                if(isCannon)
                {
                    if (cannonFireMediaPlayer.isPlaying()) {
                        cannonFireMediaPlayer.seekTo(0);
                    } else {
                        cannonFireMediaPlayer.start();
                    }
                }
                targetEnemy.loseHealth(attackDamage);
                if(targetEnemy.getHealth() <= 0) {
                    deleteEnemy(targetEnemy); // this deletes the closest enemy within range
                }
                targetEnemy = null;
            }
        }
    }
    private void handleGolgiAttack(FrameLayout containerLayout, List<Enemy> enemiesInWave, float towerX, float towerY) {
        if (isInfiniteAttack) {
            isInfiniteAttack = false;

            // Simulating almost instant attack speed behavior with a cooldown
            final Handler golgiAttackHandler = new Handler();
            Runnable golgiAttackRunnable = new Runnable() {
                @Override
                public void run() {
                    doSplashDamage(towerX, towerY, attackRange, enemiesInWave, false);
                    startGolgiCooldown();
                }
            };
            golgiAttackHandler.postDelayed(golgiAttackRunnable,(long) (GOLGI_ATTACK_DURATION));
        }
    }
    private void startGolgiCooldown() {
        if (!isOnCooldown) {
            isOnCooldown = true;
            hasShot = true;
            isAnimating = true;
            // Golgi cooldown period
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOnCooldown = false; // Cooldown finished, tower can attack again
                    hasShot = false;
                }
            },(long) (GOLGI_COOLDOWN_DURATION * 10 / attackSpeed));
        }
    }
    private void doSplashDamage(float centerX, float centerY, float damageRadius, List<Enemy> enemiesInWave, boolean isCannon) {
        if(isCannon) {
            if (cannonHitMediaPlayer.isPlaying()) {
                cannonHitMediaPlayer.seekTo(0);
            } else {
                cannonHitMediaPlayer.start();
            }
            for (Enemy enemy : getAllEnemiesInRadius(centerX, centerY, damageRadius, enemiesInWave)) {
                //enemy.takeDamage(50);
                enemy.loseHealth(attackDamage / 2);

                if (enemy.getHealth() <= 0) {
                    deleteEnemy(enemy); // this deletes the closest enemy within range
                }
            }
        }
        else {
            for (Enemy enemy : getAllEnemiesInRadius(centerX, centerY, damageRadius, enemiesInWave)) {
                enemy.loseHealth(attackDamage / 2);
                spawnProjectiles(enemy.getImageView());
                if (enemy.getHealth() <= 0) {
                    deleteEnemy(enemy); // this deletes the closest enemy within range
                }
            }
        }
    }
    private List<Enemy> getAllEnemiesInRadius(float centerX, float centerY, float damageRadius,List<Enemy> enemiesInWave) {
        List<Enemy> enemiesInRadius = new ArrayList<>();

        // Loop through all enemies to find those within the tower's damage radius
        for (Enemy enemy : enemiesInWave) {
            float enemyX = enemy.getCenterX();
            float enemyY = enemy.getCenterY();

            float distance = calculateDistance(centerX, centerY, enemyX, enemyY);
            if (distance <= damageRadius) {
                enemiesInRadius.add(enemy);
            }
        }
        return enemiesInRadius;
    }
    private float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    public void setPlacedDown()
    {
        hasPlacedDown = true;
    }

    private void deleteEnemy(Enemy enemy) {
        ImageView enemyImageView = enemy.getImageView();
        FrameLayout containerLayout = (FrameLayout) enemyImageView.getParent();
        Log.d("EnemyAttempt", "Attempt");

        if (mainActivity == null) {
            Log.d("WaveNull", "Wave object is null");
            return; // Exit the method if wave is null
        }

        if (containerLayout != null && towerScript != null && hasPlacedDown == true) {
            containerLayout.removeView(enemyImageView);
            mainActivity.deleteEnemyView(enemy);
            if (deathMediaPlayer.isPlaying()) {
                deathMediaPlayer.seekTo(0);
            } else {
                deathMediaPlayer.start();
            }

            mainActivity.addGold(5);
        }
    }

    private void startCooldown() {
        if (!isOnCooldown) {
            isOnCooldown = true;
            final int cooldownDuration = calculateCooldownDuration();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOnCooldown = false; // Cooldown finished tower can attack again
                    hasShot = false;
                }
            }, cooldownDuration);
        }
    }

    private int calculateCooldownDuration() {

        return (int) (1000000 / (attackSpeed * 50));
    }

    public void spawnProjectiles(ImageView enemyImageView) {
        if (!hasPlacedDown || isAnimating) {
            return;
        }

        isAnimating = true;
        float towerX = imageView.getX() + imageView.getWidth() / 2 - 1100;
        float towerY = imageView.getY() + imageView.getHeight() / 2 - 500;

        float[][] directions = {
                {0, -(attackRange)}, // Straight up
                {0, attackRange},  // Straight down
                {-(attackRange * 0.66f), -(attackRange * 0.66f)}, // Up and to the left
                {attackRange * 0.66f, -(attackRange * 0.66f)}, // Up and to the right
                {-(attackRange), 0}, // Left
                {attackRange, 0}, // Right
                {-(attackRange * 0.66f), attackRange * 0.66f}, // Down and to the left
                {attackRange * 0.66f, attackRange * 0.66f} // Down and to the right
        };

        List<Animator> animators = new ArrayList<>();
        FrameLayout containerLayout = (FrameLayout) enemyImageView.getParent();

        for (float[] direction : directions) {
            ImageView projectile = new ImageView(containerLayout.getContext());
            containerLayout.addView(projectile);
            projectile.setX(towerX);
            projectile.setY(towerY);
            projectile.setImageResource(R.drawable.tacframe8);
            projectile.setScaleX(0.1f);
            projectile.setScaleY(0.1f);
            ObjectAnimator projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectile.getX() + direction[0]);
            ObjectAnimator projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectile.getY() + direction[1]);

            AnimatorSet projectileAnimation = new AnimatorSet();
            projectileAnimation.playTogether(projectileAnimatorX, projectileAnimatorY);
            projectileAnimation.setDuration(400);

            projectileAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    containerLayout.removeView(projectile);
                    isAnimating = false;
                }
            });

            animators.add(projectileAnimation);
        }

        // Play all the projectile animations together
        AnimatorSet allProjectilesAnimation = new AnimatorSet();
        allProjectilesAnimation.playTogether(animators);
        allProjectilesAnimation.start();
    }
}

