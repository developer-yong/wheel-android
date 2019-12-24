package dev.yong.sample;

import androidx.annotation.NonNull;

import dev.yong.wheel.http.interceptor.BeforeInterceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author coderyong
 */
public class EncryptInterceptor implements BeforeInterceptor {

    @NonNull
    @Override
    public Response onResponseBefore(Chain chain, Response response) {
        return response;
    }

    @NonNull
    @Override
    public Request onRequestBefore(Request request) {
        return request;
    }
}
