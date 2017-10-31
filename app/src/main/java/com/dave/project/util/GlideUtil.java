package com.dave.project.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Package  com.dave.project.util
 * Project  Project
 * Author   chenxiaowu
 * Date     2017/10/30.
 */

public class GlideUtil {
    public static void load(Context context, String url, int holderResourseId, ImageView imageView) {
        Glide.with(context).load(url).placeholder(holderResourseId).into(imageView);
    }
}
