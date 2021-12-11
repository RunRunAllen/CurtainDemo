package com.example.curtaindemo.guide;

import android.view.View;

public interface OnViewInTopClickListener<T> {

    /**
     * 弹层点击事件
     */
    void onClick(View current, T currentHost);
}
