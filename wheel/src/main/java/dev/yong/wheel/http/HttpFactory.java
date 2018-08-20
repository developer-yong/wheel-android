package dev.yong.wheel.http;

import java.util.Map;

/**
 * @author coderyong
 */
public interface HttpFactory<T extends HttpRequest> {

    /**
     * 创建请求
     *
     * @param url         请求地址
     * @param parameters  请求参数
     * @param interceptor 请求拦截器
     * @return HttpRequest对象
     */
    HttpRequest createRequest(String url, Map<String, String> parameters, HttpInterceptor interceptor);

    /**
     * 创建上传
     *
     * @param url         上传地址
     * @param parameters  上传参数
     * @param interceptor 请求拦截器
     * @param listener    进度监听
     * @param files       上传文件数组
     * @return HttpRequest对象
     */
    HttpRequest createUpload(String url, Map<String, String> parameters, HttpInterceptor interceptor, UploadListener listener, UploadFile... files);

}
