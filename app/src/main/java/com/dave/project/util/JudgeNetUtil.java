package com.dave.project.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络相关辅助类 / WiFiUtil
 * <p/>
 * Desc:跟网络相关的工具类
 * <p/>
 * Created by Dave on 2015/10/27
 */
public class JudgeNetUtil {


    /**
     * 判断网络连接类型
     * * Created by Dave on 2016/10/12.
     *
     * @param context
     * @return
     */
    public static int judgeNetConnected(Context context) {
        // 获取网络连接管理者
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取可用的网络信息
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return 0;
        }
        // 获取网络类型
        int type = networkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:// 手机网络
                return 1;
            case ConnectivityManager.TYPE_WIFI:  // WIFI网络
                return 2;
            default:                             // 没有网络
                return 3;
        }
    }

    /**
     * 判断网络连接类型
     * * Created by Dave on 2016/10/12.
     *
     * @param context
     * @return
     */
    public static boolean judgeNetIsConnected(Context context) {
        // 获取网络连接管理者
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取可用的网络信息
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        // 获取网络类型
        int type = networkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:// 手机网络
            case ConnectivityManager.TYPE_WIFI:  // WIFI网络
                return true;
            default:                             // 没有网络
                return false;
        }
    }
}
