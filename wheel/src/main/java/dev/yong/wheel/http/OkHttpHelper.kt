@file:Suppress("unused", "UNCHECKED_CAST")

package dev.yong.wheel.http

import dev.yong.wheel.http.RequestBuilder.FormBuilder
import dev.yong.wheel.http.RequestBuilder.MultipartBuilder
import dev.yong.wheel.http.interceptor.ProgressInterceptor
import dev.yong.wheel.utils.JSON.fromJson
import okhttp3.OkHttpClient
import java.lang.reflect.Type

/**
 * @author coderyong
 */
class OkHttpHelper private constructor() {

    private var httpClient: OkHttpClient? = null
    private var clientBuilder: OkHttpClient.Builder? = null
    private var parserFactory: ParserFactory? = null

    /**
     * 进度拦截器
     */
    private var progressListener = ProgressInterceptor()

    private object OkHttpHelperHolder {
        val INSTANCE = OkHttpHelper()
    }

    /**
     * 根据Tag取消请求
     *
     * @param tag
     */
    fun cancelTag(tag: Any) {
        for (call in client().dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
        for (call in client().dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }

    companion object {

        @JvmStatic
        fun getInstance(): OkHttpHelper {
            return OkHttpHelperHolder.INSTANCE
        }

        /**
         * 创建OkHttp构建对象
         * <P>网络配置使用</P>
         *
         * @return OkHttpClient.Builder
         */
        @JvmStatic
        fun okHttp(): OkHttpClient.Builder {
            val instance = getInstance()
            if (instance.clientBuilder == null) {
                instance.clientBuilder = OkHttpClient.Builder()
                instance.clientBuilder!!.addInterceptor(instance.progressListener)
            }
            return instance.clientBuilder!!
        }

        /**
         * 设置解析工厂
         *
         * @param parserFactory ParserFactory
         */
        @JvmStatic
        fun setParserFactory(parserFactory: ParserFactory) {
            getInstance().parserFactory = parserFactory
        }

        /**
         * 获取接收工厂
         *
         * @return ParserFactory
         */
        @JvmStatic
        fun parserFactory(): ParserFactory {
            val instance = getInstance()
            if (instance.parserFactory == null) {
                instance.parserFactory = object : ParserFactory {
                    override fun <T> parser(content: String, type: Type): T {
                        return fromJson(content, type)
                    }
                }
            }
            return instance.parserFactory!!
        }

        /**
         * 获取进度拦截器
         */
        @JvmStatic
        fun progressListener(): ProgressInterceptor {
            return getInstance().progressListener
        }

        /**
         * 创建OkHttpClient
         *
         * @return OkHttpClient
         */
        @JvmStatic
        fun client(): OkHttpClient {
            val instance = getInstance()
            if (instance.httpClient == null) {
                instance.httpClient = okHttp().build()
            }
            return instance.httpClient!!
        }

        /**
         * GET请求
         *
         * @param url 请求地址
         * @return RequestBuilder
         */
        @JvmStatic
        fun get(url: String): RequestBuilder {
            return RequestBuilder.get(url)
        }

        /**
         * HEAD请求
         *
         * @param url 请求地址
         * @return RequestBuilder
         */
        @JvmStatic
        fun head(url: String): RequestBuilder {
            return RequestBuilder.head(url)
        }

        /**
         * POST请求
         *
         * @param url 请求地址
         * @return FormBuilder
         */
        @JvmStatic
        fun post(url: String): FormBuilder {
            return RequestBuilder.post(url)
        }

        /**
         * DELETE请求
         *
         * @param url 请求地址
         * @return FormBuilder
         */
        @JvmStatic
        fun delete(url: String): FormBuilder {
            return RequestBuilder.delete(url)
        }

        /**
         * PUT请求
         *
         * @param url 请求地址
         * @return FormBuilder
         */
        @JvmStatic
        fun put(url: String): FormBuilder {
            return RequestBuilder.put(url)
        }

        /**
         * PATCH请求
         *
         * @param url 请求地址
         * @return FormBuilder
         */
        @JvmStatic
        fun patch(url: String): FormBuilder {
            return RequestBuilder.patch(url)
        }

        /**
         * 文件上传
         *
         * @param url 请求地址
         * @return MultipartBuilder
         */
        @JvmStatic
        fun upload(url: String): MultipartBuilder {
            return RequestBuilder.upload(url)
        }

        /**
         * 取消所有请求请求
         */
        @JvmStatic
        fun cancelAll() {
            for (call in client().dispatcher.queuedCalls()) {
                call.cancel()
            }
            for (call in client().dispatcher.runningCalls()) {
                call.cancel()
            }
        }
    }
}