package dev.yong.wheel.http.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 有网情况的缓存拦截器
 *
 * @author coderyong
 */
@SuppressWarnings("unused")
public interface NetCacheInterceptor extends Interceptor {

    @NotNull
    @Override
    default Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (onNetworkAvailable()) {
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
            return response.newBuilder()
                    .request(request.newBuilder().url(urlBuilder.build()).build())
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + onlineCacheTime())
                    .build();
        }
        return response;
    }

    /**
     * 在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0 (秒)
     *
     * @return 离线缓存时间
     */
    default int onlineCacheTime() {
        return 0;
    }

    /**
     * 网络可用
     *
     * @return true 可用
     */
    boolean onNetworkAvailable();
}
