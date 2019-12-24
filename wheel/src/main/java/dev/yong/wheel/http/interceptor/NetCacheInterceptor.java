package dev.yong.wheel.http.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import dev.yong.wheel.AppManager;
import dev.yong.wheel.network.Network;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 有网情况的缓存拦截器
 *
 * @author coderyong
 */
public class NetCacheInterceptor implements Interceptor {
    /**
     * 在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0 (秒)
     */
    public static int CACHE_TIME = 0;

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (Network.isAvailable(AppManager.getInstance().getApplication())) {
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
                    .header("Cache-Control", "public, max-age=" + CACHE_TIME)
                    .build();
        }
        return response;
    }
}
