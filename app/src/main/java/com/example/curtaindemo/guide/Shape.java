package com.example.curtaindemo.guide;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface Shape {

    /**
     * 绘制shape
     */
    void drawShape(Canvas canvas, Paint paint, HollowInfo info);

}
