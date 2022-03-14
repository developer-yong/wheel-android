@file:Suppress("unused")

package dev.yong.wheel.http

import dev.yong.wheel.http.interceptor.ProgressInterceptor
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import org.json.JSONObject
import java.io.File
import java.util.*

const val GET = "GET"
const val HEAD = "HEAD"
const val POST = "POST"
const val DELETE = "DELETE"
const val PUT = "PUT"
const val PATCH = "PATCH"

open class RequestBuilder internal constructor(url: String, private val method: String) :
    Request.Builder() {

    private val urlBuilder: HttpUrl.Builder
    private var responseProgressListener: ProgressInterceptor.ProgressListener? = null
    protected var requestProgressListener: ProgressInterceptor.ProgressListener? = null

    /************************ HttpUrl.Builder  ************************/
    fun addPathSegment(pathSegment: String): RequestBuilder {
        urlBuilder.addPathSegment(pathSegment)
        return this
    }

    /**
     * Adds a set of path segments separated by a slash (either `\` or `/`). If
     * `pathSegments` starts with a slash, the resulting URL will have empty path segment.
     */
    fun addPathSegments(pathSegments: String): RequestBuilder {
        urlBuilder.addPathSegments(pathSegments)
        return this
    }

    fun addEncodedPathSegment(encodedPathSegment: String): RequestBuilder {
        urlBuilder.addEncodedPathSegment(encodedPathSegment)
        return this
    }

    /**
     * Adds a set of encoded path segments separated by a slash (either `\` or `/`). If
     * `encodedPathSegments` starts with a slash, the resulting URL will have empty path
     * segment.
     */
    fun addEncodedPathSegments(encodedPathSegments: String): RequestBuilder {
        urlBuilder.addEncodedPathSegments(encodedPathSegments)
        return this
    }

    fun setPathSegment(index: Int, pathSegment: String): RequestBuilder {
        urlBuilder.setPathSegment(index, pathSegment)
        return this
    }

    fun setEncodedPathSegment(index: Int, encodedPathSegment: String): RequestBuilder {
        urlBuilder.setEncodedPathSegment(index, encodedPathSegment)
        return this
    }

    fun removePathSegment(index: Int): RequestBuilder {
        urlBuilder.removePathSegment(index)
        return this
    }

    fun encodedPath(encodedPath: String): RequestBuilder {
        urlBuilder.encodedPath(encodedPath)
        return this
    }

    fun query(query: String?): RequestBuilder {
        urlBuilder.query(query)
        return this
    }

    fun encodedQuery(encodedQuery: String?): RequestBuilder {
        urlBuilder.encodedQuery(encodedQuery)
        return this
    }

    /**
     * Encodes the query parameter using UTF-8 and adds it to this URL's query string.
     */
    fun addQueryParameter(name: String, value: String?): RequestBuilder {
        urlBuilder.addQueryParameter(name, value)
        return this
    }

    /**
     * Adds the pre-encoded query parameter to this URL's query string.
     */
    fun addEncodedQueryParameter(encodedName: String, encodedValue: String?): RequestBuilder {
        urlBuilder.addEncodedQueryParameter(encodedName, encodedValue)
        return this
    }

    fun setQueryParameter(name: String, value: String?): RequestBuilder {
        urlBuilder.setQueryParameter(name, value)
        return this
    }

    fun setEncodedQueryParameter(encodedName: String, encodedValue: String?): RequestBuilder {
        urlBuilder.setEncodedQueryParameter(encodedName, encodedValue)
        return this
    }

    fun removeAllQueryParameters(name: String): RequestBuilder {
        urlBuilder.removeAllQueryParameters(name)
        return this
    }

    fun removeAllEncodedQueryParameters(encodedName: String): RequestBuilder {
        urlBuilder.removeAllEncodedQueryParameters(encodedName)
        return this
    }

    fun fragment(fragment: String?): RequestBuilder {
        urlBuilder.fragment(fragment)
        return this
    }

    fun encodedFragment(encodedFragment: String?): RequestBuilder {
        urlBuilder.encodedFragment(encodedFragment)
        return this
    }

    /************************ Headers.Builder  ************************/
    override fun header(name: String, value: String): RequestBuilder {
        super.header(name, value)
        return this
    }

    override fun addHeader(name: String, value: String): RequestBuilder {
        super.addHeader(name, value)
        return this
    }

    override fun removeHeader(name: String): RequestBuilder {
        super.removeHeader(name)
        return this
    }

    override fun headers(headers: Headers): RequestBuilder {
        super.headers(headers)
        return this
    }

    override fun cacheControl(cacheControl: CacheControl): RequestBuilder {
        super.cacheControl(cacheControl)
        return this
    }

    /**
     * 添加响应进度拦截器
     */
    fun addResponseProgressListener(progressListener: ProgressInterceptor.ProgressListener): RequestBuilder {
        this.responseProgressListener = progressListener
        return this
    }

    /**
     * 异步请求
     */
    open fun <T> enqueue(callback: Callback<T>) {
        val requestUrl = urlBuilder.build()
        url(requestUrl)
        if (requestProgressListener != null) {
            OkHttpHelper.progressListener()
                .addRequestProgressListener(requestUrl.toString(), requestProgressListener!!)
        }
        if (responseProgressListener != null) {
            OkHttpHelper.progressListener()
                .addResponseProgressListener(requestUrl.toString(), responseProgressListener!!)
        }
        when (method) {
            GET -> {
                OkHttpHelper.client().newCall(get().build())
                    .enqueue(callback)
            }
            HEAD -> {
                OkHttpHelper.client().newCall(head().build())
                    .enqueue(callback)
            }
            POST -> {
                OkHttpHelper.client().newCall(post(create()).build())
                    .enqueue(callback)
            }
            DELETE -> {
                OkHttpHelper.client().newCall(delete(create()).build())
                    .enqueue(callback)
            }
            PUT -> {
                OkHttpHelper.client().newCall(put(create()).build())
                    .enqueue(callback)
            }
            PATCH -> {
                OkHttpHelper.client().newCall(patch(create()).build())
                    .enqueue(callback)
            }
        }
    }

    /**
     * 同步请求
     */
    open fun execute(): Response {
        val requestUrl = urlBuilder.build()
        url(requestUrl)
        if (requestProgressListener != null) {
            OkHttpHelper.progressListener()
                .addRequestProgressListener(requestUrl.toString(), requestProgressListener!!)
        }
        if (responseProgressListener != null) {
            OkHttpHelper.progressListener()
                .addResponseProgressListener(requestUrl.toString(), responseProgressListener!!)
        }
        when (method) {
            GET -> {
                return OkHttpHelper.client().newCall(get().build())
                    .execute()
            }
            HEAD -> {
                return OkHttpHelper.client().newCall(head().build())
                    .execute()
            }
            POST -> {
                return OkHttpHelper.client().newCall(post(create()).build())
                    .execute()
            }
            DELETE -> {
                return OkHttpHelper.client().newCall(delete(create()).build())
                    .execute()
            }
            PUT -> {
                return OkHttpHelper.client().newCall(put(create()).build())
                    .execute()
            }
            PATCH -> {
                return OkHttpHelper.client().newCall(patch(create()).build())
                    .execute()
            }
        }
        return OkHttpHelper.client().newCall(get().build()).execute()
    }

    open fun create(): RequestBody {
        return EMPTY_REQUEST
    }

    open class FormBuilder internal constructor(url: String, method: String) :
        RequestBuilder(url, method) {

        private var mediaType: MediaType? = null
        private val formBuilder: FormBody.Builder = FormBody.Builder()
        private var jsonContent: String? = null

        fun setMediaType(mediaType: MediaType): FormBuilder {
            this.mediaType = mediaType
            return this
        }

        fun add(name: String, vararg values: String): FormBuilder {
            for (value in values) {
                formBuilder.add(name, value)
            }
            return this
        }

        fun add(params: Map<String, String?>): FormBuilder {
            for (name in params.keys) {
                val value = params[name]
                if (value != null) {
                    formBuilder.add(name, value)
                }
            }
            return this
        }

        fun addEncoded(name: String, vararg values: String): FormBuilder {
            for (value in values) {
                formBuilder.addEncoded(name, value)
            }
            return this
        }

        fun addEncoded(params: Map<String, String?>): FormBuilder {
            for (name in params.keys) {
                val value = params[name]
                if (value != null) {
                    formBuilder.addEncoded(name, value)
                }
            }
            return this
        }

        /**
         * 当 MediaType 为 application/json 是使用此方法设置 Json 内容
         *
         * 此方法调用时将替换所有已添加的参数
         *
         * @param jsonContent Json 字符串内容
         * @return SubmitBuilder
         */
        fun setJson(jsonContent: String): FormBuilder {
            this.mediaType = "application/json".toMediaType()
            this.jsonContent = jsonContent
            return this
        }

        /**
         * 添加请求进度拦截器
         */
        fun addRequestProgressListener(progressListener: ProgressInterceptor.ProgressListener): RequestBuilder {
            this.requestProgressListener = progressListener
            return this
        }

        override fun create(): RequestBody {
            return if (mediaType != null && "application/json" == mediaType.toString()) {
                if (jsonContent != null && "" != jsonContent!!.trim { it <= ' ' }) {
                    return jsonContent!!.toRequestBody(mediaType)
                }
                val parameters: MutableMap<String, String?> = HashMap()
                val formBody: FormBody = formBuilder.build()
                for (i in 0 until formBody.size) {
                    parameters[formBody.name(i)] = formBody.value(i)
                }
                JSONObject(parameters as Map<*, *>).toString().toRequestBody(mediaType)
            } else {
                formBuilder.build()
            }
        }
    }

    open class MultipartBuilder internal constructor(url: String, method: String) :
        RequestBuilder(url, method) {

        private val multipartBuilder: MultipartBody.Builder = MultipartBody.Builder()

        /**
         * Set the MIME type. Expected values for `type` are
         * [MultipartBody.MIXED] (the default),
         * [MultipartBody.ALTERNATIVE],
         * [MultipartBody.DIGEST],
         * [MultipartBody.PARALLEL] and
         * [MultipartBody.FORM].
         */
        fun setType(type: MediaType): MultipartBuilder {
            multipartBuilder.setType(type)
            return this
        }

        /**
         * Add a part to the body.
         */
        fun addPart(part: MultipartBody.Part): MultipartBuilder {
            multipartBuilder.addPart(part)
            return this
        }

        /**
         * Add a part to the body.
         */
        fun addPart(body: RequestBody): MultipartBuilder {
            multipartBuilder.addPart(MultipartBody.Part.create(body))
            return this
        }

        /**
         * Add a part to the body.
         */
        fun addPart(headers: Headers?, body: RequestBody): MultipartBuilder {
            multipartBuilder.addPart(MultipartBody.Part.create(headers, body))
            return this
        }

        /**
         * Add a form data part to the body.
         */
        fun addFormDataPart(name: String, vararg values: String): MultipartBuilder {
            for (value in values) {
                multipartBuilder.addPart(MultipartBody.Part.createFormData(name, value))
            }
            return this
        }

        /**
         * Add a form data part to the body.
         */
        fun addFormDataPart(
            name: String,
            filename: String?,
            body: RequestBody
        ): MultipartBuilder {
            multipartBuilder.addPart(MultipartBody.Part.createFormData(name, filename, body))
            return this
        }

        fun addFormDataParts(
            mediaType: MediaType?,
            name: String,
            vararg filePaths: String
        ): MultipartBuilder {
            for (path in filePaths) {
                val file = File(path)
                multipartBuilder.addPart(
                    MultipartBody.Part.createFormData(
                        name,
                        file.name,
                        file.asRequestBody(mediaType)
                    )
                )
            }
            return this
        }


        /**
         * 添加请求进度拦截器
         */
        fun addRequestProgressListener(progressListener: ProgressInterceptor.ProgressListener): RequestBuilder {
            this.requestProgressListener = progressListener
            return this
        }

        override fun create(): MultipartBody {
            return multipartBuilder.build()
        }
    }

    companion object {
        @JvmStatic
        fun get(url: String): RequestBuilder {
            return RequestBuilder(url, GET)
        }

        @JvmStatic
        fun head(url: String): RequestBuilder {
            return RequestBuilder(url, HEAD)
        }

        @JvmStatic
        fun post(url: String): FormBuilder {
            return FormBuilder(url, POST)
        }

        @JvmStatic
        fun delete(url: String): FormBuilder {
            return FormBuilder(url, DELETE)
        }

        @JvmStatic
        fun put(url: String): FormBuilder {
            return FormBuilder(url, PUT)
        }

        @JvmStatic
        fun patch(url: String): FormBuilder {
            return FormBuilder(url, PATCH)
        }

        @JvmStatic
        fun upload(url: String): MultipartBuilder {
            return MultipartBuilder(url, POST)
        }
    }

    init {
        check(url.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex())) { "Invalid url" }
        urlBuilder = url.toHttpUrl().newBuilder()
    }
}