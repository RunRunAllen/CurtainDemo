package com.example.curtaindemo.guide;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 镂空View 实体
 */
public class HollowInfo {

    private static final int SHIFT = 30;

    private static final int MODE_MASK = 0x3 << SHIFT;

    public static final int VERTICAL = 1 << SHIFT;

    public static final int HORIZONTAL = 2 << SHIFT;

    /**
     * The mask of offset and the direction
     */
    private int mOffsetMask;

    @IntDef(flag = true,
            value = {VERTICAL, HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface direction {
    }

    /**
     * The target view to be highlight
     */
    public View targetView;

    /**
     * The Area of the highlight field
     */
    public Rect targetBound;

    /**
     * The padding of the highlight field
     */
    public Padding padding;

    /**
     * The shape of the highlight field
     */
    public Shape shape;


    /**
     * set the  highlight shape
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public HollowInfo(View targetView) {
        this.targetView = targetView;
    }

    /**
     * set the offset of the highlight field
     */
    public void setOffset(int offset, @direction int direction) {
        this.mOffsetMask = (offset & ~MODE_MASK) | (direction & MODE_MASK);
    }

    /**
     * get the offset of the target direction
     */
    public int getOffset(@direction int direction) {
        if ((mOffsetMask & MODE_MASK) == direction) {
            return mOffsetMask & ~MODE_MASK;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HollowInfo) {
            HollowInfo target = (HollowInfo) obj;
            return target.targetView == targetView;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
