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

import java.util.ArrayList;

public class Enemy extends MainActivity{
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
    private boolean isDead;

    private boolean isBoss;

    private float offsetY;

    private float offsetX;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public Enemy(ImageView imageView, int enemyHealth, int enemySpeed) {
        this.speed = enemySpeed;
        this.health = enemyHealth;
        this.enemyImageView = imageView;
        // Set initial positions based on imageView's current position
        this.currentX = imageView.getX();
        this.currentY = imageView.getY();
        this.isDead = false;
        this.isBoss = false;

    }
    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        //float offset = 0.08f * height;

        if (checkIsBoss()){
            //offsetY = 0.20f * height;
            //offsetX = 0.20f * width;
            //offsetY = 225;
            //offsetX = 175;
            offsetY = 0.20f * height;
            offsetX = 0.15f * height;
        }
        else {
            offsetY = 0.08f * height;
            offsetX = offsetY;
        }
        //enemyImageView.setY(offset);
        enemyImageView.setImageResource(R.drawable.invis);
        startAnimation(enemyImageView, animationResource);

        ObjectAnimator first = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0 - offsetX, 0.14f * width - offsetX);
        first.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator second = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.16f * height - offsetY, 0.845f * height - offsetY);
        second.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator third = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.14f * width - offsetX, 0.32f * width - offsetX);
        third.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator fourth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.845f * height - offsetY, 0.175f * height - offsetY);
        fourth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator fifth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.32f * width - offsetX, 0.71f * width - offsetX);
        fifth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator sixth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.175f * height - offsetY, 0.505f * height - offsetY);
        sixth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator seventh = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.71f * width - offsetX, 0.49f * width - offsetX);
        seventh.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator eighth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.505f * height - offsetY, 0.84f * height - offsetY);
        eighth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator ninth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, 0.49f * width - offsetX, 0.708f * width - offsetX);
        ninth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        ObjectAnimator tenth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.84f * height - offsetY, height - offsetY - 1);
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
                    containerLayout.removeView(enemyImageView);
                    mainActivity.deleteEnemyView(Enemy.this);
                    mainActivity.losePlayerHealth((int) (health/ 100));
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
        first.setDuration(speed);
        second.setDuration((speed * (2 + (3/4))));
        third.setDuration(speed * (1 + (3/10)));
        fourth.setDuration(speed * (2 + (3/4)));
        fifth.setDuration(speed * (2 + (85/100)));
        sixth.setDuration(speed * (1 + (35/100)));
        seventh.setDuration(speed * (1 + (70/100)));
        eighth.setDuration(speed * (1 + (35/100)));
        ninth.setDuration(speed * (1 + (70/100)));
        tenth.setDuration(speed/2);
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
        return x - towerX + width / 2;
    }

    public float getYRelativeToTower(float towerY) {
        return y - towerY + height / 2;
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

    public void setBoss(){
        isBoss = true;
    }

    public Boolean checkIsBoss(){
        return isBoss;
    }
}
