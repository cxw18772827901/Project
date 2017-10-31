package com.dave.project.wedjet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 嵌套在ListView中的GridView会存在只显示一行的问题,故需要重写onMeasure方法来解决问题
 * Created by Dave on 2015/11/21.
 */
public class ListGridView extends GridView {
    public ListGridView(Context context) {
        super(context);
    }

    public ListGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;  //禁止GridView滑动
        }
        return super.dispatchTouchEvent(ev);
    }
}
