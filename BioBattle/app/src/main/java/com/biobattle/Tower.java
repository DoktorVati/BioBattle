package com.biobattle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

public class Tower {
    private int towerNumber;
    private ImageView imageView;
    private float attackDamage;
    private float attackSpeed;
    private float attackRange;
    private float upgradePercentage = 1.15f;
    private int totalUpgrades = 0;
    private AttackRangeView attackRangeView;
    private boolean isOnCooldown = false;
    public MainActivity mainActivity;
    private TowerScript towerScript;

    private boolean shots;

    private float newAttackRange, newAttackDamage, newAttackSpeed;
    public Tower(ImageView imageView, float attackRange, float attackDamage, float attackSpeed, MainActivity mainActivity, float upgradePercentages)
    {
        this.imageView = imageView;
        this.attackRange = attackRange;
        this.newAttackRange = attackRange;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.mainActivity = mainActivity;
        this.dontrun = false;
        this.upgradePercentage = upgradePercentages;
        this.shots = true;
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
        attackSpeed = (newAttackSpeed /= upgradePercentage);
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
        if (!isOnCooldown && dontrun == false) {
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
                        int projectileResource = R.drawable.cannonshot1; // Default resource ID
                        if(targetEnemy != null && towerScript != null) {
                            ImageView projectile = new ImageView(containerLayout.getContext());
                            float projectileStartX = towerX + imageView.getWidth() / 2 - projectile.getWidth() / 2;
                            float projectileStartY = towerY + imageView.getHeight() / 2 - projectile.getHeight() / 2;

                            projectile.setX(projectileStartX);
                            projectile.setY(projectileStartY);
                            ObjectAnimator projectileAnimatorX = null;
                            ObjectAnimator projectileAnimatorY = null;


                            projectile.setScaleX(0.1f); // Set X scale factor as desired (e.g., 0.5 for half size)
                            projectile.setScaleY(0.1f);
                            if (imageView != null && imageView.getTag() != null) {
                                if (imageView.getTag().equals(R.drawable.simpletower)) {
                                    projectileResource = R.drawable.cannonshot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1050, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                                } else if (imageView.getTag().equals(R.drawable.golgitower)) {
                                    projectileResource = R.drawable.cannonshot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1150, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 550, enemyY - 440);
                                } else if (imageView.getTag().equals(R.drawable.cannontower)) {
                                    projectileResource = R.drawable.cannonshot1;
                                     projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1055, enemyX - 1040);
                                     projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                                } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
                                    projectileResource = R.drawable.cannonshot1;
                                    projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1150, enemyX - 1040);
                                    projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 550, enemyY - 440);
                                }
                            } else {
                                projectileResource = R.drawable.cannonshot1;
                                projectileAnimatorX = ObjectAnimator.ofFloat(projectile, View.X, projectileStartX - 1050, enemyX - 1040);
                                projectileAnimatorY = ObjectAnimator.ofFloat(projectile, View.Y, projectileStartY - 500, enemyY - 440);
                            }
                            projectile.setImageResource(projectileResource);
                            containerLayout.addView(projectile);


                            AnimatorSet projectileAnimation = new AnimatorSet();
                            projectileAnimation.playTogether(projectileAnimatorX, projectileAnimatorY);
                            projectileAnimation.setDuration(50); // Set the duration of the animation (adjust as needed)
                            projectileAnimation.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    // Remove the projectile view after the animation completes
                                    containerLayout.removeView(projectile);
                                    shots = true;
                                }
                            });

                            /*
                            //do something like this to specify attack type per tower
                            if(imageView.getTag().equals(R.drawable.cannonshot1)) {
                                projectileAnimation.start();
                            }
                            */
                            projectileAnimation.start();
                        }
                    }
                }
            }
            if (targetEnemy != null && shots == true) {
                shots = false;
                startCooldown(); // Start the cooldown before deleting the enemy
                deleteEnemy(targetEnemy); // Delete the closest enemy within range
            }
        }
    }


    private float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    private void deleteEnemy(Enemy enemy) {
        ImageView enemyImageView = enemy.getImageView();
        FrameLayout containerLayout = (FrameLayout) enemyImageView.getParent();
        Log.d("EnemyAttempt", "Attempt");

        if (mainActivity == null) {
            Log.d("WaveNull", "Wave object is null");
            return; // Exit the method if wave is null
        }

        if (containerLayout != null && towerScript != null) {
            //add to here so that the tower shoots something at the targeted enemy
            containerLayout.removeView(enemyImageView);
            Log.d("EnemyDestroyed", "WeGotEm");
            mainActivity.deleteEnemyView(enemy);
        }
    }

    private void startCooldown() {
        if (!isOnCooldown) {
            isOnCooldown = true;
            // Cooldown period
            final int cooldownDuration = (int) (attackSpeed / 10); // in milliseconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("CooldownFinished", "cooldowndone");
                    isOnCooldown = false; // Cooldown finished, tower can attack again
                }
            }, cooldownDuration);
        }
    }


}

