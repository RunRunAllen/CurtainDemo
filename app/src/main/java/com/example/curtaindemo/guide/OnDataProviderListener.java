package com.example.curtaindemo.guide;

import android.view.ViewGroup;

/**
 * 数据源回调
 */
public interface OnDataProviderListener<T> {

    void provideData(ViewGroup contentView);
}
