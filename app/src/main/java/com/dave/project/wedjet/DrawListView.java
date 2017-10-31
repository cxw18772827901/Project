package com.dave.project.wedjet;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Scroller;

import com.dave.project.util.UIUtil;


/**
 * 头图拉伸与回缩的原理
 * Created by Dave on 2015/11/12.
 */
public class DrawListView extends ListView {
    public DrawListView(Context context, AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawListView(Context context) {
        super(context);
    }

    private int ivHeight;
    private View headIv = null;

    public void setView(View headIv) {
        this.headIv = headIv;
        ivHeight = UIUtil.dip2px(200);
    }

    private float lastEvY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (scroller != null && !scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 只关心上送的效果：
                // 两次触摸事件的y的差 < 0
                // ImageView的高度大于原始高度
                float deltaY = ev.getY() - lastEvY;
                if (deltaY < 0 && headIv != null && headIv.getHeight() > ivHeight) {
                    // 减小headIv的高度
                    int y = (int) (Math.abs(deltaY) / dampFactor);
                    int hei1 = headIv.getLayoutParams().height;
                    headIv.getLayoutParams().height = hei1 - y;
                    headIv.requestLayout();
                    // 在头图回到原来的高度之前让listview一直显示头图,不会发生滚动
                    setSelection(0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (headIv != null && headIv.getHeight() > ivHeight) {
                    resetHead();
                }
                break;
        }
        lastEvY = ev.getY();
        return super.onTouchEvent(ev);
    }

    private void resetHead() {
        // 2 scroller 实现
        scroller = new Scroller(getContext());
        scroller.startScroll(0, headIv.getHeight(), 0, ivHeight - headIv.getHeight(), 400);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    //回滚动画
    @Override
    public void computeScroll() {
        if (scroller != null && scroller.computeScrollOffset()) {
            headIv.getLayoutParams().height = scroller.getCurrY();
            headIv.requestLayout();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private Scroller scroller;

    //优化拉伸体验,设置阻尼效果,系数越大,用户拉伸越困难
    float dampFactor = 3;

    // 处理下拉的时候放大头图
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        // isTouchEvent 如果是手指导致的，就是true，如果惯性导致的就是false
        // deltaY
        // 在顶部下拉的时候 ： isTouchEvent = true -- 负值
        // 在顶部下拉的时候 ： isTouchEvent = false -- 正值
        // 在底部上拉的时候 ： isTouchEvent = true -- 正值
        // 在底部上拉的时候 ： isTouchEvent = false -- 负值
        // maxOverScrollY 如果不是0, 就可以实现苹果的效果
        if (isTouchEvent && deltaY < 0 && headIv != null) { // 表示下拉了，
            // 改变headIv的高度
            headIv.getLayoutParams().height = (int) (headIv.getLayoutParams().height + Math
                    .abs(deltaY) / dampFactor);
            headIv.requestLayout();
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }
}
