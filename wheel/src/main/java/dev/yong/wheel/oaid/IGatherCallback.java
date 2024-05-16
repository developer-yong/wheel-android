package dev.yong.wheel.oaid;

/**
 * OAID采集回调
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
public interface IGatherCallback {

    /**
     * OAID获取成功
     *
     * @param oaid OAID
     */
    void onSuccessful(String oaid);

    /**
     * OAID获取失败（不正常或获取不到）
     *
     * @param error 错误信息
     */
    void onError(Throwable error);

}
