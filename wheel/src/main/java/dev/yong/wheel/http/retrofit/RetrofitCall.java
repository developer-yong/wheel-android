package dev.yong.wheel.http.retrofit;


import android.support.annotation.NonNull;

import java.io.IOException;

import dev.yong.wheel.http.HttpRequest;
import dev.yong.wheel.http.HttpResponse;
import dev.yong.wheel.http.ResolveFactory;
import dev.yong.wheel.http.ResponseVerify;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author coderyong
 */
public abstract class RetrofitCall implements HttpRequest, Callback<ResponseBody> {

    private HttpResponse mResponse;

    /**
     * 创建GET回调执行类
     *
     * @return 回调执行类
     */
    protected abstract Call<ResponseBody> getCall();

    /**
     * 创建POST回调执行类
     *
     * @return 回调执行类
     */
    protected abstract Call<ResponseBody> postCall();

    @Override
    public void get() {
        get(null);
    }

    @Override
    public void get(HttpResponse response) {
        this.mResponse = response;
        getCall().enqueue(this);
    }

    @Override
    public void post() {
        post(null);
    }

    @Override
    public void post(HttpResponse response) {
        this.mResponse = response;
        postCall().enqueue(this);
    }

    @Override
    public void onResponse(@NonNull final Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        if (mResponse != null) {
            try {
                ResponseBody responseBody = response.body();
                String body = "";
                if (responseBody != null) {
                    body = responseBody.string();
                }
                ResolveFactory factory = mResponse.getFactory();
                ResponseVerify verify = mResponse.getVerify();
                int code = factory == null ? response.code() : factory.createCode(body);
                String message = factory == null ? response.message() : factory.createMessage(body);
                if (verify != null) {
                    verify.verify(new ResponseVerify.VerifyListener() {

                        @Override
                        public void onSuccess() {
                            call.clone().enqueue(RetrofitCall.this);
                        }

                        @Override
                        public void onFail(int code, String message) {
                            mResponse.responseHandle(code, message);
                        }
                    }, code, message);
                } else {
                    mResponse.responseHandle(response.code(), body);
                }

            } catch (IOException e) {
                mResponse.responseHandle(response.code(), e.getMessage());
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
        if (mResponse != null) {
            mResponse.onFail(t);
        }
    }
}
