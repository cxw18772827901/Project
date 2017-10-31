package com.dave.project.request.volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.dave.project.request.DirectoryManager;
import com.dave.project.util.NetworkUtil;

import org.apache.http.conn.params.ConnRoutePNames;

import java.io.File;
import java.net.Proxy;

/**
 * volley初始化队列工具类,指定缓存目录
 * Created by skindhu on 2015/05/12
 * modify by dave on 2017/04/01
 */
public class NetClient {

    private static final String TAG = "NetClient";
    public static final String DEFAULT_CACHE_DIR = "v_cache";//指定的volley的缓存目录
    private static RequestQueue requestQueue;
    private static DiskBasedCache diskCache;
    private static Network network;

    /**
     * 初始化，cgi请求用的是volley，图片请求用的是Fresco
     *
     * @param context
     */
    public static void init(Context context) {
        if (null != requestQueue) {
            requestQueue.stop();
        }
        requestQueue = newRequestQueue(context);
    }

    /**
     * app初始化时候执行一次,开启volley网络请求队列
     *
     * @param context 上下文
     * @return 返回Volley的RequestQueue
     */
    private static RequestQueue newRequestQueue(Context context) {
        //首先指定app的NetWork,设置移动端访问的ua
        HttpStack stack;
        if (Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack();
        } else {
            String userAgent = "myua/0";
            try {
                String packageName = context.getPackageName();
                PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            AndroidHttpClient client = AndroidHttpClient.newInstance(userAgent);
            Proxy proxy = NetworkUtil.getProxy();
            if (null != proxy) {
                client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
            stack = new HttpClientStack(client);
        }
        network = new BasicNetwork(stack);

        // 然后设置缓存目录，缓存的大小默认是5MB
        File cacheDir = new File(DirectoryManager.getDirectory(DirectoryManager.DIR_VOLLEY_CACHE), DEFAULT_CACHE_DIR);
        diskCache = new DiskBasedCache(cacheDir);
        // 当监听目录的变化，切换cache目录
        DirectoryManager.addSdCardListener(new DirectoryManager.SDcardStatusListener() {
            @Override
            public void onChange(int status) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 根据sd卡的状态(status)，来切换缓存目录。
                        diskCache = new DiskBasedCache(new File(DirectoryManager.getDirectory(DirectoryManager.DIR_VOLLEY_CACHE), DEFAULT_CACHE_DIR));
                        RequestQueue requestQueue = getRequestQueue();
                        if (null != requestQueue) {
                            requestQueue.stop();
                            requestQueue = new RequestQueue(diskCache, network);
                            requestQueue.start();
                        }
                    }
                }).start();
            }
        });
        RequestQueue queue = new RequestQueue(diskCache, network);
        queue.start();//开启队列
        return queue;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    //所有的网络请求发起的操作,超时20s,重试两次
    public static void request(Request request) {
        request.setRetryPolicy(new DefaultRetryPolicy(20000,
               /* DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = getRequestQueue();
        if (null != requestQueue && null != request) {
            requestQueue.add(request);
        }
    }

    static void cancleAll() {
        RequestQueue requestQueue = getRequestQueue();
        if (null != requestQueue) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    // do I have to cancel this?
                    return true; // -> always yes
                }
            });
        }
    }
}
