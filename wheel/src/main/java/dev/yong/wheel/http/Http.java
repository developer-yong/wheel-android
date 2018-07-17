package dev.yong.wheel.http;

import android.text.TextUtils;


import org.simple.eventbus.EventBus;

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
    private BasicParameters basicParameters;
    private ResponseVerify verify;

    /**
     * 创建请求构建者
     *
     * @param url 请求地址
     * @return 构建对象
     */
    public HttpBuilder request(String url) {
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
    public HttpBuilder upload(String url, UploadListener listener, UploadFile... files) {
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
     * 设置基础参数处理实现
     *
     * @param basicParameters 基础参数处理实现类
     */
    public void setBasicParameters(BasicParameters basicParameters) {
        this.basicParameters = basicParameters;
    }

    /**
     * 设置请求响应验证
     * <p>
     * 例如token验证处理
     * </P>
     *
     * @param verify 请求响应验证实现类
     */
    public void setResponseVerify(ResponseVerify verify) {
        this.verify = verify;
    }

    /**
     * 普通请求构建者
     */
    public class HttpBuilder {

        boolean isUpload = false;

        protected String url;
        Map<String, String> parameters;
        boolean isCarry = true;
        ResolveFactory factory;

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

        /**
         * 设置解析工厂实现类
         *
         * @param factory 解析工厂对象
         * @return HttpBuilder
         */
        public HttpBuilder setResolveFactory(ResolveFactory factory) {
            this.factory = factory;
            return this;
        }

        public void get() {
            get(null);
        }

        public void get(HttpResponse response) {
            //创建GET请求
            createHttpRequest(isUpload, response).get(response);
        }

        public void post() {
            post(null);
        }

        public void post(HttpResponse response) {
            //创建POST请求
            createHttpRequest(isUpload, response).post(response);
        }

        private HttpRequest createHttpRequest(boolean isUpload, final HttpResponse response) {
            if (isCarry) {
                if (basicParameters == null) {
                    throw new IllegalStateException(
                            "BasicParameters not initialized, Http.setBasicParameters(BasicParameters) not used");
                }
                basicParameters.resetParameters(parameters);
            }
            if (httpFactory == null) {
                httpFactory = new RetrofitFactory();
            }
            response.setFactory(factory != null ? factory : new DefaultResolveFactory());
            if (verify != null) {
                response.setVerify(verify);
            }
            return isUpload ?
                    httpFactory.createUpload(url, parameters, listener, files) :
                    httpFactory.createRequest(url, parameters);
        }
    }
}
