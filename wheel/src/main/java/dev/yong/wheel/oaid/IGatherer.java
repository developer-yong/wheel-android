package dev.yong.wheel.oaid;

/**
 * OAID采集者
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
public interface IGatherer {

    /**
     * 是否支持
     *
     * @return 支持则返回true，不支持则返回false
     */
    boolean isSupported();

    /**
     * 执行获取
     *
     * @param callback 采集回调
     */
    void doGather(IGatherCallback callback);

}
