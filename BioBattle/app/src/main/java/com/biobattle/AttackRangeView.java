package com.biobattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AttackRangeView extends View {

    private Paint paint;
    private float centerX;
    private float centerY;
    public float radius;

    public AttackRangeView(Context context) {
        super(context);
        init();
    }

    public AttackRangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AttackRangeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.DKGRAY); // Change color as needed
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5); // Change stroke width as needed
    }

    public void setCenter(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }
}
