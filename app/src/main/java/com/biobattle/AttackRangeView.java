package com.biobattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

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
        paint.setColor(Color.TRANSPARENT); // Set color as transparent initially
        paint.setStyle(Paint.Style.FILL_AND_STROKE); // Fill and stroke
        paint.setStrokeWidth(5); // Change stroke width as needed
        paint.setAlpha(50); // Set alpha to control transparency (0 to 255)
    }

    public void setCenter(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }
    public void setColor(Context context, int colorResource) {
        int color = ContextCompat.getColor(context, colorResource);
        this.paint.setColor(color);
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
