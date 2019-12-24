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
 * 离线缓存拦截器
 *
 * @author coderyong
 */
public class OfflineCacheInterceptor implements Interceptor {

    /**
     * 离线缓存过期时间（秒）
     */
    public static int CACHE_TIME = 60;

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!Network.isAvailable(AppManager.getInstance().getApplication())) {
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
                    .cacheControl(new CacheControl.Builder()
                            .maxStale(CACHE_TIME, TimeUnit.SECONDS)
                            .onlyIfCached()
                            .build()
                    )
                    .build();
        }
        return chain.proceed(request);
    }
}
