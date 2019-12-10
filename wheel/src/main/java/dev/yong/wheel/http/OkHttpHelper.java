package dev.yong.wheel.http;

import java.lang.reflect.Type;

import dev.yong.wheel.utils.JSON;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * @author coderyong
 */
public class OkHttpHelper {

    private OkHttpClient httpClient;
    private OkHttpClient.Builder clientBuilder;
    private ParserFactory parserFactory;

    private OkHttpHelper() {
    }

    private static class OkHttpHelperHolder {
        private final static OkHttpHelper INSTANCE = new OkHttpHelper();
    }

    public static OkHttpHelper getInstance() {
        return OkHttpHelperHolder.INSTANCE;
    }

    /**
     * 创建OkHttp构建对象
     * <P>网络配置使用</P>
     *
     * @return OkHttpClient.Builder
     */
    public static OkHttpClient.Builder okHttp() {
        if (getInstance().clientBuilder == null) {
            getInstance().clientBuilder = new OkHttpClient.Builder();
        }
        return getInstance().clientBuilder;
    }

    /**
     * 设置解析工厂
     *
     * @param parserFactory ParserFactory
     */
    public static void setParserFactory(ParserFactory parserFactory) {
        getInstance().parserFactory = parserFactory;
    }

    /**
     * 获取接收工厂
     *
     * @return ParserFactory
     */
    static ParserFactory parserFactory() {
        if (getInstance().parserFactory == null) {
            getInstance().parserFactory = new ParserFactory() {
                @Override
                public <T> T parser(String content, Type type) {
                    if (content == null) {
                        return null;
                    }
                    return JSON.fromJson(content, type);
                }
            };
        }
        return getInstance().parserFactory;
    }

    /**
     * 创建OkHttpClient
     *
     * @return OkHttpClient
     */
    static OkHttpClient client() {
        if (getInstance().httpClient == null) {
            getInstance().httpClient = okHttp().build();
        }
        return getInstance().httpClient;
    }

    /**
     * 数据请求
     *
     * @param url 请求地址
     * @return RequestBuilder
     */
    public static RequestBuilder request(String url) {
        return RequestBuilder.request(url);
    }

    /**
     * 数据提交
     *
     * @param url 请求地址
     * @return RequestBuilder.SubmitBuilder
     */
    public static RequestBuilder.SubmitBuilder submit(String url) {
        return RequestBuilder.submit(url);
    }

    /**
     * 文件上传
     *
     * @param url 请求地址
     * @return RequestBuilder.MultipartBuilder
     */
    public static RequestBuilder.MultipartBuilder upload(String url) {
        return RequestBuilder.upload(url);
    }

    /**
     * 根据Tag取消请求
     *
     * @param tag
     */
    public void cancelTag(Object tag) {
        if (tag == null) return;
        for (Call call : client().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 取消所有请求请求
     */
    public static void cancelAll() {
        for (Call call : client().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : client().dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}
