package com.dave.project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.dave.project.R;
import com.dave.project.ui.base.BaseActivity;

/**
 * Package  com.dave.project
 * Project  Project
 * Author   chenxiaowu
 * Date     2017/10/27.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewWithTheme(APP_THEME.BLACK_THEME, R.layout.main_activity);
        setTitleLeftName("退出");
        setTitleName("主页");
    }

    @Override
    public void onClick(View v) {

    }

    public void selPic(View view) {
        Intent intent = new Intent(this, SelPicActivity.class);
        startActivity(intent);
    }

    public void fresh(View view) {
        Intent intent = new Intent(this, FreshActivity.class);
        startActivity(intent);
    }

    public void request(View view) {
        Toast.makeText(this, "略", Toast.LENGTH_SHORT).show();
    }

    public void img(View view) {
        Intent intent = new Intent(this, ImgActivity.class);
        startActivity(intent);
    }
}
