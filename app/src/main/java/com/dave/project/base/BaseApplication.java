package com.dave.project.base;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dave.project.request.DirectoryManager;
import com.dave.project.request.HttpsTrustManager;
import com.dave.project.request.volley.NetClient;
import com.dave.project.util.choose.ISNav;
import com.dave.project.util.choose.common.ImageLoader;

/**
 * Package  com.dave.project.base
 * Project  Project
 * Author   chenxiaowu
 * Date     2017/10/27.
 */

public class BaseApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        init();
    }

    private void init() {
        //路径管理类初始化
        DirectoryManager.init(this);
        //Volley初始化网络请求队列以及缓存目录
        NetClient.init(this);
        //Volley允许SSL访问（所有的Https请求统一初始化）
        HttpsTrustManager.allowAllSSL();
        //glide
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    //获取context
    public static Context getContext() {
        return context;
    }
}
