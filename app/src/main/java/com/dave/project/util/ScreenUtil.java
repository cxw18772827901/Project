package com.dave.project.util;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import com.dave.project.base.BaseApplication;


/**
 * 获取屏幕尺寸工具类
 * Created by Dave on 2016/12/15.
 */

public class ScreenUtil {
    public static int getScreenWid() {
        WindowManager wm = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        return defaultDisplay.getWidth();
    }

    public static int getScreenHei() {
        WindowManager wm = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        return defaultDisplay.getHeight();
    }
}
