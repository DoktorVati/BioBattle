package com.biobattle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
    private float GOLGI_ATTACK_DURATION = 3000;
    private float GOLGI_COOLDOWN_DURATION = 3000;
    private boolean isInfiniteAttack = false;
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
    }
    public void setTowerScript(TowerScript script) {
        this.towerScript = script;
    }

    public void cleanupScriptIfImageViewDeleted() {
            towerScript.stop(); // Assuming there's a method to stop the script execution
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
        GOLGI_ATTACK_DURATION /= upgradePercentage;
        GOLGI_COOLDOWN_DURATION = GOLGI_ATTACK_DURATION;
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
                                    projectile.setScaleX(0.5f); // Set X scale factor as desired (e.g., 0.5 for half size)
                                    projectile.setScaleY(0.5f);
                                } else if (imageView.getTag().equals(R.drawable.cannontower)) {
                                    projectileResource = R.drawable.cannonshot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1055, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                                    projectile.setScaleX(0.2f); // Set X scale factor as desired (e.g., 0.5 for half size)
                                    projectile.setScaleY(0.2f);
                                } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
                                    projectileResource = R.drawable.killershot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1150, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 550, enemyY - 440);
                                    projectile.setScaleX(0.1f); // Set X scale factor as desired (e.g., 0.5 for half size)
                                    projectile.setScaleY(0.1f);
                                } else if (imageView.getTag().equals(R.drawable.golgitower)) {
                                    projectileResource = 0;
                                    isInfiniteAttack = true;
                                    handleGolgiAttack(containerLayout, enemiesInWave, towerX, towerY);
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
                                            hasShot = true;
                                            isAnimating = false;
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
                deleteEnemy(targetEnemy); // this deletes the closest enemy within range
                targetEnemy = null;
            }
        }
    }
    private void handleGolgiAttack(FrameLayout containerLayout, List<Enemy> enemiesInWave, float towerX, float towerY) {
        if (isInfiniteAttack) {
            // Simulating almost instant attack speed behavior with a cooldown
            isInfiniteAttack = false;

            final Handler golgiAttackHandler = new Handler();
            Runnable golgiAttackRunnable = new Runnable() {
                @Override
                public void run() {
                    doSplashDamage(towerX, towerY, attackRange, enemiesInWave, false);
                    startGolgiCooldown();
                }
            };
            golgiAttackHandler.postDelayed(golgiAttackRunnable,(long) GOLGI_ATTACK_DURATION);
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
            },(long) GOLGI_COOLDOWN_DURATION);
        }
    }
    private void doSplashDamage(float centerX, float centerY, float damageRadius, List<Enemy> enemiesInWave, boolean isCannon) {
        if(isCannon) {
            for (Enemy enemy : getAllEnemiesInRadius(centerX, centerY, damageRadius, enemiesInWave)) {
                //enemy.takeDamage(50);
                deleteEnemy(enemy);
            }
        }
        else {
            for (Enemy enemy : getAllEnemiesInRadius(centerX, centerY, damageRadius, enemiesInWave)) {
                deleteEnemy(enemy);
            }
        }


    }
    private List<Enemy> getAllEnemiesInRadius(float centerX, float centerY, float damageRadius,List<Enemy> enemiesInWave) {
        List<Enemy> enemiesInRadius = new ArrayList<>();

        // Loop through all enemies to find those within the damage radius
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
            //add to here so that the tower shoots something at the targeted enemy
            containerLayout.removeView(enemyImageView);
            mainActivity.deleteEnemyView(enemy);
            mainActivity.addGold(5);
        }
    }

    private void startCooldown() {
        if (!isOnCooldown) {
            isOnCooldown = true;
            // Cooldown period
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


}

