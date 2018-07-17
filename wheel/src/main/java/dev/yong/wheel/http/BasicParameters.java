package dev.yong.wheel.http;

import java.util.Map;

/**
 * 基础参数处理类
 *
 * @author coderyong
 */
public interface BasicParameters {
    /**
     * 重组参数
     *
     * @param parameters 普通参数
     */
    void resetParameters(Map<String, String> parameters);
}
