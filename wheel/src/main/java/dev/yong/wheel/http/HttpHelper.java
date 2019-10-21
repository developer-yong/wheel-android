package dev.yong.wheel.http;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 * @author coderyong
 */
public class HttpHelper {

    private OkHttpClient.Builder mClientBuilder;
    private Retrofit.Builder mRetrofitBuilder;

    private HttpHelper() {
    }

    private static class OkHttpHelperHolder {
        private final static HttpHelper INSTANCE = new HttpHelper();
    }

    public static HttpHelper getInstance() {
        return OkHttpHelperHolder.INSTANCE;
    }

    public static OkHttpClient.Builder okHttp() {
        if (getInstance().mClientBuilder == null) {
            getInstance().mClientBuilder = new OkHttpClient.Builder();
        }
        return getInstance().mClientBuilder;
    }

    public static Retrofit.Builder retrofit() {
        if (getInstance().mRetrofitBuilder == null) {
            getInstance().mRetrofitBuilder = new Retrofit.Builder();
        }
        return getInstance().mRetrofitBuilder;
    }

    public static RequestBuilder connect(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("Url must be not null");
        }
        if (!url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            throw new IllegalStateException("Invalid url");
        }
        return new RequestBuilder(url);
    }

    private OkHttpClient.Builder getClientBuilder() {
        if (mClientBuilder == null) {
            mClientBuilder = new OkHttpClient.Builder();
        }
        return mClientBuilder;
    }

    private Retrofit.Builder getRetrofitBuilder() {
        if (mRetrofitBuilder == null) {
            mRetrofitBuilder = new Retrofit.Builder();
        }
        return mRetrofitBuilder;
    }

    public static final class RequestBuilder {

        private String mUrl;
        private Map<String, String> mParams;
        private List<MultipartBody.Part> mParts;

        RequestBuilder(String url) {
            mUrl = url;
            if (mParams == null) {
                mParams = new HashMap<>();
            }
        }

        public RequestBuilder addParameter(String name, String value) {
            if (value == null) {
                value = "";
            }
            mParams.put(name, value);
            return this;
        }

        public RequestBuilder setParameter(Map<String, String> params) {
            mParams = params;
            return this;
        }

        public RequestBuilder addPart(String name, String value) {
            if (mParts == null) {
                mParts = new ArrayList<>();
            }
            mParts.add(MultipartBody.Part.createFormData(name, value));
            return this;
        }

        public RequestBuilder addPart(String name, String filePath, MediaType mediaType) {
            if (mParts == null) {
                mParts = new ArrayList<>();
            }
            File file = new File(filePath);
            mParts.add(MultipartBody.Part.createFormData(name, file.getName(), RequestBody.create(mediaType, file)));
            return this;
        }

        public RequestBuilder addPart(String name, String filename, RequestBody body) {
            if (mParts == null) {
                mParts = new ArrayList<>();
            }
            mParts.add(MultipartBody.Part.createFormData(name, filename, body));
            return this;
        }

        public <T> void get(Callback<T> callback) {
            create().get(mUrl, mParams).enqueue(callback);
        }

        public <T> void post(Callback<T> callback) {
            create().post(mUrl, mParams).enqueue(callback);
        }

        public <T> void head(Callback<T> callback) {
            create().head(mUrl, mParams).enqueue(callback);
        }

        public <T> void put(Callback<T> callback) {
            create().put(mUrl, mParams).enqueue(callback);
        }

        public <T> void patch(Callback<T> callback) {
            create().patch(mUrl, mParams).enqueue(callback);
        }

        public <T> void delete(Callback<T> callback) {
            create().delete(mUrl, mParams).enqueue(callback);
        }

        public <T> void download(Callback<T> callback) {
            create().download(mUrl).enqueue(callback);
        }

        public <T> void upload(Callback<T> callback) {
            if (mParts == null) {
                mParts = new ArrayList<>();
            }
            Map<String, RequestBody> params = new HashMap<>();
            if (mParams != null) {
                for (String key : mParams.keySet()) {
                    params.put(key, RequestBody.create(MediaType
                            .parse("text/plain"), Objects.requireNonNull(mParams.get(key))));
                }
            }
            create().upload(mUrl, params, mParts).enqueue(callback);
        }

        private RetrofitService create() {
            String baseUrl = mUrl.replaceAll("(.*//[^/]*).*", "$1");
            return getInstance().getRetrofitBuilder().baseUrl(baseUrl)
                    .client(getInstance().getClientBuilder().build())
                    .build().create(RetrofitService.class);
        }
    }
}
