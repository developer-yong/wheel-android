package dev.yong.wheel.http;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import dev.yong.wheel.utils.Logger;

/**
 * @author coderyong
 */
public abstract class HttpResponse<T> {

    private ResolveFactory mFactory;
    private String mMessage;
    private ResponseVerify mVerify;

    /**
     * 请求成功
     *
     * @param code code码
     * @param t    请求成功后得到的响应数据
     */
    public abstract void onSuccess(int code, T t);

    /**
     * 请求失败
     *
     * @param t 错误信息
     */
    public abstract void onFail(Throwable t);

    public ResolveFactory getFactory() {
        return mFactory;
    }

    public void setFactory(ResolveFactory factory) {
        this.mFactory = factory;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public ResponseVerify getVerify() {
        return mVerify;
    }

    public void setVerify(ResponseVerify verify) {
        this.mVerify = verify;
    }

    /**
     * 响应内容处理
     *
     * @param code         code响应状态码
     * @param responseBody 响应内容
     */
    public void responseHandle(int code, String responseBody) {
        if (code == 200) {
            ResolveFactory factory = getFactory();
            setMessage(factory.createMessage(responseBody));
            try {
                ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
                Type type = genericSuperclass.getActualTypeArguments()[0];
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Class<?> tClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    onSuccess(factory.createCode(responseBody), (T) factory.createList(responseBody, tClass));
                } else {
                    Class<T> tClass = (Class<T>) type;
                    onSuccess(factory.createCode(responseBody), factory.createObject(responseBody, tClass));
                }
            } catch (Exception e) {
                Logger.w(e, responseBody);
                setMessage(e.getMessage());
                onSuccess(code, null);
            }
        } else {
            Logger.w(responseBody);
            setMessage(responseBody);
            onSuccess(code, null);
        }
    }

}
