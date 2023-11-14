package com.biobattle;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.ImageView;

public class Enemy {

    public static void Path(ImageView view){
        ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 200);
        moveAnimator.setDuration(500);
        moveAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            view.setTranslationX(500);

        });
        moveAnimator.start();
    }
}
