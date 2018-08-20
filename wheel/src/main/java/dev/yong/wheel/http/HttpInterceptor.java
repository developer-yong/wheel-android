package dev.yong.wheel.http;

import java.util.Map;

/**
 * @author coderyong
 */
public interface HttpInterceptor {
    /**
     * 重组参数
     *
     * @param parameters 普通参数
     */
    void resetParameters(Map<String, String> parameters);

    /**
     * 请求拦截器
     * <p>
     * 用于Token验证
     * </P>
     *
     * @param parameters   请求参数
     * @param responseBody 请求响应内容
     * @param listener     token监听类
     */
    void onVerify(Map<String, String> parameters, String responseBody, Callback listener);

    /**
     * 拦截回调执行类
     */
    interface Callback {
        /**
         * 重新请求
         *
         * @param parameters 重新请求的参数
         */
        void retry(Map<String, String> parameters);

        /**
         * token通过
         *
         * @param responseBody 响应内容
         */
        void response(String responseBody);
    }
}
