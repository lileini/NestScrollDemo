package com.example.administrator.nestscrolldemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class NestScrollParent extends LinearLayout implements NestedScrollingParent {
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private static final String TAG = "NestScrollParent";
    private View nestChildView;
    private View imgView;
    private View tvView;
    private int tvHeight;
    private int imgHeight;

    public NestScrollParent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestScrollParent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public NestScrollParent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void init() {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof NestedScrollingChild) {
                nestChildView = childView;
            }
        }
        imgView = getChildAt(0);
        tvView = getChildAt(1);
        tvView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i(TAG, "addOnGlobalLayoutListener: tvHeight: " + tvHeight);
                tvHeight = tvView.getMeasuredHeight();
            }
        });
        imgView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i(TAG, "addOnGlobalLayoutListener: imgHeight: " + imgHeight);
                imgHeight = imgView.getMeasuredHeight();
            }
        });
    }

    /**
     * 回调开始滑动
     *
     * @param child            改view的子类
     * @param target           支持嵌套滑动的view
     * @param nestedScrollAxes 滑动方向
     * @return 是否支持嵌套滑动
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (target instanceof NestedScrollingChild) {
            return true;
        }
        return false;
    }

    /**
     * @param child
     * @param target
     * @param axes
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        mNestedScrollingParentHelper.onStopNestedScroll(child);
    }

    /**
     * 这里传过来x,y轴上的滑动距离，并让target view 处理滑动（即Consumed[1] y轴需要滑动的距离），然后targetview
     * 根据consumed[1]来处理y轴的滑动距离
     *
     * @param target
     * @param dx
     * @param dy
     * @param consumed consumed[0] x轴消费的距离
     *                 consumed[1] y轴消费的距离
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.i(TAG, "dy: " + dy + ",imgHeight: " + imgHeight);
        if (showHeader(dy)) {
            consumed[1] = dy;
            scrollBy(0, -dy);
            Log.i(TAG, "showHeader consumed: " + consumed[1]);

        }else if(hideHeader(dy)){
            consumed[1] = dy; //传递给嵌套的子类y轴上父类消费的距离
            consumed[0] = imgHeight;//通过此吧imgView的高度传递给嵌套的子类
            scrollBy(0, -dy);
            Log.i(TAG, "hideHeader consumed: " + consumed[1]);
        }
        Log.i(TAG, "getScrollY(): " + getScrollY()+",nestChildView.getScrollY(): "+nestChildView.getScrollY());
        Log.i(TAG, "onNestedPreScroll: " + "dx: " + dx + ",dy: " + dy);
    }

    private boolean showHeader(int dy) {
        if (dy > 0 && nestChildView.getScrollY() == 0 && getScrollY() > 0) {
                return true;
        }
        return false;
    }

    private boolean hideHeader(int dy) {
        if (dy < 0 && getScrollY() < imgHeight) {
            return true;
        }
        return false;
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y>imgHeight) //处理一次滑动超出imgView的高度
            y = imgHeight;
        if (y<0)//处理下滑超出childView范围
            y = 0;
        super.scrollTo(x, y);
    }

    /**
     * 这里主要处理dxConsumed dyConsumed 这两数据
     * 后于child滚动
     *
     * @param target       能嵌套滑动的子类
     * @param dxConsumed   x轴上消费的滑动距离
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    /**
     * 父View是否放嵌套fling
     *
     * @param velocityX
     * @param velocityY
     * @param consumed
     * @return
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * 获取嵌套滚动的 坐标轴 x/y轴 SCROLL_AXIS_HORIZONTAL/SCROLL_AXIS_VERTICAL
     *
     * @return
     */
    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }
}
