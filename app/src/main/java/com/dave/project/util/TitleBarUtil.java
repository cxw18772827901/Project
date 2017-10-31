package com.dave.project.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 沉浸式工具类
 * Created by Dave on 2017/1/10.
 */
public class TitleBarUtil {
    //在setContentView之后调用即可
    public static void initPrisiveBar(Activity context, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(context, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(context);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(false);//不管navagationBar
            // 自定义颜色
//            tintManager.setTintColor(Color.parseColor("#1bad88"));//注意此方法是给statuesBar和navagationBar一起着色
            tintManager.setStatusBarTintColor(context.getResources().getColor(colorId));//只修改statuesBar颜色
        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity context, boolean on) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;//winParams.flags=winParams.flags|bits;
        } else {
            winParams.flags &= ~bits;// winParams.flags= winParams.flags&(~bits);
        }
        win.setAttributes(winParams);
    }

}
