package dev.yong.wheel.http;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import dev.yong.wheel.http.retrofit.RetrofitFactory;

/**
 * Http请求帮助类
 *
 * @author coderyong
 */
public class Http {

    private static final String TAG = Http.class.getSimpleName();

    private Http() {
    }

    private static class HttpHelperHolder {
        private final static Http INSTANCE = new Http();
    }

    public static Http getInstance() {
        return HttpHelperHolder.INSTANCE;
    }


    private HttpFactory httpFactory;
    private HttpInterceptor interceptor;

    /**
     * 创建请求构建者
     *
     * @param url 请求地址
     * @return 构建对象
     */
    public static HttpBuilder request(String url) {
        return new HttpBuilder(url);
    }

    /**
     * 创建上传构建者
     *
     * @param url      上传地址
     * @param listener 进度监听
     * @param files    上传文件数组
     * @return 构建对象
     */
    public static HttpBuilder upload(String url, UploadListener listener, UploadFile... files) {
        return new HttpBuilder(url, listener, files);
    }

    /**
     * 设置网络请求工厂实现
     *
     * @param httpFactory 网络请求实现类
     */
    public void setFactory(HttpFactory httpFactory) {
        this.httpFactory = httpFactory;
    }


    /**
     * 设置请求拦截
     * <p>
     * 设置基础参数或者token验证处理
     * </P>
     *
     * @param interceptor 请求拦截实现类
     */
    public void setHttpInterceptor(HttpInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * 普通请求构建者
     */
    public static class HttpBuilder {

        boolean isUpload = false;

        protected String url;
        Map<String, String> parameters;
        boolean isCarry = true;

        private UploadListener listener;
        private UploadFile[] files;

        /**
         * 普通请求构造器
         *
         * @param url 请求地址
         */
        HttpBuilder(String url) {
            if (TextUtils.isEmpty(url)) {
                throw new NullPointerException(TAG + "Url must be not null");
            }
            this.url = url;
        }

        /**
         * 上传请求构造器
         *
         * @param url 请求地址
         */
        HttpBuilder(String url, UploadListener listener, UploadFile... files) {
            this(url);
            this.listener = listener;
            this.files = files;
            this.isUpload = true;
        }

        /**
         * 添加参数
         *
         * @param key   参数Key
         * @param value 参数值
         * @return HttpBuilder
         */
        public HttpBuilder addParameter(String key, String value) {
            if (parameters == null) {
                parameters = new HashMap<>();
            }
            if (TextUtils.isEmpty(key)) {
                throw new NullPointerException(TAG + "Parameter key must be not null");
            }
            parameters.put(key, value);
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param parameters 参数Map
         * @return HttpBuilder
         */
        public HttpBuilder setParameters(Map<String, String> parameters) {
            if (parameters != null) {
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    addParameter(parameter.getKey(), parameter.getValue());
                }
            }
            return this;
        }

        /**
         * 是否携带基础参数
         *
         * @param isCarry 是否携带
         * @return HttpBuilder
         */
        public HttpBuilder isCarryBasic(boolean isCarry) {
            this.isCarry = isCarry;
            return this;
        }

        public void get() {
            get(null);
        }

        public <T> void get(HttpResponse<T> response) {
            //创建GET请求
            execute(HttpRequest.GET, response);
        }

        public void post() {
            post(null);
        }

        public <T> void post(HttpResponse<T> response) {
            //创建POST请求
            execute(HttpRequest.POST, response);
        }

        public <T> void head(HttpResponse<T> response) {
            //创建POST请求
            execute(HttpRequest.HEAD, response);
        }

        public <T> void put(HttpResponse<T> response) {
            execute(HttpRequest.PUT, response);
        }

        public <T> void delete(HttpResponse<T> response) {
            execute(HttpRequest.DELETE, response);
        }

        public <T> void patch(HttpResponse<T> response) {
            execute(HttpRequest.PATCH, response);
        }

        public <T> void execute(@HttpRequest.Method String method, HttpResponse<T> response) {
            if (isCarry) {
                if (getInstance().interceptor == null) {
                    throw new IllegalStateException(
                            "HttpInterceptor not initialized, Http.setHttpInterceptor(HttpInterceptor) not used");
                }
                getInstance().interceptor.resetParameters(parameters);
            }
            if (getInstance().httpFactory == null) {
                getInstance().httpFactory = new RetrofitFactory();
            }
            HttpRequest request = isUpload ?
                    getInstance().httpFactory.createUpload(url, parameters, getInstance().interceptor, listener, files) :
                    getInstance().httpFactory.createRequest(url, parameters, getInstance().interceptor);
            request.request(method, response);
        }
    }
}
