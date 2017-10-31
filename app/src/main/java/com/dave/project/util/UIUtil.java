package com.dave.project.util;


import android.util.DisplayMetrics;

import com.dave.project.base.BaseApplication;


/**
 * 提供UI操作 / 布局 工具类
 * <p>
 * Created by Sky on 2015/09/16
 */
public class UIUtil {
    /**
     * dip 转 px
     */
    public static int dip2px(int dip) {
        DisplayMetrics metrics = BaseApplication.getContext().getResources().getDisplayMetrics();
        float density = metrics.density;
        return (int) (dip * density + 0.5f);
    }

    /**
     * px 转 dip
     */
    public static int px2dip(int px) {
        // dp = px / denisity
        DisplayMetrics metrics = BaseApplication.getContext().getResources().getDisplayMetrics();
        float density = metrics.density;
        return (int) (px / density + 0.5f);
    }

}

