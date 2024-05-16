package dev.yong.wheel.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.reflect.TypeToken;
import dev.yong.wheel.utils.JSON;
import dev.yong.wheel.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

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
                Result.call(this, parse(body));
            } catch (Exception e) {
                onFailure(call, new IOException(e));
            }
        } else {
            onFailure(call, new IOException("Request failed, response's code is: " + response.code()));
        }
    }

    @Override
    default void onFailure(@NotNull Call call, @NotNull IOException e) {
        //网络请求失败，服务器链接异常或者数据解析异常
        Logger.e(e, call.request().url().toString());
        Result.call(this, e);
    }

    @SuppressWarnings("unchecked")
    default T parse(String body) {
        Type type = findCallbackGenericType();
        if (type == String.class) {
            return (T) body;
        } else {
            return JSON.fromJson(body, type);
        }
    }

    default Type findCallbackGenericType() {
        Type parameterizedType = null;
        Type typeArgument = null;
        for (Type gInterface : getClass().getGenericInterfaces()) {
            if (gInterface.toString().contains(Callback.class.getName())) {
                parameterizedType = ((ParameterizedType) gInterface).getActualTypeArguments()[0];
                break;
            } else {
                for (Class<?> sInterface : getClass().getInterfaces()) {
                    for (Type sGInterface : sInterface.getGenericInterfaces()) {
                        if (sGInterface.toString().contains(Callback.class.getName())) {
                            typeArgument = ((ParameterizedType) gInterface).getActualTypeArguments()[0];
                            parameterizedType = ((ParameterizedType) sGInterface).getActualTypeArguments()[0];
                            if (parameterizedType instanceof ParameterizedType) {
                                parameterizedType = ((ParameterizedType) parameterizedType).getRawType();
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (typeArgument == null) {
            return parameterizedType;
        } else {
            return TypeToken.getParameterized(parameterizedType, typeArgument).getType();
        }
    }

    /**
     * 请求成功
     *
     * @param t 请求成功后得到的响应数据
     */
    void onResponse(@NotNull T t);

    /**
     * 请求失败
     *
     * @param t 错误信息
     */
    default void onFailed(@NotNull Throwable t) {
    }

    class Result<T> implements Runnable {

        private final Callback<T> mCallback;
        private T mResult;
        private Throwable mThrowable;

        public Result(Callback<T> callback, T result) {
            mCallback = callback;
            mResult = result;
        }

        public Result(Callback<T> callback, Throwable throwable) {
            mCallback = callback;
            mThrowable = throwable;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            if (mCallback != null) {
                if (mResult != null) {
                    mCallback.onResponse(mResult);
                }
                if (mThrowable != null) {
                    mCallback.onFailed(mThrowable);
                }
            }
        }

        static <T> void call(Callback<T> callback, T result) {
            new Handler(Looper.getMainLooper()).post(new Result<>(callback, result));
        }

        static <T> void call(Callback<T> callback, Throwable throwable) {
            new Handler(Looper.getMainLooper()).post(new Result<>(callback, throwable));
        }
    }
}
