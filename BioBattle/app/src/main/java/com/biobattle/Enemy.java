package com.biobattle;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Enemy {
    private final int speed;
    private float health;
    private ImageView enemyImageView;
    private float currentX;
    private float currentY;
    private float x;
    private float y;
    private int width;
    private int height;
    private MainActivity mainActivity;

    private Tower tower;
    private boolean isDead = false;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    //public void setTower(Tower tower) {
        //this.tower = tower;
    //}
    public Enemy(ImageView imageView, int enemyHealth, int enemySpeed) {
        this.speed = enemySpeed;
        this.health = enemyHealth;
        this.enemyImageView = imageView;
        // Set initial positions based on imageView's current position
        this.currentX = imageView.getX();
        this.currentY = imageView.getY();

    }

    public ImageView getImageView()
    {
        return enemyImageView;
    }
    public float getX() {
        return this.currentX;
    }

    public float getY() {
        return this.currentY;
    }
    public void startPath(ImageView enemyImageView, float width, float height, int animationResource) {
        float offset = 0.08f * height;

        enemyImageView.setY(offset);
        enemyImageView.setImageResource(R.drawable.invis);
        startAnimation(enemyImageView, animationResource);

        ObjectAnimator first = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0 - offset, 0.14f * width - offset);
        first.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator second = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.16f * height - offset, 0.845f * height - offset);
        second.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator third = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.14f * width - offset, 0.32f * width - offset);
        third.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator fourth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.845f * height - offset, 0.175f * height - offset);
        fourth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator fifth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.32f * width - offset, 0.71f * width - offset);
        fifth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator sixth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.175f * height - offset, 0.505f * height - offset);
        sixth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator seventh = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.71f * width - offset, 0.49f * width - offset);
        seventh.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator eighth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.505f * height - offset, 0.84f * height - offset);
        eighth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator ninth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.49f * width - offset, 0.708f * width - offset);
        ninth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator tenth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.84f * height - offset, height - offset - 1);
        tenth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });
        tenth.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isDead) {
                    FrameLayout containerLayout = (FrameLayout) enemyImageView.getParent();
                    //containerLayout.removeView(enemyImageView);
                    //mainActivity.deleteEnemyView(Enemy.this);
                    containerLayout.removeView(enemyImageView);
                    mainActivity.deleteEnemyView(Enemy.this);
                    mainActivity.losePlayerHealth();
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        AnimatorSet pathSet = new AnimatorSet();
        pathSet.playSequentially(
                first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth
        );
        pathSet.setInterpolator(new LinearInterpolator());
        pathSet.setDuration(speed);
        pathSet.start();

    }

    public static void startAnimation(ImageView enemyImageView, int animationResource){
        ImageView animatedEnemy = enemyImageView;
        animatedEnemy.setBackgroundResource(animationResource);
        AnimationDrawable enemyAnimation = (AnimationDrawable) animatedEnemy.getBackground();

        enemyAnimation.start();
    }
    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2;
    }
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
    public float getXRelativeToTower(float towerX) {
        return x - towerX + width / 2; // Subtract towerX from enemy's x and add half its width
    }

    public float getYRelativeToTower(float towerY) {
        return y - towerY + height / 2; // Subtract towerY from enemy's y and add half its height
    }
    public void loseHealth(float damage){
        health = health - damage;
    if (health <= 0){
        //if enemy health drops to zero or lower set to dead
        isDead = true;
    }
    }
    public float getHealth(){
        return health;
    }
    public void die(){
        //set isDead Boolean to true for debugging purposes
        isDead = true;
    }
    public Boolean checkIsDead(){
        return isDead;
    }
}
