package com.dave.project.request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 路径管理类
 * Created by skindhu on 2015/05/16
 */
public class DirectoryManager {

    public static final String DIR_IMAGE = "image";
    public static final String DIR_VOLLEY_CACHE = "cache";

    private static File baseDirectory;
    private static File cacheDirectory;

    static List<SDcardStatusListener> sdcardStatusListener;

    public static void init(Context context) {
        update(context);
        sdcardStatusListener = new ArrayList<>();
        //监听sd卡卸载和装载，并及时切换缓存目录
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        filter.addDataScheme("file");
        context.registerReceiver(new SDCardListenerReceiver(), filter);
    }

    public static void addSdCardListener(SDcardStatusListener listener) {
        sdcardStatusListener.add(listener);
    }

    /**
     * 创建缓存目录
     *
     * @param context 上下文
     */
    public synchronized static void update(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String packageName = context.getPackageName();
            baseDirectory = new File(Environment.getExternalStorageDirectory(), "Android/data/" + packageName + "/files/");
            cacheDirectory = new File(Environment.getExternalStorageDirectory(), "Android/data/" + packageName + "/cache/");
        } else {
            baseDirectory = context.getFilesDir();
            cacheDirectory = context.getCacheDir();
        }

        File file = new File(baseDirectory, DIR_IMAGE);
        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(baseDirectory, DIR_VOLLEY_CACHE);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 获得内部定义好的一个文件路径
     *
     * @param dir
     * @return
     */
    public synchronized static File getDirectory(String dir) {
        if (dir.equals(DIR_VOLLEY_CACHE)) {
            return cacheDirectory;
        } else {
            return new File(baseDirectory, dir);
        }
    }

    /**
     * 递归删除某个路径下的所有文件
     *
     * @param path
     */
    public static void clear(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                clear(file);
            }
        } else {
            path.delete();
        }
    }

    /**
     * SD卡状态发生变化时的监听函数
     */
    public interface SDcardStatusListener {
        int MEDIA_MOUNTED = 0;
        int MEDIA_REMOVED = 1;
        int MEDIA_UNMOUNTED = 2;
        int MEDIA_BAD_REMOVAL = 3;

        void onChange(int status);
    }

    /**
     * 监听SD卡的广播接收者
     */
    static class SDCardListenerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null != action) {
                DirectoryManager.update(context);
            } else {
                return;
            }
            int status = -1;
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                status = SDcardStatusListener.MEDIA_MOUNTED;
            } else if (action.equals("android.intent.action.MEDIA_REMOVED")) {
                status = SDcardStatusListener.MEDIA_REMOVED;
            } else if (action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                status = SDcardStatusListener.MEDIA_UNMOUNTED;
            } else if (action.equals("android.intent.action.MEDIA_BAD_REMOVAL")) {
                status = SDcardStatusListener.MEDIA_BAD_REMOVAL;
            }
            //依次遍历每一个listener进行回调,切换缓存的目录
            if (status >= 0 && sdcardStatusListener.size() > 0) {
                for (SDcardStatusListener listener : sdcardStatusListener) {
                    listener.onChange(status);
                }
            }
        }
    }
}
