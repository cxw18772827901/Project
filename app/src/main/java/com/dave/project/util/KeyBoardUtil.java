package com.dave.project.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dave.project.base.BaseApplication;


/**
 * 键盘输入工具类
 * Created by Dave on 2017/1/13.
 */

public class KeyBoardUtil {
    /**
     * 输入类型管理器
     *
     * @return
     */
    public static InputMethodManager getInputManager() {
        return (InputMethodManager) BaseApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 为给定的编辑器开启软键盘
     *
     * @param editText 给定的编辑器
     */
    public static void showSoftKeyboard(EditText editText) {
        editText.requestFocus();
        getInputManager().showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void closeKeybord(EditText mEditText) {
        getInputManager().hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 关闭系统的软键盘(无需传编辑框的引用）
     */
    public static void dismissSoftKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            getInputManager().hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 切换软键盘的状态
     */
    public static void toggleSoftKeyboardState() {
      getInputManager().toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
