package com.dave.project.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dave.project.R;
import com.dave.project.ui.base.BaseActivity;
import com.dave.project.util.choose.ISNav;
import com.dave.project.util.choose.config.ISCameraConfig;
import com.dave.project.util.choose.config.ISListConfig;

import java.util.List;

/**
 * https://github.com/smuyyh/ImageSelector
 *
 * @author yuyh.
 * @date 2016/8/5.
 */
public class SelPicActivity extends BaseActivity {

    private static final int REQUEST_LIST_CODE = 0;
    private static final int REQUEST_CAMERA_CODE = 1;

    private TextView tvResult;
//    private SimpleDraweeView draweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(this);
        setViewWithTheme(APP_THEME.BLACK_THEME, R.layout.sel_pic_main);
        setTitleLeftName("返回");
        setTitleName("图片选择器");
        tvResult = (TextView) findViewById(R.id.tvResult);
//        draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
    }

    public void Multiselect(View view) {
        tvResult.setText("");
        ISListConfig config = new ISListConfig.Builder()
                .multiSelect(true)
                // 是否记住上次选中记录
                .rememberSelected(false)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5")).build();

        ISNav.getInstance().toListActivity(this, config, REQUEST_LIST_CODE);
    }

    public void Single(View view) {
        tvResult.setText("");
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选
                .multiSelect(false)
                .btnText("Confirm")
                // 确定按钮背景色
                //.btnBgColor(Color.parseColor(""))
                // 确定按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                .title("Images")
                .titleColor(Color.WHITE)
                .titleBgColor(Color.parseColor("#3F51B5"))
                .allImagesText("All Images")
                .needCrop(true)
                .cropSize(1, 1, 200, 200)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();

        ISNav.getInstance().toListActivity(this, config, REQUEST_LIST_CODE);
    }

    public void Camera(View view) {
        ISCameraConfig config = new ISCameraConfig.Builder()
                .needCrop(true)
                .cropSize(1, 1, 200, 200)
                .build();

        ISNav.getInstance().toCameraActivity(this, config, REQUEST_CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LIST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra("result");

            // 测试Fresco
            // draweeView.setImageURI(Uri.parse("file://"+pathList.get(0)));
            for (String path : pathList) {
                tvResult.append(path + "\n");
            }
        } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK && data != null) {
            String path = data.getStringExtra("result");
            tvResult.append(path + "\n");
        }
    }
}
