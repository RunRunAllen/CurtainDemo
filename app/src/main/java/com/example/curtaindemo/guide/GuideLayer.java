package com.example.curtaindemo.guide;

import android.content.Context;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.jetbrains.annotations.NotNull;

public class GuideLayer {
    Param param;
    private final HollowInfo info;

    public GuideLayer(@NonNull FragmentActivity activity, @NonNull View which) {
        this.param = new Param();
        param.activity = activity;
        param.hollows = new SparseArray<>();
        info = getHollowInfo(which);
    }

    public GuideLayer withPadding(int paddingSize) {
        return withPadding(Padding.all(paddingSize));
    }

    public GuideLayer withPadding(Padding padding) {
        info.padding = padding;
        return this;
    }

    public GuideLayer withShape(Shape shape) {
        info.setShape(shape);
        return this;
    }

    public GuideLayer withSize(int width, int height) {
        info.targetBound = new Rect(0, 0, width, height);
        return this;
    }

    public GuideLayer withOffset(int offset, @HollowInfo.direction int direction) {
        info.setOffset(offset, direction);
        return this;
    }

    public GuideLayer setTopView(@LayoutRes int layoutId) {
        this.param.topLayoutRes = layoutId;
        return this;
    }

    public GuideLayer setCurtainColor(int color) {
        this.param.curtainColor = color;
        return this;
    }

    public GuideLayer setCurtainColorRes(@ColorRes int color) {
        this.param.curtainColor = color;
        return this;
    }

    public GuideLayer setInterceptTargetView(boolean isInterceptTargetView) {
        this.param.isInterceptTarget = isInterceptTargetView;
        return this;
    }

    @NotNull
    public GuideLayer setDismissTime(long delayMillis) {
        this.param.delayMillis = delayMillis;
        return this;
    }

    public GuideLayer setGuideData(@NotNull OnDataProviderListener<IGuide> onDataProviderListener) {
        this.param.onDataProviderListener = onDataProviderListener;
        return this;
    }

    public GuideLayer addOnTopViewClickListener(@IdRes int viewId, OnViewInTopClickListener<IGuide> onClickListener) {
        this.param.topViewOnClickListeners.append(viewId, onClickListener);
        return this;
    }

    @MainThread
    public void show() {
        SparseArray<HollowInfo> hollows = param.hollows;
        if (hollows == null || hollows.size() == 0) {
            return;
        }
        View checkView = hollows.valueAt(0).targetView;
        if (checkView.getWidth() == 0) {
            checkView.post(this::show);
            return;
        }
        FullScreenDialog.newInstance(param).init().show();
    }

    private HollowInfo getHollowInfo(View which) {
        SparseArray<HollowInfo> hollows = param.hollows;
        HollowInfo info = hollows.get(which.hashCode());
        if (null == info) {
            info = new HollowInfo(which);
            info.targetView = which;
            hollows.append(which.hashCode(), info);
        }
        return info;
    }

    public static class Param {

        long delayMillis;
        Context activity;
        SparseArray<HollowInfo> hollows;
        //弹层View
        int topLayoutRes;
        //点击外部取消
        boolean cancelBackPressed = true;
        // 高亮的目标View仍然可以响应touch事件,默认为true
        boolean isInterceptTarget = true;
        int curtainColor = 0x00000000;
        //弹层view事件监听
        SparseArray<OnViewInTopClickListener<IGuide>> topViewOnClickListeners = new SparseArray<>();
        //数据源回调
        OnDataProviderListener onDataProviderListener;
    }

}
