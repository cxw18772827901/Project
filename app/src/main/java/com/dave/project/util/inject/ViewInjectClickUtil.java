package com.dave.project.util.inject;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.dave.project.ui.base.MyException;

import java.lang.reflect.Method;

/**
 * 点击事件的注解
 * Created by Dave on 2016/12/26.
 */
public class ViewInjectClickUtil {
    /**
     * 点击事件中的注解
     *
     * @param activity activity.this
     */
    public static void initInActivity(final Activity activity) {
        try {
            Method[] methods = activity.getClass().getDeclaredMethods();
            if (null != methods && methods.length > 0) {
                for (final Method method : methods) {
                    ViewInjectClick viewInjectClick = method.getAnnotation(ViewInjectClick.class);//返回ViewInject中声明的所有注解
                    if (null != viewInjectClick) {
                        int[] viewIds = viewInjectClick.id();
                        for (int viewId : viewIds) {
                            method.setAccessible(true);//暴力反射private
                            final View view = activity.findViewById(viewId);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        method.invoke(activity, view);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("注解", "失败");
            throw new MyException("反射注解失败");
        }
    }

}
