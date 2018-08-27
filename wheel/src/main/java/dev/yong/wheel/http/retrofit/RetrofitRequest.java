package dev.yong.wheel.http.retrofit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import dev.yong.wheel.http.HttpInterceptor;
import dev.yong.wheel.http.HttpRequest;
import dev.yong.wheel.http.HttpResponse;
import dev.yong.wheel.utils.Logger;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author CoderYong
 */

public class RetrofitRequest implements HttpRequest {

    RetrofitService mService;
    String mUrl;
    Map<String, String> mParameters;

    private HttpInterceptor mInterceptor;
    protected HttpResponse mResponse;

    RetrofitRequest(RetrofitService service, String url, Map<String, String> parameters, HttpInterceptor interceptor) {
        this.mService = service;
        this.mUrl = url;
        this.mParameters = parameters;
        this.mInterceptor = interceptor;
    }

    protected Call<ResponseBody> call(String method) {
        if (mParameters != null && mParameters.size() > 0) {
            switch (method) {
                case GET:
                    return mService.get(mUrl, mParameters);
                case POST:
                    return mService.post(mUrl, mParameters);
                case HEAD:
                    return mService.head(mUrl, mParameters);
                case PUT:
                    return mService.put(mUrl, mParameters);
                case DELETE:
                    return mService.delete(mUrl, mParameters);
                case PATCH:
                    return mService.patch(mUrl, mParameters);
                default:
                    return mService.get(mUrl, mParameters);
            }
        } else {
            if (POST.equals(method)) {
                return mService.post(mUrl);
            } else {
                return mService.get(mUrl);
            }
        }
    }

    @Override
    public <T> void request(String method, HttpResponse<T> response) {
        call(method).enqueue(new HttpCallback<>(method, response));
    }

    class HttpCallback<T> implements retrofit2.Callback<ResponseBody> {

        private String mMethod;
        private HttpResponse<T> mResponse;

        HttpCallback(String method, HttpResponse<T> response) {
            this.mMethod = method;
            this.mResponse = response;
        }

        @Override
        public void onResponse(@NonNull final Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
            if (mResponse != null) {
                if (response.isSuccessful()) {
                    try {
                        final ResponseBody responseBody = response.body();
                        String body = "";
                        if (responseBody != null) {
                            body = responseBody.string();
                        }
                        if (mInterceptor != null) {
                            mInterceptor.onVerify(mParameters, body, new HttpInterceptor.Callback() {
                                @Override
                                public void retry(Map<String, String> parameters) {
                                    call.cancel();
                                    mParameters = parameters;
                                    request(mMethod, mResponse);
                                }

                                @Override
                                public void response(String responseBody) {
                                    mResponse.onSuccess(parse(responseBody));
                                }
                            });
                        } else {
                            mResponse.onSuccess(parse(body));
                        }
                    } catch (IOException e) {
                        Logger.w(e, e.getMessage());
                        mResponse.onFail(e);
                    }
                } else {
                    mResponse.onFail(new Exception("request failed, response's code is: " + response.code()));
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            if (mResponse != null) {
                mResponse.onFail(t);
            }
        }

        T parse(String content) {
            try {
                Type type = ((ParameterizedType) mResponse.getClass()
                        .getGenericInterfaces()[0]).getActualTypeArguments()[0];
                return new Gson().fromJson(content, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
