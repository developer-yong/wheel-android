package dev.yong.wheel.http.retrofit;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dev.yong.wheel.BuildConfig;
import dev.yong.wheel.http.retrofit.interceptor.BeforeInterceptor;
import dev.yong.wheel.http.retrofit.interceptor.LoggerInterceptor;
import dev.yong.wheel.http.retrofit.interceptor.ProgressInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static dev.yong.wheel.http.HttpConfig.CONNECT_TIMEOUT;
import static dev.yong.wheel.http.HttpConfig.ERROR_RECONNECTION;
import static dev.yong.wheel.http.HttpConfig.READ_TIMEOUT;
import static dev.yong.wheel.http.HttpConfig.WRITE_TIMEOUT;

/**
 * @author coderyong
 */
public class RetrofitHelper {

    private String mBaseUrl;
    private Retrofit mRetrofit;
    private BeforeInterceptor mBeforeInterceptor;
    private ProgressInterceptor mProgressInterceptor;
    private OkHttpClient.Builder mClientBuilder;
    private Converter.Factory mConverterFactory;

    private RetrofitHelper() {
    }

    private static class RetrofitHelperHolder {
        private final static RetrofitHelper INSTANCE = new RetrofitHelper();
    }

    public static RetrofitHelper getInstance() {
        return RetrofitHelperHolder.INSTANCE;
    }

    public RetrofitHelper baseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
        return this;
    }

    public RetrofitHelper beforeInterceptor(BeforeInterceptor beforeInterceptor) {
        this.mBeforeInterceptor = beforeInterceptor;
        return this;
    }

    public RetrofitHelper progressInterceptor(ProgressInterceptor progressInterceptor) {
        this.mProgressInterceptor = progressInterceptor;
        return this;
    }

    public RetrofitHelper clientBuilder(OkHttpClient.Builder clientBuilder) {
        this.mClientBuilder = clientBuilder;
        return this;
    }

    public RetrofitHelper setConverterFactory(Converter.Factory converterFactory) {
        this.mConverterFactory = converterFactory;
        return this;
    }

    public <T> T create(Class<T> service) {
        if (mRetrofit == null) {
            if (TextUtils.isEmpty(mBaseUrl)) {
                throw new IllegalStateException("BaseUrl not initialized, RetrofitHelper.baseUrl(String) not implemented");
            }
            OkHttpClient.Builder builder = mClientBuilder == null ? defaultClientBuilder() : mClientBuilder;
            if (mBeforeInterceptor != null) {
                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        return mBeforeInterceptor.onResponseBefore(chain,
                                chain.proceed(mBeforeInterceptor.onRequestBefore(chain.request())));
                    }
                });
            }
            if (mProgressInterceptor != null) {
                builder.addInterceptor(mProgressInterceptor);
            }
            //打印网络日志
            if (BuildConfig.DEBUG) {
                //设置 Debug Log 模式
                builder.addInterceptor(new LoggerInterceptor());
            }
            mRetrofit = defaultRetrofitBuilder(mConverterFactory)
                    .baseUrl(mBaseUrl)
                    .client(builder.build())
                    .build();
        }
        return mRetrofit.create(service);
    }


    public static OkHttpClient.Builder defaultClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //设置超时
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        //错误重连
        builder.retryOnConnectionFailure(ERROR_RECONNECTION);
        return builder;
    }

    public static Retrofit.Builder defaultRetrofitBuilder() {
        return defaultRetrofitBuilder(GsonConverterFactory.create());
    }

    public static Retrofit.Builder defaultRetrofitBuilder(Converter.Factory factory) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(factory == null ? GsonConverterFactory.create() : factory);
    }

}
