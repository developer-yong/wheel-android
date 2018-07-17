package dev.yong.wheel.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 输入法工具
 *
 * @author CoderYong
 */
public class InputMethodUtils {

    private InputMethodUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    public static void hide(View v) {
        Context context = v.getContext();
        if (context != null) {
            //隐藏键盘
            InputMethodManager manager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    public static void show(View v) {
        Context context = v.getContext();
        if (context != null) {
            //显示键盘
            InputMethodManager manager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.showSoftInput(v, 1);
            }
        }
    }
}
