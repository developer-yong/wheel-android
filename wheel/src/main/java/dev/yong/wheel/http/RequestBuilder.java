package dev.yong.wheel.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dev.yong.wheel.http.interceptor.ProgressInterceptor;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

import static dev.yong.wheel.http.interceptor.ProgressInterceptor.PROGRESS_REQUEST;
import static dev.yong.wheel.http.interceptor.ProgressInterceptor.PROGRESS_RESPONSE;

public class RequestBuilder extends Request.Builder {

    private HttpUrl.Builder urlBuilder;

    RequestBuilder(String url) {
        if (url == null) {
            throw new NullPointerException("Url must be not null");
        }
        if (!url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            throw new IllegalStateException("Invalid url");
        }
        urlBuilder = HttpUrl.get(url).newBuilder();
    }

    static RequestBuilder request(String url) {
        return new RequestBuilder(url);
    }

    static SubmitBuilder submit(String url) {
        return new SubmitBuilder(url);
    }

    static MultipartBuilder upload(String url) {
        return new MultipartBuilder(url);
    }

    /************************ HttpUrl.Builder ************************/
    public RequestBuilder addPathSegment(String pathSegment) {
        urlBuilder.addPathSegment(pathSegment);
        return this;
    }

    /**
     * Adds a set of path segments separated by a slash (either {@code \} or {@code /}). If
     * {@code pathSegments} starts with a slash, the resulting URL will have empty path segment.
     */
    public RequestBuilder addPathSegments(String pathSegments) {
        urlBuilder.addPathSegments(pathSegments);
        return this;
    }

    public RequestBuilder addEncodedPathSegment(String encodedPathSegment) {
        urlBuilder.addEncodedPathSegment(encodedPathSegment);
        return this;
    }

    /**
     * Adds a set of encoded path segments separated by a slash (either {@code \} or {@code /}). If
     * {@code encodedPathSegments} starts with a slash, the resulting URL will have empty path
     * segment.
     */
    public RequestBuilder addEncodedPathSegments(String encodedPathSegments) {
        urlBuilder.addEncodedPathSegments(encodedPathSegments);
        return this;
    }

    public RequestBuilder setPathSegment(int index, String pathSegment) {
        urlBuilder.setPathSegment(index, pathSegment);
        return this;
    }

    public RequestBuilder setEncodedPathSegment(int index, String encodedPathSegment) {
        urlBuilder.setEncodedPathSegment(index, encodedPathSegment);
        return this;
    }

    public RequestBuilder removePathSegment(int index) {
        urlBuilder.removePathSegment(index);
        return this;
    }

    public RequestBuilder encodedPath(String encodedPath) {
        urlBuilder.encodedPath(encodedPath);
        return this;
    }

    public RequestBuilder query(@Nullable String query) {
        urlBuilder.query(query);
        return this;
    }

    public RequestBuilder encodedQuery(@Nullable String encodedQuery) {
        urlBuilder.encodedQuery(encodedQuery);
        return this;
    }

    /**
     * Encodes the query parameter using UTF-8 and adds it to this URL's query string.
     */
    public RequestBuilder addQueryParameter(String name, @Nullable String value) {
        urlBuilder.addQueryParameter(name, value);
        return this;
    }

