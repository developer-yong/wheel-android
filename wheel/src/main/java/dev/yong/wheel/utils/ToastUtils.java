package dev.yong.wheel.utils;

import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import dev.yong.wheel.AppManager;

/**
 * Toast工具类
 *
 * @author CoderYong
 */

public class ToastUtils {

    private ToastUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    private static long sTime = -1;

    public static int GRAVITY = Gravity.NO_GRAVITY;

    /**
     * Toast提示
     *
     * @param text     提示文本
     * @param duration 显示时长
     */
    public static void show(CharSequence text, int duration) {
        long intervals = 2000;
        if (System.currentTimeMillis() - sTime > intervals) {
            try {
                Toast toast = Toast.makeText(AppManager.getInstance().getApplication(), text, duration);
                toast.setGravity(GRAVITY, 0, 0);
                toast.show();
            } catch (Exception e) {
                //解决在子线程中调用Toast的异常情况处理
                Looper.prepare();
                Toast toast = Toast.makeText(AppManager.getInstance().getApplication(), text, duration);
                toast.setGravity(GRAVITY, 0, 0);
                toast.show();
                Looper.loop();
            }
            sTime = System.currentTimeMillis();
        }
    }

    /**
     * Toast提示
     *
     * @param text 提示文本 (默认短时间显示)
     */
    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * Toast提示
     *
     * @param resId    提示文本资源Id
     * @param duration 显示时长
     */
    public static void show(int resId, int duration) {
        show(AppManager.getInstance().getApplication().getString(resId), duration);
    }

    /**
     * Toast提示 (默认短时间显示)
     *
     * @param resId 提示文本资源Id
     */
    public static void show(int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }
}
