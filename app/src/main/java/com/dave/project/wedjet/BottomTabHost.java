package com.dave.project.wedjet;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dave.project.R;
import com.dave.project.ui.base.BaseFragment;
import com.dave.project.ui.base.MyException;
import com.dave.project.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义导航栏底部的TabHost,用法:
 * ①在activity中布局:
 * <xxx.BottomTabHost
 * android:id="@+id/tabHost"
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"/>
 * ②初始化控件
 * MyTabHost myTabHost = (MyTabHost) findViewById(R.id.tabHost);
 * ③生成底部导航栏,参数分别是:manager,fragment容器id,tab栏文字集合(String集合),tab栏图片集合(R.drawable.xxx集合),fragment集合(new xxxFragment()集合),要展示pop栏index,要隐藏tab栏index
 * myTabHost.init(getSupportFragmentManager(), R.id.fragme_container, tabText, imageRes, fragments, 1, -2);
 * ④获取pop显示
 * TextView tv_pop = myTabHost.getPop();
 * tv_pop.setText("9");
 * ⑤手动切换布局,如:切换到最后一个布局
 * myTabHost.setCurrentTab(2);
 * ⑥隐藏文字,只保留tab图片
 * myTabHost.hideTextView();
 * ⑦tab栏监听回调(初始创建的时候不回调,需要可以自定义修改)
 * myTabHost.setOnTabChangedListener(this);
 * ⑧获取当前tab栏下标
 * myTabHost.getCurrentTab();
 * Created by Dave on 2017/1/18.
 */

public class BottomTabHost extends LinearLayout {
    private LinearLayout tab_container;
    private Activity activity;
    private FragmentManager fragmentManager;
    private int fragmentContainerViewId;
    private List<String> tvList;
    private List<Integer> imgList;
    private List<? extends Fragment> fragmentList;
    private LayoutInflater layoutInflater;
    private int popPosition;
    private TextView tv_pop;
    private Fragment currentFragment;
    private int hideTabPosition;
    private List<View> tabViewList = new ArrayList<>();
    private int currentTab = 0;
    private static FrameLayout fl_pop;

    public BottomTabHost(Context context) {
        this(context, null);
    }

