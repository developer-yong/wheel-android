package dev.yong.wheel.http.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author coderyong
 */
public class ProgressInterceptor implements Interceptor {

    public static int PROGRESS_REQUEST = 0x01;
    public static int PROGRESS_RESPONSE = 0x02;

    private ProgressListener mListener;
    private int mType;


    /**
     * 用于上传/下载文件进度拦截器
     *
     * @param listener     进度监听
     * @param progressType 监听类型
     */
    public ProgressInterceptor(int progressType, ProgressListener listener) {
        this.mType = progressType;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        if (mType == PROGRESS_REQUEST) {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .method(original.method(), new ProgressRequestBody(original.body(), mListener))
                    .build();

            return chain.proceed(request);
        } else {
            //拦截
            Response response = chain.proceed(chain.request());

            //包装响应体并返回
            return response.newBuilder()
                    .body(new ProgressResponseBody(response.body(), mListener))
                    .build();
        }
    }

    public interface ProgressListener {
        /**
         * 进度监听
         *
         * @param currentLength 当前长度
         * @param totalLength   总长度
         * @param done          是否完成或者失效
         */
        void onProgress(long currentLength, long totalLength, boolean done);
    }
}
