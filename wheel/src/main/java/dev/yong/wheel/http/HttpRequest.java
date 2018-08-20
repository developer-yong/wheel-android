package dev.yong.wheel.http;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author coderyong
 */
public interface HttpRequest {

    String GET = "GET";
    String POST = "POST";
    String HEAD = "HEAD";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String PATCH = "PATCH";

    @StringDef({GET, POST, HEAD, PUT, DELETE, PATCH})
    @Retention(RetentionPolicy.SOURCE)
    @interface Method {
    }

    /**
     * 响应请求
     *
     * @param method   请求方式
     * @param response 请求响应
     */
    <T> void request(@Method String method, HttpResponse<T> response);
}
