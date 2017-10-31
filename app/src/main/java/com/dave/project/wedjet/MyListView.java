package com.dave.project.wedjet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 嵌套内部的的listview
 * Created by Dave on 2015/11/9.
 */
public class MyListView extends ListView {

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
