package dev.yong.wheel.http;


import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import dev.yong.wheel.utils.Logger;

/**
 * @author coderyong
 */
public abstract class HttpResponse<T> {

    /**
     * 请求成功
     *
     * @param t 请求成功后得到的响应数据
     */
    public abstract void onSuccess(T t);

    /**
     * 请求失败
     *
     * @param t 错误信息
     */
    public abstract void onFail(Throwable t);

    /**
     * 响应内容处理
     *
     * @param responseBody 响应内容
     */
    public void responseHandle(String responseBody) {
        try {
            ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
            Type type = genericSuperclass.getActualTypeArguments()[0];
            onSuccess((T) new Gson().fromJson(responseBody, type));
        } catch (Exception e) {
            Logger.w(e, e.getMessage());
            onSuccess(null);
        }
    }

}
