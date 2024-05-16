package dev.yong.wheel.oaid;

import android.content.Context;

/**
 * OAID获取工具类
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public final class OAID {

    private OAID() {
        super();
    }

    /**
     * 获取OAID
     *
     * @param context AppContext
     * @param getter  IGatherCallback
     */
    public static void get(Context context, IGatherCallback getter) {
        IGatherer gatherer = GathererFactory.create(context);
        OAIDLog.print("OAID implements class: " + gatherer.getClass().getName());
        gatherer.doGather(getter);
    }

    /**
     * 是否支持OAID获取
     *
     * @param context AppContext
     * @return 支持则返回true，不支持则返回false
     */
    public static boolean isSupported(Context context) {
        return GathererFactory.create(context).isSupported();
    }
}
