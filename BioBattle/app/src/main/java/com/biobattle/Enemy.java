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
        return currentX;
    }

    public float getY() {
        return currentY;
    }
    public void startPath(ImageView enemyImageView, float width, float height) {
        float offset = (float) (height * 0.08);
        enemyImageView.setY(offset);
        AnimatorSet path = new AnimatorSet();
        ObjectAnimator first = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) 0 - offset, (float) (width * 0.14f) - offset);
        first.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentX = enemyImageView.getX(); // Update X-coordinate during animation
            }
        });

        ObjectAnimator second = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.16) - offset, (float) (height * 0.845) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator third = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.14) - offset, (float) (width * 0.32) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getX();
            }
        });
        ObjectAnimator fourth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.845) - offset, (float) (height * 0.175) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator fifth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.32) - offset, (float) (width * 0.71) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator sixth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.175) - offset, (float) (height * 0.505) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator seventh = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.71) - offset, (float) (width * 0.49) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator eighth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.505) - offset, (float) (height * 0.84) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator ninth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.49) - offset, (float) (width * 0.708) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });
        ObjectAnimator tenth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.84) - offset, (float) (height) - offset);
        second.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentY = enemyImageView.getY(); // Update Y-coordinate during animation
            }
        });

        path.playSequentially(
                first,
                second,
                third,
                fourth,
                fifth,
                sixth,
                seventh,
                eighth,
                ninth,
                tenth
        );
        path.setInterpolator(new LinearInterpolator());
        path.setDuration(3500);
        path.start();
    }
}
