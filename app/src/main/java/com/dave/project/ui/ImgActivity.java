package com.dave.project.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dave.project.R;
import com.dave.project.ui.base.BaseActivity;

/**
 * Package  com.dave.project.ui
 * Project  Project
 * Author   chenxiaowu
 * Date     2017/10/30.
 */

public class ImgActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewWithTheme(APP_THEME.BLACK_THEME, R.layout.img_activity);
    }
}
