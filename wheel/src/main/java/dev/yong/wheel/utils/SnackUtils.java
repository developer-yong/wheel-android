package dev.yong.wheel.utils;


import com.google.android.material.snackbar.Snackbar;
import android.text.TextUtils;
import android.view.View;

/**
 * SnackBar工具类
 *
 * @author CoderYong
 */
public class SnackUtils {

    private static final String TAG = SnackUtils.class.getSimpleName();

    private SnackUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    /**
     * SnackBar 提示
     *
     * @param v        依附View
     * @param text     提示文本
     * @param duration 显示时间
     */
    public static void show(View v, CharSequence text, int duration) {
        if (v == null) {
            throw new NullPointerException(TAG + " --- 依附View为空!");
        }
        if (!TextUtils.isEmpty(text)) {
            Snackbar snackbar = Snackbar.make(v, text, duration);
            if (!snackbar.isShown()) {
                snackbar.show();
            }
        }
    }

    /**
     * SnackBar 提示 （默认短时间显示）
     *
     * @param v    依附View
     * @param text 提示文本
     */
    public static void show(View v, CharSequence text) {
        show(v, text, -1);
    }

    /**
     * SnackBar 提示
     *
     * @param v        依附View
     * @param resId    提示文本资源Id
     * @param duration 显示时间
     */
    public static void show(View v, int resId, int duration) {
        show(v, v.getResources().getString(resId), duration);
    }

    /**
     * SnackBar 提示 （默认短时间显示）
     *
     * @param v     依附View
     * @param resId 提示文本资源Id
     */
    public static void show(View v, int resId) {
        show(v, resId, -1);
    }
}
