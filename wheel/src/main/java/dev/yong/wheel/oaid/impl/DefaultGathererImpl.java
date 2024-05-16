package dev.yong.wheel.oaid.impl;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;

/**
 * 默认OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
public class DefaultGathererImpl implements IGatherer {

    @Override
    public boolean isSupported() {
        return false;
    }

    @Override
    public void doGather(IGatherCallback getter) {
        if (getter == null) {
            return;
        }
        getter.onError(new OAIDException("Unsupported"));
    }
}
