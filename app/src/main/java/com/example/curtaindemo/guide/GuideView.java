package com.example.curtaindemo.guide;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import java.util.Map;

public class GuideView extends View {
    private HollowInfo[] mHollows;
    private Map<HollowInfo, HollowInfo> mPositionCache;

    private int mCurtainColor = 0x88000000;

    private Paint mPaint;

    public GuideView(Context context) {
        super(context, null);
        init();
    }

    private void init() {
        mPaint = new Paint(ANTI_ALIAS_FLAG);
        mPositionCache = new ArrayMap<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count;
        //开启一个新的图层  save？
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            count = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        } else {
            count = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        }
        drawBackGround(canvas);
        drawHollowFields(canvas);
        canvas.restoreToCount(count);
    }

    public void setCurtainColor(int color) {
        this.mCurtainColor = color;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getScreenWidth(getContext()), getScreenHeight(getContext()));
    }

    public void setHollowInfo(@NonNull SparseArray<HollowInfo> hollows) {
        HollowInfo[] tobeDraw = new HollowInfo[hollows.size()];
        for (int i = 0; i < hollows.size(); i++) {
            tobeDraw[i] = hollows.valueAt(i);
        }
        setHollowInfo(tobeDraw);
    }

    public void setHollowInfo(@NonNull HollowInfo... hollows) {
        this.mHollows = hollows;
        postInvalidate();
    }

    /**
     * 绘制半透明背景
     */
    private void drawBackGround(Canvas canvas) {
        mPaint.setXfermode(null);
        mPaint.setColor(mCurtainColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

    /**
     * 绘制透明区域
     */
    private void drawHollowFields(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        //重叠区域透明化
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        for (HollowInfo mHollow : mHollows) {
            drawSingleHollow(mHollow, canvas);
        }
    }

    /**
     * 绘制镂空区域
     */
    private void drawSingleHollow(HollowInfo info, Canvas canvas) {
        if (mHollows.length <= 0) {
            return;
        }
        HollowInfo fromCache = mPositionCache.get(info);
        if (null != fromCache) {
            realDrawHollows(fromCache, canvas);
            return;
        }
        info.targetBound = new Rect();
        info.targetView.getDrawingRect(info.targetBound);
        int[] viewLocation = new int[2];
        info.targetView.getLocationOnScreen(viewLocation);
        info.targetBound.left = viewLocation[0];
        info.targetBound.top = viewLocation[1];
        info.targetBound.right += info.targetBound.left;
        info.targetBound.bottom += info.targetBound.top;
        setTheBoundPadding(info);
        //设置偏移量
        if (info.getOffset((HollowInfo.VERTICAL)) > 0) {
            info.targetBound.top += info.getOffset(HollowInfo.VERTICAL);
            info.targetBound.bottom += info.getOffset(HollowInfo.VERTICAL);
        }
        if (info.getOffset(HollowInfo.HORIZONTAL) > 0) {
            info.targetBound.right += info.getOffset(HollowInfo.HORIZONTAL);
            info.targetBound.left += info.getOffset(HollowInfo.HORIZONTAL);
        }
        //status bar 高度处理
        info.targetBound.top -= getStatusBarHeight(getContext());
        info.targetBound.bottom -= getStatusBarHeight(getContext());
        realDrawHollows(info, canvas);
        mPositionCache.put(info, info);
    }

    private void setTheBoundPadding(HollowInfo info) {
        Padding padding = info.padding;
        if (null == padding) {
            return;
        }
        boolean isAllPadding = padding.isAll();
        int allPadding = padding.getSizeByDirection(Padding.ALL);
        Rect bound = info.targetBound;
        bound.left -= isAllPadding ? allPadding : padding.getSizeByDirection(Padding.LEFT);
        bound.top -= isAllPadding ? allPadding : padding.getSizeByDirection(Padding.TOP);
        bound.right += isAllPadding ? allPadding : padding.getSizeByDirection(Padding.RIGHT);
        bound.bottom += isAllPadding ? allPadding : padding.getSizeByDirection(Padding.BOTTOM);
    }

    /**
     * 绘制高亮区域
     */
    private void realDrawHollows(HollowInfo info, Canvas canvas) {
        if (!drawHollowSpaceIfMatched(info, canvas)) {
            //没有匹配，直接画矩形框
            canvas.drawRect(info.targetBound, mPaint);
        }
    }

    private boolean drawHollowSpaceIfMatched(HollowInfo info, Canvas canvas) {
        //自定义shape匹配
        if (null != info.shape) {
            info.shape.drawShape(canvas, mPaint, info);
            return true;
        }
        //TODO:可扩展
        return false;
    }

    private int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public HollowInfo[] getHollows() {
        return mHollows;
    }
}
