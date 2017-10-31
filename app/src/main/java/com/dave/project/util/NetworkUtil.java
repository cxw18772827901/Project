package com.dave.project.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.dave.project.base.BaseApplication;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断设备当前网络状态的工具类,主要是判断当前网络类型和代理相关设置
 * Created by skindhu on 15/5/28.
 */
public class NetworkUtil {

    public interface NetType {

        /**
         * 无网络
         */
        int NO_NETWORK = -1;

        /**
         * 未知网络
         */
        int UNKNOWN = 0;

        /**
         * WIFI网络
         */
        int WIFI = 1;

        /**
         * 2G网络
         */
        int G2 = 2;

        /**
         * 3G网络
         */
        int G3 = 3;

        /**
         * 4G网络
         */
        int G4 = 4;
    }

    /**
     * 获取网络类型
     *
     * @return -1  :   'NO_NETWORK' 无网络
     * 0  ：  'UNKDOWN'  未知类型
     * 1  ：  'WIFI'
     * 2  :   '2G'
     * 3  ：  '3G'
     * 4  :   '4G'
     */
    public static int getNetworkType() {
        final Context context = BaseApplication.getContext();
        if (context == null) {
            throw new RuntimeException("net work not initial");
        }

        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr == null) {
                return NetType.UNKNOWN;
            }

            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info == null) {
                return NetType.UNKNOWN;
            }
            if (!info.isConnected()) {
                return NetType.NO_NETWORK;
            }
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_WIFI
                    || type == ConnectivityManager.TYPE_ETHERNET) {
                return NetType.WIFI;
            }

            if (type == ConnectivityManager.TYPE_MOBILE) {
                NetworkInfo networkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (networkInfo == null) {
                    return NetType.UNKNOWN;
                }
                int subType = networkInfo.getSubtype();

                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return NetType.G2; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:// ~ 400-7000 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:/** Current network is HSUPA */
                    case TelephonyManager.NETWORK_TYPE_HSPA:/** Current network is HSPA */
                    case TelephonyManager.NETWORK_TYPE_IDEN:/** Current network is iDen */
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:/** Current network is EVDO revision B*/
                        return NetType.G3;
                    case TelephonyManager.NETWORK_TYPE_LTE: /** Current network is LTE */
                    case TelephonyManager.NETWORK_TYPE_EHRPD:/** Current network is eHRPD */
                    case TelephonyManager.NETWORK_TYPE_HSPAP:/** Current network is HSPA+ */
                        return NetType.G4;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NetType.UNKNOWN;
    }

    /**
     * 获取当前的网络代理
     *
     * @return
     */
    public static java.net.Proxy getProxy() {
        try {
            if (getNetworkType() != NetType.WIFI) {
                // 获取当前正在使用
                Uri uri = Uri.parse("content://telephony/carriers/preferapn");
                Cursor cursor = BaseApplication.getContext().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    boolean b = cursor.moveToNext();
                    if (b) {
                        String proxyStr = cursor.getString(cursor.getColumnIndex("proxy")); //有可能报错
                        int port = cursor.getInt(cursor.getColumnIndex("port"));
                        if (proxyStr != null && proxyStr.trim().length() > 0) {
                            if (port == -1) {
                                port = 80;
                            }
                            return createProxy(proxyStr, port);
                        }
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param proxy, 代理信息
     * @param port,  端口信息
     * @return Proxy, 代理类
     */
    private static java.net.Proxy createProxy(String proxy, int port) throws UnknownHostException {
        Pattern p = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
        Matcher m = p.matcher(proxy);
        InetAddress add;
        if (m != null && m.find()) {
            byte[] i4 = new byte[4];
            i4[0] = (byte) Integer.parseInt(m.group(1));
            i4[1] = (byte) Integer.parseInt(m.group(2));
            i4[2] = (byte) Integer.parseInt(m.group(3));
            i4[3] = (byte) Integer.parseInt(m.group(4));
            add = InetAddress.getByAddress(proxy, i4);
        } else {
            add = InetAddress.getByName(proxy);
        }
        return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(add, port));
    }

    /**
     * 监测当前是否有网络连接
     *
     * @return
     */
    public static boolean isNetSupport() {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            try {
                NetworkInfo[] e = cm.getAllNetworkInfo();
                if (e != null) {
                    for (int i = 0; i < e.length; ++i) {
                        if (e[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}
