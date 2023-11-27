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
    public Enemy(ImageView imageView) {
        this.enemyImageView = imageView;
    }

    public void startPath(float width, float height) {

        AnimatorSet path = new AnimatorSet();
        ObjectAnimator first = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) 0, (float) (width * 0.14f));
        ObjectAnimator second = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.16), (float) (height * 0.845));
        ObjectAnimator third = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.14), (float) (width * 0.32));
        ObjectAnimator fourth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.845), (float) (height * 0.175));
        ObjectAnimator fifth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.32), (float) (width * 0.71));
        ObjectAnimator sixth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.175), (float) (height * 0.505));
        ObjectAnimator seventh = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.71), (float) (width * 0.49));
        ObjectAnimator eighth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.505), (float) (height * 0.84));
        ObjectAnimator ninth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_X, (float) (width * 0.49), (float) (width * 0.708));
        ObjectAnimator tenth = ObjectAnimator.ofFloat(enemyImageView, View.TRANSLATION_Y, (float) (height * 0.84), (float) (height));
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
        path.setDuration(3000);
        path.start();
    }
}
