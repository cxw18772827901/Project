package com.dave.project.ui.base;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dave.project.R;
import com.dave.project.util.KeyBoardUtil;
import com.dave.project.util.TitleBarUtil;
import com.dave.project.util.inject.ViewInjectUtil;

import static com.dave.project.ui.base.BaseActivity.APP_THEME.NONE_THEME;


/**
 * activity的基类,跟BaseFragmentActivity的区别在于父类不同:
 * 1.处理沉浸式风格的标题栏(可以设置三种风格黄色,黑色,默认无沉浸式无风格);
 * 2.使用注解处理控件的查找和点击事件
 * Created by Dave on 2016/11/10.
 */
public class BaseActivity extends FragmentActivity {
    private TextView tv_title_title;
    private TextView tv_title_right;
    private TextView tv_title_back;
    private ImageView iv_title_right;
    private View iv_base_bg;
    private RelativeLayout rl_right_click;
    private APP_THEME app_theme = NONE_THEME;
    protected boolean needFinishAnim = true;
    private FrameLayout rootView;
    private TextView tv_title_back_no_arrow;

    protected enum APP_THEME {
        YELLOW_THEME, BLACK_THEME, NONE_THEME
    }

    protected void setViewWithTheme(APP_THEME app_theme, int layoutResID) {
        this.app_theme = app_theme;
        setContentView(layoutResID);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (APP_THEME.YELLOW_THEME == app_theme || APP_THEME.BLACK_THEME == app_theme) {
            LayoutInflater inflater = LayoutInflater.from(this);
            if (APP_THEME.YELLOW_THEME == app_theme) {
                rootView = (FrameLayout) inflater.inflate(R.layout.custom_title, null);
            } else {
                rootView = (FrameLayout) inflater.inflate(R.layout.custom_black_title, null);
            }
            LinearLayout ll_container = (LinearLayout) rootView.findViewById(R.id.ll_container);
            View contentView = inflater.inflate(layoutResID, rootView, false);
            ll_container.addView(contentView);
            super.setContentView(rootView);
            rootView.setFitsSystemWindows(true);
            TitleBarUtil.initPrisiveBar(this, BaseActivity.APP_THEME.YELLOW_THEME == app_theme ? R.color.cl_f6c708 : R.color.cl_black);
            initTitleView();
        } else {
            super.setContentView(layoutResID);
        }
        ViewInjectUtil.initInActivity(this);
    }

    protected boolean shouldFinish = true;

    private void initTitleView() {
        iv_base_bg = rootView.findViewById(R.id.iv_base_bg);
        tv_title_back = (TextView) rootView.findViewById(R.id.tv_title_back);
        tv_title_back_no_arrow = (TextView) rootView.findViewById(R.id.tv_title_back_no_arrow);
        tv_title_title = (TextView) rootView.findViewById(R.id.tv_title_title);
        rl_right_click = (RelativeLayout) rootView.findViewById(R.id.rl_right_click);
        tv_title_right = (TextView) rootView.findViewById(R.id.tv_title_right);
        iv_title_right = (ImageView) rootView.findViewById(R.id.iv_title_right);
        tv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_title_back:
                        titleBackClick();
                        if (shouldFinish) {
                            finish();
                            overridePendingTransition(R.anim.trans_pre_in_back, R.anim.trans_pre_out_back);
                        }
                        break;
                }
            }
        });
    }

    //设置标题名字
    protected void setTitleName(String titleName) {
        tv_title_title.setText(titleName);
    }

    //设置返回按钮的名字
    protected void setTitleLeftName(String leftName) {
        tv_title_back.setText(leftName);
    }

    //设置返回按钮的名字
    protected void hideTitleLeftTv() {
        tv_title_back.setVisibility(View.INVISIBLE);
    }

    protected void hideLeftArrow(String str) {
        tv_title_back.setVisibility(View.GONE);
        tv_title_back_no_arrow.setVisibility(View.VISIBLE);
        tv_title_back_no_arrow.setText(str);
        tv_title_back_no_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_title_back_no_arrow:
                        titleBackClick();
                        if (shouldFinish) {
                            finish();
                            overridePendingTransition(R.anim.trans_pre_in_back, R.anim.trans_pre_out_back);
                        }
                        break;
                }
            }
        });
    }

    //设置右边按钮的点击事件
    protected void setTitleRightTvClick(View.OnClickListener clickListener, String msg) {
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText(null == msg ? "确定" : msg);
        tv_title_right.setOnClickListener(clickListener);
    }

    //设置右边按钮的点击事件
    protected void setTitleRightIvClick(View.OnClickListener clickListener, int resourseId) {
        iv_title_right.setVisibility(View.VISIBLE);
        iv_title_right.setImageResource(resourseId);
        rl_right_click.setOnClickListener(clickListener);
    }

    //更换背景颜色
    protected void setBgColor(int colorId) {
        rootView.findViewById(R.id.root_view).setBackgroundColor(colorId);
    }

    protected void changeBg(int resourseId) {
        if (-1 != resourseId) {
            iv_base_bg.setBackgroundResource(resourseId);
        }
        iv_base_bg.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        KeyBoardUtil.dismissSoftKeyboard(this);
        if (needFinishAnim) {
            overridePendingTransition(R.anim.trans_pre_in_back, R.anim.trans_pre_out_back);
        }
    }

    protected void titleBackClick() {

    }

}
