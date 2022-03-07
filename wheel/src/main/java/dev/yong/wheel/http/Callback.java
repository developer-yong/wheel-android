package dev.yong.wheel.http;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author coderyong
 */
public interface Callback<T> extends okhttp3.Callback {

    @Override
    default void onResponse(@NotNull Call call, @NotNull Response response) {
        if (response.isSuccessful()) {
            try {
                //responseBody.string()只能在非UI线程中调用
                String body = Objects.requireNonNull(response.body()).string();
                T result = parse(body);
                new Handler(Looper.getMainLooper()).post(() -> {
                    onResponseString(body);
                    onResponse(result);
                });
            } catch (Exception e) {
                onFailure(call, new IOException(e));
            }
        } else {
            onFailure(call, new IOException("Request failed, response's code is: " + response.code()));
        }
    }

    @Override
    default void onFailure(@NotNull Call call, @NotNull IOException e) {
        new Handler(Looper.getMainLooper()).post(() -> onFailure(e));
    }

    @SuppressWarnings("unchecked")
    default T parse(String content) {
        Type type = ((ParameterizedType) getClass()
                .getGenericInterfaces()[0]).getActualTypeArguments()[0];
        if (type == String.class) {
            return (T) content;
        } else {
            return OkHttpHelper.parserFactory().parser(content, type);
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
     * @param t 错误信息
     */
    void onFailure(Throwable t);

    /**
     * 请求成功
     *
     * @param responseBody 请求成功后得到的响应数据
     */
    default void onResponseString(String responseBody) {
    }
}
