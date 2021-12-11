package com.example.curtaindemo.guide;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.curtaindemo.R;

public class FullScreenDialog extends Dialog implements IGuide {
    private static final int GUIDE_ID = 0x3;
    private static final int MAX_CHILD_COUNT = 2;
    private GuideLayer.Param param;
    private int topLayoutRes = 0;
    private GuideView guideView;
    private float downX;
    private float downY;
    private int touchSlop;

    public FullScreenDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static FullScreenDialog newInstance(GuideLayer.Param param) {
        FullScreenDialog dialog = new FullScreenDialog(param.activity, R.style.TransparentDialog);
        dialog.setParam(param);
        dialog.setOwnerActivity((Activity) param.activity);
        dialog.setCanceledOnTouchOutside(param.cancelBackPressed);
        dialog.setTopViewRes(param.topLayoutRes);
        GuideView guideView = new GuideView(param.activity);
        guideView.setCurtainColor(param.curtainColor);
        SparseArray<HollowInfo> hollows = param.hollows;
        HollowInfo[] infos = new HollowInfo[hollows.size()];
        for (int i = 0; i < hollows.size(); i++) {
            infos[i] = hollows.valueAt(i);
        }
        guideView.setHollowInfo(infos);
        dialog.setGuideView(guideView);
        return dialog;
    }

    public FullScreenDialog init() {
        touchSlop = ViewConfiguration.get(param.activity).getScaledTouchSlop();
        Window window = getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
        guideView.setId(GUIDE_ID);
        FrameLayout contentView = new FrameLayout(guideView.getContext());
        contentView.addView(guideView);
        if (topLayoutRes != 0) {
            if (contentView.getChildCount() == MAX_CHILD_COUNT) {
                contentView.removeViewAt(1);
            }
            LayoutInflater.from(contentView.getContext()).inflate(topLayoutRes, contentView, true);
        }
        setGuideData(contentView);
        viewClick(contentView);
        setContentView(contentView);
        dismissDelay(param.delayMillis);
        return this;
    }

    /**
     * 数据源回调处理
     */
    public void setGuideData(ViewGroup contentView) {
        if (null != contentView && param.onDataProviderListener != null) {
            param.onDataProviderListener.provideData(contentView);
        }
    }

    /**
     * 事件处理
     */
    public void viewClick(ViewGroup contentView) {
        SparseArray<OnViewInTopClickListener<IGuide>> listeners = param.topViewOnClickListeners;
        int onClickListenersSize = listeners.size();
        for (int i = 0; i < onClickListenersSize; i++) {
            int idRes = listeners.keyAt(i);
            final OnViewInTopClickListener<IGuide> listener = listeners.valueAt(i);
            View view = contentView.findViewById(idRes);
            if (null != view) {
                view.setOnClickListener(v -> {
                    if (null != listener) {
                        listener.onClick(v, FullScreenDialog.this);
                    }
                });
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (isInBoundSide(ev)) {
            dismissDelay(800);
            return false;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    public void dismissDelay(long delayMillis) {
        if (guideView != null) {
            guideView.postDelayed(this::dismiss, delayMillis);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean isInterceptAll = param.isInterceptTarget;
        if (!isInterceptAll) {
            if (isInBoundSide(event))
                return super.onTouchEvent(event) || tryHandleByActivity(event);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否在高亮区域内
     */
    private boolean isInBoundSide(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            int upY = (int) event.getY();
            int upX = (int) event.getX();
            if (Math.abs(upX - downX) < touchSlop && Math.abs(upY - downY) < touchSlop) {
                if (guideView != null) {
                    HollowInfo[] hollows = guideView.getHollows();
                    if (hollows != null && hollows.length > 0) {
                        for (HollowInfo info : hollows) {
                            Rect rect = info.targetBound;
                            if (rect.contains(upX, upY)) {
                                info.targetView.performClick();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setGuideView(GuideView guideView) {
        this.guideView = guideView;
    }

    private void setTopViewRes(int topLayoutRes) {
        this.topLayoutRes = topLayoutRes;
    }

    public void setParam(GuideLayer.Param param) {
        this.param = param;
    }

    private boolean tryHandleByActivity(MotionEvent ev) {
        Activity activity = getOwnerActivity();
        if (activity != null) {
            return activity.dispatchTouchEvent(ev);
        }
        return false;
    }

    public void dismissGuide() {
        dismiss();
    }
}
