package dev.yong.wheel.http;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import dev.yong.wheel.utils.Logger;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author coderyong
 */
public interface Callback<T> extends okhttp3.Callback {

    default void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) {
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    onResponse("");
                    onResponse((T) null);
                });
            } else {
                try {
                    //responseBody.string()只能在非UI线程中调用
                    String body = responseBody.string();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        onResponse(body);
                        onResponse(parse(body));
                    });
                } catch (IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> onFailure(e, response.body()));
                }
            }
        } else {
            new Handler(Looper.getMainLooper()).post(() ->
                    onFailure(new Exception("Request failed, response's code is: " + response.code()), response.body()));
        }

    }

    default void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
        new Handler(Looper.getMainLooper()).post(() -> onFailure(e, null));
    }

    default T parse(String content) {
        Type type = ((ParameterizedType) getClass()
                .getGenericInterfaces()[0]).getActualTypeArguments()[0];
        try {
            return OkHttpHelper.parserFactory().parser(content, type);
        } catch (Exception e) {
            Logger.e(e);
            return null;
        }
    }

    /**
     * 请求成功
     *
     * @param t 请求成功后得到的响应数据
     */
    void onResponse(T t);

    /**
     * 请求失败
     *
     * @param t            错误信息
     * @param responseBody 响应内容
     */
    void onFailure(Throwable t, ResponseBody responseBody);

    /**
     * 请求成功
     *
     * @param responseBody 请求成功后得到的响应数据
     */
    default void onResponse(String responseBody) {
    }
}