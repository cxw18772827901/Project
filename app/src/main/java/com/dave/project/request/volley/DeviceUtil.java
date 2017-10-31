package com.dave.project.request.volley;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.dave.project.base.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 设备信息获取类
 * Created by skindhu on 15/5/13.
 */
public class DeviceUtil {

    public static final String TAG = "DeviceUtil";

    public static final int BUFFER_SIZE = 1024;

    private static String deviceId = "";
    private static String imsi = "";

    private static long screenWidth = 0;
    private static long screenHeight = 0;
    public static float density = 1;
    public static int densityDpi = 0;

    public static String mid = "0";
    private static long cachedTotalMemory = 0;

    public static final String APP_FOLDER_ON_SD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TianXiang/TianXiang";

    /**
     * 获取手机IMEI,设备号,通常将次作为每台手机唯一标示即可
     *
     * @return String
     */
    public static String getIMEI() {
        if (deviceId != null && deviceId.length() > 0) {
            return deviceId;
        }
        try {
            Context context = BaseApplication.getContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
            if (TextUtils.isEmpty(deviceId) || deviceId.equals("000000000000000")) {
                deviceId = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

    /**
     * 获取手机IMSI,subid
     *
     * @return String
     */
    public static String getIMSI() {
        if (imsi != null && imsi.length() > 0) {
            return imsi;
        }
        try {
            Context context = BaseApplication.getContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
            if (TextUtils.isEmpty(imsi)) {
                return "0";
            } else {
                return imsi;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imsi;
    }

    /**
     * 获取设备的MID,暂时用deviceid代替
     *
     * @return String
     */
    public static String getMid() {
        if (mid != null && !mid.equals("0")) {
            return mid;
        } else {
            Context context = BaseApplication.getContext();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mid = telephonyManager.getDeviceId();
            return mid;
        }
    }

    /**
     * @param context context
     * @return mcnc
     */
    public static String getMcnc(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mcnc = tm.getNetworkOperator();
        if (TextUtils.isEmpty(mcnc)) {
            return "0";
        } else {
            return mcnc;
        }
    }

    /**
     * @param context context
     * @return SerialNumber
     */
    public static String getSerialNumber(Context context) {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            if (serial == null || serial.trim().length() <= 0) {
                TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                serial = tManager.getDeviceId();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取系统版本号
     *
     * @return String 如4.0, 5.0, 5.1
     */
    public static String getDeviceOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机系统SDK版本
     *
     * @return int  如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机信息
     *
     * @return String
     */
    public String getPhoneInfo() {
        String phoneInfo = "Product: " + Build.PRODUCT;
        phoneInfo += ", CPU_ABI: " + Build.CPU_ABI;
        phoneInfo += ", TAGS: " + Build.TAGS;
        phoneInfo += ", VERSION_CODES.BASE: "
                + Build.VERSION_CODES.BASE;
        phoneInfo += ", MODEL: " + Build.MODEL;
        phoneInfo += ", SDK: " + Build.VERSION.SDK_INT;
        phoneInfo += ", VERSION.RELEASE: " + Build.VERSION.RELEASE;
        phoneInfo += ", DEVICE: " + Build.DEVICE;
        phoneInfo += ", DISPLAY: " + Build.DISPLAY;
        phoneInfo += ", BRAND: " + Build.BRAND;
        phoneInfo += ", BOARD: " + Build.BOARD;
        phoneInfo += ", FINGERPRINT: " + Build.FINGERPRINT;
        phoneInfo += ", ID: " + Build.ID;
        phoneInfo += ", MANUFACTURER: " + Build.MANUFACTURER;
        phoneInfo += ", USER: " + Build.USER;
        return phoneInfo;
    }

    private static void initDisplayParams() {
        if (screenWidth == 0 || screenHeight == 0) {
            Context context = BaseApplication.getContext();
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            density = dm.density;
            densityDpi = dm.densityDpi;
        }
    }

    public static float getDensity() {
        initDisplayParams();
        return density;
    }

    public static int getDisplayDpi() {
        initDisplayParams();
        return densityDpi;
    }

    public static long getDisplayWidth() {
        initDisplayParams();
        return screenWidth;
    }

    public static long getDiplayHeight() {
        initDisplayParams();
        return screenHeight;
    }

    /**
     * 获取系统总共内存
     *
     * @return long 系统总共内存，单位为Byte
     */
    public static long getSystemTotalMemory() {
        if (cachedTotalMemory == 0) {
            String str1 = "/proc/meminfo"; // 系统内存文件
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(str1);
                br = new BufferedReader(fr, BUFFER_SIZE);
                String line = br.readLine(); //获取meminfo第一行，系统总内存大小
                if (line != null) {
                    String[] arrayOfString = line.split("\\s+");
                    cachedTotalMemory = Long.valueOf(arrayOfString[1]).longValue() * 1024; // 获取系统总内存，单位是KB，乘以1024转换为Byte
                }
            } catch (Exception e) {
                cachedTotalMemory = 0;
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }

                    if (fr != null) {
                        fr.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cachedTotalMemory == 0) {
                return (long) 1024 * 1024 * 1024;
            }
        }

        return cachedTotalMemory;
    }

    /**
     * 获取系统可用内存信息
     *
     * @return long  系统可用内存，单位为byte
     */
    public static long getSystemAvaialbeMemory() {
        Context context = BaseApplication.getContext();
        if (context != null) {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            //获得MemoryInfo对象
            ActivityManager.MemoryInfo sysMemoryInfo = new ActivityManager.MemoryInfo();
            mActivityManager.getMemoryInfo(sysMemoryInfo);
            return sysMemoryInfo.availMem;
        } else {
            return -1;
        }
    }

    /**
     * 系统限制每个应用的内存大小，单位为Byte
     *
     * @return long
     */
    public static long getMemoryClass() {
        Context context = BaseApplication.getContext();
        if (context == null) {
            return (long) ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024;
        } else {
            return -1;
        }
    }

    /**
     * 获取手机及SIM卡相关信息
     *
     * @param context
     * @return Map<String, String>
     */
    public static Map<String, String> getPhoneInfo(Context context) {
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String imsi = tm.getSubscriberId();
        String phoneMode = Build.MODEL;
        String phoneSDk = Build.VERSION.RELEASE;
        map.put("imei", imei);
        map.put("imsi", imsi);
        map.put("phoneMode", phoneMode + "##" + phoneSDk);
        map.put("model", phoneMode);
        map.put("sdk", phoneSDk);
        return map;
    }

    /**
     * @param context 上下文
     * @return apn
     */
    public static String getAPN(Context context) {
        String apn = "";
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null) {
            if (ConnectivityManager.TYPE_WIFI == info.getType()) {
                apn = info.getTypeName();
                if (apn == null) {
                    apn = "wifi";
                }
            } else {
                apn = info.getExtraInfo().toLowerCase();
                if (apn == null) {
                    apn = "mobile";
                }
            }
        }
        return apn;
    }

    /**
     * @param context 上下文
     * @return model
     */
    public static String getModel(Context context) {
        return Build.MODEL;
    }

    /**
     * @param context context
     * @return MANUFACTURER
     */
    public static String getManufacturer(Context context) {
        return Build.MANUFACTURER;
    }

    /**
     * @param context context
     * @return RELEASE
     */
    public static String getFirmware(Context context) {
        return Build.VERSION.RELEASE;
    }

    /**
     * @param context context
     * @param keyName keyName
     * @return data
     */
    public static Object getMetaData(Context context, String keyName) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.metaData;
            Object value = bundle.get(keyName);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return 获取语言
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String languageCode = locale.getLanguage();
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = "";
        }
        return languageCode;
    }

    /**
     * @return 获取国家
     */
    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String countryCode = locale.getCountry();
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = "";
        }
        return countryCode;
    }

    public static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.

        // Logger.d(TAG, "checkFsWritable directoryName ==   "
        // + PathCommonDefines.APP_FOLDER_ON_SD);

        File directory = new File(APP_FOLDER_ON_SD);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        File f = new File(APP_FOLDER_ON_SD, ".probe");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean hasStorage() {
        boolean hasStorage = false;
        String str = Environment.getExternalStorageState();

        if (str.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            hasStorage = checkFsWritable();
        }

        return hasStorage;
    }


    /**
     * 获取手机分辨率
     */
    /*public static String getDisplayMetrix(Context context)
    {
		if (Constant.Screen.SCREEN_WIDTH == 0 || Constant.Screen.SCREEN_HEIGHT == 0)
		{
			if (context != null)
			{
				int width = 0;
				int height = 0;
				SharedPreferences DiaplayMetrixInfo = context.getSharedPreferences("display_metrix_info", 0);
				if (context instanceof Activity)
				{
					WindowManager windowManager = ((Activity)context).getWindowManager();
					Display display = windowManager.getDefaultDisplay();
					DisplayMetrics dm = new DisplayMetrics();
					display.getMetrics(dm);
					width = dm.widthPixels;
					height = dm.heightPixels;

					SharedPreferences.Editor editor = DiaplayMetrixInfo.edit();
					editor.putInt("width", width);
					editor.putInt("height", height);
					editor.commit();
				}
				else
				{
					width = DiaplayMetrixInfo.getInt("width", 0);
					height = DiaplayMetrixInfo.getInt("height", 0);
				}

				Constant.Screen.SCREEN_WIDTH = width;
				Constant.Screen.SCREEN_HEIGHT = height;
			}
		}
		return Constant.Screen.SCREEN_WIDTH + "×" + Constant.Screen.SCREEN_HEIGHT;
	}*/

}
