package dev.yong.wheel.http;

import java.util.Map;

/**
 * @author coderyong
 */
public interface HttpFactory {

    /**
     * 创建请求
     *
     * @param url 请求地址
     * @return HttpRequest对象
     */
    HttpRequest createRequest(String url);

    /**
     * 创建请求
     *
     * @param url        请求地址
     * @param parameters 请求参数
     * @return HttpRequest对象
     */
    HttpRequest createRequest(String url, Map<String, String> parameters);

    /**
     * 创建上传
     *
     * @param url      上传地址
     * @param listener 进度监听
     * @param files    上传文件数组
     * @return HttpRequest对象
     */
    HttpRequest createUpload(String url, UploadListener listener, UploadFile... files);

    /**
     * 创建上传
     *
     * @param url        上传地址
     * @param parameters 上传参数
     * @param listener   进度监听
     * @param files      上传文件数组
     * @return HttpRequest对象
     */
    HttpRequest createUpload(String url, Map<String, String> parameters, UploadListener listener, UploadFile... files);

}
