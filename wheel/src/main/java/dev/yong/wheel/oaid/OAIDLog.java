package dev.yong.wheel.oaid;

import android.util.Log;

/**
 * 调试日志工具类
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public final class OAIDLog {

    private static final String TAG = "OAID";
    private static boolean ENABLE = false;

    private OAIDLog() {
        super();
    }

    /**
     * 启用调试日志
     */
    public static void setEnable(boolean enable) {
        ENABLE = enable;
    }

    /**
     * 打印调试日志
     *
     * @param log 日志信息
     */
    public static void print(Object log) {
        if (!ENABLE) {
            return;
        }
        if (log == null) {
            log = "<null>";
        }
        Log.i(TAG, log.toString());
    }
}