    /**
     * Adds the pre-encoded query parameter to this URL's query string.
     */
    public RequestBuilder addEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
        urlBuilder.addEncodedQueryParameter(encodedName, encodedValue);
        return this;
    }

    public RequestBuilder setQueryParameter(String name, @Nullable String value) {
        urlBuilder.setQueryParameter(name, value);
        return this;
    }

    public RequestBuilder setEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
        urlBuilder.setEncodedQueryParameter(encodedName, encodedValue);
        return this;
    }

    public RequestBuilder removeAllQueryParameters(String name) {
        urlBuilder.removeAllQueryParameters(name);
        return this;
    }

    public RequestBuilder removeAllEncodedQueryParameters(String encodedName) {
        urlBuilder.removeAllEncodedQueryParameters(encodedName);
        return this;
    }

    public RequestBuilder fragment(@Nullable String fragment) {
        urlBuilder.fragment(fragment);
        return this;
    }

    public RequestBuilder encodedFragment(@Nullable String encodedFragment) {
        urlBuilder.encodedFragment(encodedFragment);
        return this;
    }
    /************************ HttpUrl.Builder ************************/

    /************************ Headers.Builder ************************/
    @NonNull
    @Override
    public RequestBuilder header(String name, String value) {
        super.header(name, value);
        return this;
    }

    @NonNull
    @Override
    public RequestBuilder addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @NonNull
    @Override
    public RequestBuilder removeHeader(@NonNull String name) {
        super.removeHeader(name);
        return this;
    }

    @NonNull
    @Override
    public RequestBuilder headers(Headers headers) {
        super.headers(headers);
        return this;
    }

    @NonNull
    @Override
    public RequestBuilder cacheControl(CacheControl cacheControl) {
        super.cacheControl(cacheControl);
        return this;
    }

    /************************ Headers.Builder ************************/

    public <T> void get(Callback<T> callback) {
        url(urlBuilder.build());
        OkHttpHelper.client()
                .newCall(get().build())
                .enqueue(callback);
    }

    public <T> void head(Callback<T> callback) {
        url(urlBuilder.build());
        OkHttpHelper.client()
                .newCall(head().build())
                .enqueue(callback);
    }

    public <T> void post(Callback<T> callback) {
        url(urlBuilder.build());
        OkHttpHelper.client()
                .newCall(post(create()).build())
                .enqueue(callback);
    }

    public <T> void delete(Callback<T> callback) {
        url(urlBuilder.build());
        RequestBody requestBody = create();
        OkHttpHelper.client()
                .newCall(delete(requestBody == null ? Util.EMPTY_REQUEST : requestBody).build())
                .enqueue(callback);
    }

    public <T> void put(Callback<T> callback) {
        url(urlBuilder.build());
        OkHttpHelper.client()
                .newCall(put(create()).build())
                .enqueue(callback);
    }

    public <T> void patch(Callback<T> callback) {
        url(urlBuilder.build());
        OkHttpHelper.client()
                .newCall(patch(create()).build())
                .enqueue(callback);
    }

    RequestBody create() {
        return null;
    }

    public static class SubmitBuilder extends RequestBuilder {

        private MediaType mediaType;
        private FormBody.Builder formBuilder;
        private String jsonContent;

        SubmitBuilder(String url) {
            super(url);
            formBuilder = new FormBody.Builder();
        }

        public SubmitBuilder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public SubmitBuilder add(String name, String... values) {
            if (values != null) {
                for (String value : values) {
                    formBuilder.add(name, value);
                }
            }
            return this;
        }

        public SubmitBuilder add(Map<String, String> params) {
            if (params != null) {
                for (String name : params.keySet()) {
                    String value = params.get(name);
                    if (name != null && value != null) {
                        formBuilder.add(name, value);
                    }
                }
            }
            return this;
        }

        public SubmitBuilder addEncoded(String name, String... values) {
            if (values != null) {
                for (String value : values) {
                    formBuilder.addEncoded(name, value);
                }
            }
            return this;
        }

        public SubmitBuilder addEncoded(Map<String, String> params) {
            if (params != null) {
                for (String name : params.keySet()) {
                    String value = params.get(name);
                    if (name != null && value != null) {
                        formBuilder.addEncoded(name, value);
                    }
                }
            }
            return this;
        }

        /**
         * 当 MediaType 为 application/json 是使用此方法设置 Json 内容
         * <p>
         * 此方法调用时将替换所有已添加的参数
         * </P>
         *
         * @param jsonContent Json 字符串内容
         * @return SubmitBuilder
         */
        public SubmitBuilder setJson(String jsonContent) {
            setMediaType(MediaType.parse("application/json"));
            this.jsonContent = jsonContent;
            return this;
        }

        public SubmitBuilder addResponseProgressListener(ProgressInterceptor.ProgressListener progressListener) {
            OkHttpHelper.okHttp().addInterceptor(new ProgressInterceptor(PROGRESS_RESPONSE, progressListener));
            return this;
        }

        @Override
        RequestBody create() {
            if (mediaType != null && "application/json".equals(mediaType.toString())) {
                if (jsonContent != null && !"".equals(jsonContent.trim())) {
                    return RequestBody.create(mediaType, jsonContent);
                }
                Map<String, String> parameters = new HashMap<>();
                FormBody formBody = formBuilder.build();
                for (int i = 0; i < formBody.size(); i++) {
                    parameters.put(formBody.name(i), formBody.value(i));
                }
                return RequestBody.create(mediaType, new JSONObject(parameters).toString());
            } else {
                return formBuilder.build();
            }
        }
    }

    public static final class MultipartBuilder extends RequestBuilder {

        private MultipartBody.Builder multipartBuilder;

        MultipartBuilder(String url) {
            super(url);
            multipartBuilder = new MultipartBody.Builder();
        }

        /**
         * Set the MIME type. Expected values for {@code type} are
         * {@link MultipartBody.Builder#MIXED} (the default),
         * {@link MultipartBody.Builder#ALTERNATIVE},
         * {@link MultipartBody.Builder#DIGEST},
         * {@link MultipartBody.Builder#PARALLEL} and
         * {@link MultipartBody.Builder#FORM}.
         */
        public MultipartBuilder setType(MediaType type) {
            multipartBuilder.setType(type);
            return this;
        }

        /**
         * Add a part to the body.
         */
        public MultipartBuilder addPart(MultipartBody.Part part) {
            multipartBuilder.addPart(part);
            return this;
        }

        /**
         * Add a part to the body.
         */
        public MultipartBuilder addPart(RequestBody body) {
            multipartBuilder.addPart(MultipartBody.Part.create(body));
            return this;
        }

        /**
         * Add a part to the body.
         */
        public MultipartBuilder addPart(@Nullable Headers headers, RequestBody body) {
            multipartBuilder.addPart(MultipartBody.Part.create(headers, body));
            return this;
        }

        /**
         * Add a form data part to the body.
         */
        public MultipartBuilder addFormDataPart(String name, String... values) {
            if (values != null) {
                for (String value : values) {
                    multipartBuilder.addPart(MultipartBody.Part.createFormData(name, value));
                }
            }
            return this;
        }

        /**
         * Add a form data part to the body.
         */
        public MultipartBuilder addFormDataPart(String name, @Nullable String filename, RequestBody body) {
            multipartBuilder.addPart(MultipartBody.Part.createFormData(name, filename, body));
            return this;
        }

        public MultipartBuilder addFormDataParts(MediaType mediaType, String name, String... filePaths) {
            for (String path : filePaths) {
                File file = new File(path);
                multipartBuilder.addPart(MultipartBody.Part.createFormData(name, file.getName(), RequestBody.create(mediaType, file)));
            }
            return this;
        }

        public MultipartBuilder addRequestProgressListener(ProgressInterceptor.ProgressListener progressListener) {
            OkHttpHelper.okHttp().addInterceptor(new ProgressInterceptor(PROGRESS_REQUEST, progressListener));
            return this;
        }

        @Override
        MultipartBody create() {
            return multipartBuilder.build();
        }
    }
}