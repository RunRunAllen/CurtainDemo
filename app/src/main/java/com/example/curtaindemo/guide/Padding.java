package com.example.curtaindemo.guide;


import android.util.SparseArray;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Padding {

    static final int ALL = 1;

    static final int LEFT = 1 << 1;

    static final int TOP = 2 << 1;

    static final int RIGHT = 3 << 1;

    static final int BOTTOM = 4 << 1;

    private final SparseArray<Integer> paddingArrays;

    @IntDef(flag = true,
            value = {ALL, LEFT, TOP, RIGHT, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    @interface PaddingDirection {
    }

    /**
     * all the direction
     */
    public static Padding all(int size) {
        return new Padding(size);
    }

    public static Padding only(int left) {
        return Padding.only(left, 0, 0, 0);
    }

    public static Padding only(int left, int top) {
        return Padding.only(left, top, 0, 0);
    }

    public static Padding only(int left, int top, int right) {
        return Padding.only(left, top, right, 0);
    }

    /**
     * build an padding with padding in different directions
     */
    public static Padding only(int left, int top, int right, int bottom) {
        return new Padding()
                .appendPadding(LEFT, left)
                .appendPadding(TOP, top)
                .appendPadding(RIGHT, right)
                .appendPadding(BOTTOM, bottom);
    }

    Padding() {
        this.paddingArrays = new SparseArray<>(4);
    }

    Padding(int size) {
        this.paddingArrays = new SparseArray<>(1);
        paddingArrays.append(ALL, size);
    }

    Padding appendPadding(@PaddingDirection int direction, int size) {
        paddingArrays.append(direction, size);
        return this;
    }

    int getSizeByDirection(@PaddingDirection int direction) {
        return paddingArrays.get(direction, 0);
    }

    boolean isAll() {
        return paddingArrays.size() == 1;
    }

}
