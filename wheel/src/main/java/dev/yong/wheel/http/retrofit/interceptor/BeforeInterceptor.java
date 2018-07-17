package dev.yong.wheel.http.retrofit.interceptor;

import android.support.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author coderyong
 */
public interface BeforeInterceptor {
    /**
     * 响应前
     *
     * @param response response
     * @param chain chain
     * @return 返回处理后的响应内容，必须不为空
     */
    @NonNull
    Response onResponseBefore(Interceptor.Chain chain, Response response);

    /**
     * 请求前
     *
     * @param request request
     * @return 返回处理后的请求，必须不为空
     */
    @NonNull
    Request onRequestBefore(Request request);
}
