package dev.yong.wheel.http.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * /**
 * 离线缓存拦截器
 *
 * @author coderyong
 */
@SuppressWarnings("unused")
public interface OfflineCacheInterceptor extends Interceptor {

    @NotNull
    @Override
    default Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!onNetworkUnavailable()) {
            //离线的时候的缓存的过期时间
            HttpUrl url = request.url();
            TreeMap<String, String> parameters = new TreeMap<>();
            for (int i = 0; i < url.querySize(); i++) {
                parameters.put(url.queryParameterName(i), url.queryParameterValue(i));
            }
            HttpUrl.Builder urlBuilder = url.newBuilder();
            for (String key : parameters.keySet()) {
                urlBuilder.removeAllQueryParameters(key);
                urlBuilder.addQueryParameter(key, parameters.get(key));
            }
            request = request.newBuilder()
                    .url(urlBuilder.build())
                    .cacheControl(
                            new CacheControl.Builder()
                                    .maxStale(offlineCacheTime(), TimeUnit.SECONDS)
                                    .onlyIfCached()
                                    .build()
                    )
                    .build();
        }
        return chain.proceed(request);
    }

    /**
     * 离线缓存过期时间，默认60（秒）
     *
     * @return 离线缓存时间
     */
    default int offlineCacheTime() {
        return 60;
    }

    /**
     * 网络可用
     *
     * @return true 不可用
     */
    boolean onNetworkUnavailable();
}
