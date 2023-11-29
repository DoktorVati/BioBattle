package com.biobattle;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Enemy {

    private ImageView enemyImageView;
    private float currentX;
    private float currentY;
    private float x;
    private float y;
    private int width;
    private int height;
    public Enemy(ImageView imageView) {
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
    public void startPath(ImageView enemyImageView, float width, float height) {
        float offset = 0.08f * height;

        enemyImageView.setY(offset);

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

        ObjectAnimator tenth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, 0.84f * height - offset, height - offset);
        tenth.addUpdateListener(animation -> {
            currentX = enemyImageView.getX();
            currentY = enemyImageView.getY();
        });

        AnimatorSet pathSet = new AnimatorSet();
        pathSet.playSequentially(
                first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth
        );
        pathSet.setInterpolator(new LinearInterpolator());
        pathSet.setDuration(3500);
        pathSet.start();
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
}
