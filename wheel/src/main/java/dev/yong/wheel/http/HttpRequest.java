package dev.yong.wheel.http;

/**
 * @author coderyong
 */
public interface HttpRequest {
    /**
     * 无回调GET请求
     */
    void get();

    /**
     * 回调GET请求
     *
     * @param response 请求响应
     */
    void get(HttpResponse response);

    /**
     * 无回调POST请求
     */
    void post();

    /**
     * 回调POST请求
     *
     * @param response 请求响应
     */
    void post(HttpResponse response);
}
