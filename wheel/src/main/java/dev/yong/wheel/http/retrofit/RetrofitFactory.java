package dev.yong.wheel.http.retrofit;

import android.text.TextUtils;

import java.util.Map;

import dev.yong.wheel.http.HttpConfig;
import dev.yong.wheel.http.HttpFactory;
import dev.yong.wheel.http.HttpInterceptor;
import dev.yong.wheel.http.HttpRequest;
import dev.yong.wheel.http.UploadFile;
import dev.yong.wheel.http.UploadListener;
import dev.yong.wheel.http.retrofit.interceptor.ProgressInterceptor;

import static dev.yong.wheel.http.retrofit.interceptor.ProgressInterceptor.PROGRESS_UPLOAD;


/**
 * @author coderyong
 */
public class RetrofitFactory implements HttpFactory<RetrofitRequest> {

    @Override
    public HttpRequest createRequest(String url, Map<String, String> parameters, HttpInterceptor interceptor) {
        return new RetrofitRequest(createService(url, RetrofitService.class), url, parameters, interceptor);
    }

    @Override
    public HttpRequest createUpload(String url, Map<String, String> parameters, HttpInterceptor interceptor, final UploadListener listener, UploadFile... files) {
        if (listener != null) {
            //添加进度监听拦截器
            RetrofitHelper.getInstance()
                    .progressInterceptor(new ProgressInterceptor(PROGRESS_UPLOAD, new ProgressInterceptor.ProgressListener() {
                        @Override
                        public void onProgress(long currentLength, long totalLength, boolean done) {
                            listener.onProgress(currentLength, totalLength, done);
                        }
                    }));
        }
        return new RetrofitUpload(createService(url, RetrofitService.class), url, parameters, interceptor, files);
    }

    /**
     * 构建Retrofit实体
     *
     * @param url 请求地址
     * @return Retrofit实体
     */
    private <T> T createService(String url, Class<T> service) {

        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("Url must be not null");
        }
        if (!url.matches(HttpConfig.URL_REGEX)) {
            throw new IllegalStateException("Invalid url");
        }
        String baseUrl = url.replaceAll("(.*//[^/]*).*", "$1");

        return RetrofitHelper.getInstance().baseUrl(baseUrl).create(service);
    }
}
