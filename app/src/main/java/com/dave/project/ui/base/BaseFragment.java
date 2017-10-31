package com.dave.project.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.dave.project.util.debug.MyDebugUtil;
import com.dave.project.util.inject.ViewInjectUtil;


/**
 * 主界面fragment基类
 * Created by Dave on 2017/1/11.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewInjectUtil.initNotInActivity(this, view);//注解
    }

    //tab切换后fragment里面的回调
    public void tabClick() {
        MyDebugUtil.logTest("tabClick", "tab4");
    }

}
