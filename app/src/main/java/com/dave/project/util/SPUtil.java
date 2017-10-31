package com.dave.project.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dave.project.base.BaseApplication;


/**
 * 首选项存储工具类
 * Created by Dave on 15/10/25.
 */
public class SPUtil {
    /**
     * 获取SharedPreferences
     *
     * @return
     */
    public static SharedPreferences getSharedPreference() {
        return BaseApplication.getContext().getSharedPreferences("spdb", Context.MODE_PRIVATE);
    }

    /**
     * 从SharedPreferences获取String值
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return getSharedPreference().getString(key, "");
    }

    /**
     * 从SharedPreferences获取int值
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return getSharedPreference().getInt(key, 0);
    }

    /**
     * 从SharedPreferences获取boolean值
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        return getSharedPreference().getBoolean(key, false);
    }

    /**
     * 从SharedPreferences获取long值
     *
     * @param key
     * @return
     */
    public static long getLong(String key) {
        return getSharedPreference().getLong(key, -1);
    }

    /**
     * 存储数据到SharedPreferences中,有String,Integer,Boolean,Long,可扩展
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        Editor editor = getSharedPreference().edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }
        editor.apply();
    }

}
