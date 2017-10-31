package com.dave.project.wedjet;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 只暴露一个方法textChanged(Editable s)
 * Created by chenxiaowu on 2017/5/10.
 */
public class MyTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textChanged(s);
    }

    //只是用这个方法即可
    public void textChanged(Editable s) {

    }
}
