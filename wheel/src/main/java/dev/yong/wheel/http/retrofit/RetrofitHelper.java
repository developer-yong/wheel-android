package dev.yong.wheel.http.retrofit;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dev.yong.wheel.BuildConfig;
import dev.yong.wheel.http.retrofit.interceptor.BeforeInterceptor;
import dev.yong.wheel.http.retrofit.interceptor.LoggerInterceptor;
import dev.yong.wheel.http.retrofit.interceptor.ProgressInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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

    public <T> T create(Class<T> service) {
        if (mRetrofit == null) {
            if (TextUtils.isEmpty(mBaseUrl)) {
                throw new IllegalStateException("BaseUrl not initialized, RetrofitHelper.baseUrl(String) not implemented");
            }
            OkHttpClient.Builder builder = defaultClientBuilder();
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
            mRetrofit = defaultRetrofitBuilder()
                    .baseUrl(mBaseUrl)
                    .client(builder.build())
                    .build();
        }
        return mRetrofit.create(service);
    }


    public static OkHttpClient.Builder defaultClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //打印网络日志
        if (BuildConfig.DEBUG) {
            // Log信息拦截器
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            //设置 Debug Log 模式
            builder.addInterceptor(new LoggerInterceptor());
        }

        //设置超时
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        //错误重连
        builder.retryOnConnectionFailure(ERROR_RECONNECTION);
        return builder;
    }

    public static Retrofit.Builder defaultRetrofitBuilder() {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

}
