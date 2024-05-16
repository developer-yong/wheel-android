package dev.yong.wheel.http;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public final class Transfer {

    /**
     * 初始化网络请求
     *
     * @param builder OkHttpClient构建对象
     */
    public static void init(OkHttpClient.Builder builder) {
        if (builder == null) {
            builder = new OkHttpClient.Builder();
        }
        TransferHolder.INSTANCE.mClient = builder.build();
    }

    /**
     * 初始化网络请求
     *
     * @param builder   OkHttpClient构建对象
     * @param mediaType Global MediaType
     */
    public static void init(OkHttpClient.Builder builder, MediaType mediaType) {
        init(builder);
        TransferHolder.INSTANCE.mMediaType = mediaType;
    }

    /**
     * 初始化网络请求
     *
     * @param builder      OkHttpClient构建对象
     * @param globalParams Global Params
     */
    public static void init(OkHttpClient.Builder builder, Map<String, String> globalParams) {
        init(builder);
        TransferHolder.INSTANCE.mGlobalParams = globalParams;
    }

    /**
     * 初始化网络请求
     *
     * @param builder      OkHttpClient构建对象
     * @param mediaType    Global MediaType
     * @param globalParams Global Params
     */
    public static void init(OkHttpClient.Builder builder, MediaType mediaType, Map<String, String> globalParams) {
        init(builder, mediaType);
        TransferHolder.INSTANCE.mGlobalParams = globalParams;
    }

    public static OkHttpClient client() {
        if (TransferHolder.INSTANCE.mClient == null) {
            TransferHolder.INSTANCE.mClient = new OkHttpClient();
        }
        return TransferHolder.INSTANCE.mClient;
    }

    public static MediaType mediaType() {
        return TransferHolder.INSTANCE.mMediaType;
    }

    public static Map<String, String> globalParams() {
        return TransferHolder.INSTANCE.mGlobalParams;
    }

    public static Requester with(String url, RequestMethod method) {
        return new Requester(url, method);
    }

    public static Requester get(String url) {
        return new Requester(url, RequestMethod.GET);
    }

    public static BodyRequester post(String url) {
        return new BodyRequester(url, RequestMethod.POST);
    }

    public static UploadRequester upload(String url) {
        return new UploadRequester(url);
    }

    /**
     * 根据Tag取消请求
     *
     * @param tag Tag
     */
    public static void cancel(Object tag) {
        Dispatcher dispatcher = client().dispatcher();
        for (Call call : dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel();
            }
        }
    }

    public static void cancelAll() {
        client().dispatcher().cancelAll();
    }

    private OkHttpClient mClient;
    private MediaType mMediaType;
    private Map<String, String> mGlobalParams;

    private static class TransferHolder {
        private static final Transfer INSTANCE = new Transfer();
    }
}