    public BottomTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        layoutInflater = LayoutInflater.from(context);
        tab_container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.tab_container, this);
    }

    public void init(Activity activity, FragmentManager fragmentManager, int fragmentContainerViewId, List<String> tvList, List<Integer> imgList, List<? extends Fragment> fragmentList, int popPosition, int hideTabPosition) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.fragmentContainerViewId = fragmentContainerViewId;
        this.tvList = tvList;
        this.imgList = imgList;
        this.fragmentList = fragmentList;
        this.popPosition = popPosition;
        this.hideTabPosition = hideTabPosition;
        //检查集合数据是否一致
        checkResourse();
        //添加tab
        addTabs();
        //设置tab的点击切换
        tabClick();
        //展示第一个界面
        showFirstTab();
    }

    private void checkResourse() {
        if (null == tvList || null == imgList || null == fragmentList
                || tvList.size() == 0 || imgList.size() == 0 || fragmentList.size() == 0) {
            throw new MyException("初始化资源文件为空");
        }
        if (imgList.size() != tvList.size() || fragmentList.size() != tvList.size()) {
            throw new MyException("初始化资源文件不一致");
        }
        if (popPosition > tvList.size() - 1) {//popPosition可以为负数,但是不能大于下标最大值
            throw new MyException("要展示的pop不存在");
        }
        if (hideTabPosition > tvList.size() - 1) {//hideTabPosition可以为负数,但是不能大于下标最大值
            throw new MyException("要隐藏的tab不存在");
        }
    }

    //所有的tab按照weight为1来添加,之后需要重绘才能正常显示
    private void addTabs() {
        tabViewList.clear();
        tab_container.removeAllViews();
        for (int i = 0; i < tvList.size(); i++) {
            View bottom_tab = layoutInflater.inflate(R.layout.bottom_tab_1, tab_container, false);
            tabViewList.add(bottom_tab);
            ImageView iv_tab = (ImageView) bottom_tab.findViewById(R.id.iv_tab);
            TextView tv_name = (TextView) bottom_tab.findViewById(R.id.tv_name);
            iv_tab.setImageResource(imgList.get(i));
            tv_name.setText(tvList.get(i));
            tab_container.addView(bottom_tab, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
            if (popPosition == i) {
                fl_pop = (FrameLayout) bottom_tab.findViewById(R.id.fl_pop);
                tv_pop = (TextView) bottom_tab.findViewById(R.id.tv_pop);
                //动态调整pop的位置
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fl_pop.getLayoutParams();
                int leftMargin = ScreenUtil.getScreenWid() * 80 / 720;
//                if (ScreenUtil.getScreenWid() >= 1080) {
//                    leftMargin = UIUtil.dip2px(70);
//                } else {
//                    leftMargin = UIUtil.dip2px(60);
//                }
                layoutParams.leftMargin = leftMargin;
                fl_pop.setLayoutParams(layoutParams);
            }
            if (hideTabPosition == i) {
                bottom_tab.setVisibility(View.INVISIBLE);
            }
        }
        tab_container.invalidate();
    }

    private void showFirstTab() {
        currentFragment = fragmentList.get(0);
        if (!currentFragment.isAdded()) {
            fragmentManager.beginTransaction().add(fragmentContainerViewId, currentFragment).commit();
        } else {
            fragmentManager.beginTransaction().show(currentFragment).commit();
        }
        setTabSelected(0);
        currentTab = 0;
    }

    //此处有判断登录的逻辑
    private void tabClick() {
        for (int i = 0; i < tabViewList.size(); i++) {
            final int finalI = i;
            tabViewList.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (UserUtil.isAnchor()) {
//                        String state = SPUtil.getString(Constants.HEART_STATE);
//                        if (!ChangeAnchorStateUtil.LEISURE.equals(state) &&
//                                !ChangeAnchorStateUtil.OFF_LINE.equals(state)) {
//                            SPUtil.put(Constants.HEART_STATE, ChangeAnchorStateUtil.LEISURE);
//                            ChangeAnchorStateUtil.changeAnchorState(SPUtil.getString(Constants.USER_ACCOUNT), ChangeAnchorStateUtil.LEISURE);
//                        }
//                    }
//                    if (finalI == 0 || finalI == 1 /*|| finalI == 2*/) {
//                        changeFragment(finalI);
//                    } else/* if (finalI == 2 || finalI == 3) */ {//不切换,直接回调跳转到登录界面
//                        if (LoginVerifyUtil.isLogin()) {
                    changeFragment(finalI);
//                        } else {
//                            IntentUtil.startActivity(activity, LoginActivity.class);
//                        }
//                    }
//                    MyDebugUtil.log("account_me", SPUtil.getString(Constants.USER_ACCOUNT) + "");
                }
            });
        }
    }

    private synchronized void changeFragment(int finalI) {
        Fragment fragment = fragmentList.get(finalI);
        if (currentFragment.equals(fragment)) {//是当前的tab,再次点击的话无效
            return;
        }
        if (fragment.isAdded()) {
            Log.e("sss", "isAdded");
            fragmentManager.beginTransaction().hide(currentFragment).show(fragment).commit();
            //以下两种都可以,根据需求选择即可
            if (null != onTabChangedListener) {
                onTabChangedListener.tabChange(finalI);//回调到MainActivity
            }
            ((BaseFragment) fragment).tabClick();//直接回调到Fragment
        } else {
            Log.e("sss", "notAdded");
            fragmentManager.beginTransaction().hide(currentFragment).add(fragmentContainerViewId, fragment).commit();
        }
        //状态
        setTabSelected(finalI);
        currentFragment = fragment;
        currentTab = finalI;
    }

    //设置选中状态
    private void setTabSelected(int position) {
        for (int i = 0; i < tabViewList.size(); i++) {
            tabViewList.get(i).findViewById(R.id.tv_name).setSelected(position == i);
            tabViewList.get(i).findViewById(R.id.iv_tab).setSelected(position == i);
        }
    }

    //获取显示的pop
    public TextView getPop() {
        if (null == tv_pop) {
            throw new MyException("pop为空,先在init里面设置pop的索引");
        }
        return tv_pop;
    }

    //自定义切换tab栏
    public void setCurrentTab(int tabPosition) {
        if (tabPosition < 0 || tabPosition > tvList.size() - 1) {
            throw new MyException("要展示的tab不存在");
        }
        changeFragment(tabPosition);
    }

    //获取当前tab栏
    public int getCurrentTab() {
        return currentTab;
    }

    //只显示tab的图片,隐藏文字
    public void hideTextView() {
        for (View view : tabViewList) {
            view.findViewById(R.id.tv_name).setVisibility(View.GONE);
        }
    }

    //只显示tab的文字,隐藏图片
    public void hideImgView() {
        for (View view : tabViewList) {
            view.findViewById(R.id.iv_tab).setVisibility(View.GONE);
        }
    }

    public void showPop(int unReadCount) {
        if (unReadCount < 99) {
            tv_pop.setText(String.valueOf(unReadCount));
        } else {
            tv_pop.setText("99+");
        }
        fl_pop.setVisibility(0 == unReadCount ? View.GONE : View.VISIBLE);
    }

    //tab切换后在MainActivity里面的回调,参数为下标
    private OnTabChangedListener onTabChangedListener;

    public void setOnTabChangedListener(OnTabChangedListener onTabChangedListener) {
        this.onTabChangedListener = onTabChangedListener;
    }

    public interface OnTabChangedListener {
        void tabChange(int tabIndex);
    }
}
