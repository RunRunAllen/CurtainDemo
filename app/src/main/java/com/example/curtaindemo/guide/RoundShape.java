package com.example.curtaindemo.guide;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RoundShape implements Shape {

    private float radius;

    public RoundShape(float radius) {
        this.radius = radius;
    }

    @Override
    public void drawShape(Canvas canvas, Paint paint, HollowInfo info) {
        canvas.drawRoundRect(new RectF(info.targetBound.left, info.targetBound.top, info.targetBound.right
                , info.targetBound.bottom), radius, radius, paint);
    }
}
