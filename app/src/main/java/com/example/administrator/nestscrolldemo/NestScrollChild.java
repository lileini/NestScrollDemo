package com.example.administrator.nestscrolldemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.Size;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class NestScrollChild extends LinearLayout implements NestedScrollingChild {
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private float mLastY;
    private int showHeight;
    private static final String TAG = "NestScrollChild";
    private int[] offests = new int[2];
    private int[] consumeds = new int[2];
    private int imgHeigt;

    public NestScrollChild(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    public NestScrollChild(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取该控件显示的高度
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        showHeight = getMeasuredHeight();


        //获取该控件全部展示的高度
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 设置嵌套滑动是否可用
     *
     * @param enabled
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 开始嵌套滚动
     *
     * @param axes 表示方向 ViewCompat.SCROLL_AXIS_VERTICAL | ViewCompat.SCROLL_AXIS_HORIZONTAL
     * @return
     */
    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    /**
     * 停止潜逃滚动
     */
    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    /**
     * 是否有嵌套滚动的父类
     *
     * @return
     */
    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    /**
     * 在滑动之后调用
     *
     * @param dxConsumed     x轴上被消费的距离
     * @param dyConsumed
     * @param dxUnconsumed   x轴上未被消费的距离
     * @param dyUnconsumed
     * @param offsetInWindow view的移动距离
     * @return
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    /**
     * 一般在处理滑动之前调用，在ontouch中计算出滑动距离，然后调用该方法，判断支持嵌套的父类是否处理滑动事件
     *
     * @param dx             x轴上的滑动距离，相对于上次事件，不是相对于down事件的距离
     * @param dy
     * @param consumed
     * @param offsetInWindow 支持嵌套父view消费事件后导致本view滑动的距离
     * @return 支持的嵌套父类是否处理了滑动事件
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable @Size(value = 2) int[] consumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int daltY = (int) (event.getRawY() - mLastY);
                mLastY = event.getRawY();
                //这里判断是否是垂直滑动，并通过dispatchNestedPreScroll在滑动前计算出父类在y轴上的滑动距离，获取需要隐藏的view的高度
                if (startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL) && dispatchNestedPreScroll(0, daltY, consumeds, offests)) {
                    int remian_dy = daltY - consumeds[1];
                    imgHeigt = consumeds[0];
//                    Log.i(TAG, "daltY: " + daltY + ",consumeds[1]: " + consumeds[1]);
                    if (remian_dy != 0)
                        scrollBy(0, remian_dy);
                } else {
                    scrollBy(0, -daltY);
                }

                break;
        }
        return true;
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        int dy = getMeasuredHeight() - showHeight - imgHeigt;//这里计算嵌套的view自身需要滑动的最大距离
        //                Log.i(TAG, "y: " + y + ",dy: " + dy + ",getMeasuredHeight: " + getMeasuredHeight() + ",showHeight: " + showHeight);
        if (y < 0)
            y = 0;//当单次滑动超过距离时进行校正
        if (y > dy)
            y = dy;//当单次滑动超过距离时进行校正
        if (y <= dy && y >= 0) {
            super.scrollTo(x, y);

        }
    }
}
