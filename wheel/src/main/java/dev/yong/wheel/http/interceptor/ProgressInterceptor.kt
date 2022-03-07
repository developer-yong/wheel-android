package dev.yong.wheel.http.interceptor

import android.os.Handler
import android.os.Looper
import okhttp3.*
import okio.*
import java.io.IOException

/**
 * @author coderyong
 */
open class ProgressInterceptor : Interceptor {

    private val mRequestListeners = mutableMapOf<String, ProgressListener>()
    private val mResponseListeners = mutableMapOf<String, ProgressListener>()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        //获取请求监听
        val requestListener = mRequestListeners[request.url.toString()]
        //获取响应监听
        val responseListener = mResponseListeners[request.url.toString()]
        val response = if (requestListener != null) {
            val newRequest = request.newBuilder()
                .method(request.method, object : RequestBody() {
                    /**
                     * 包装完成的BufferedSink
                     */
                    private var bufferedSink: BufferedSink? = null

                    override fun contentType(): MediaType? {
                        return request.body?.contentType()
                    }

                    @Throws(IOException::class)
                    override fun writeTo(sink: BufferedSink) {
                        if (bufferedSink == null) {
                            //包装
                            bufferedSink = sink(sink).buffer()
                        }
                        //写入
                        request.body?.writeTo(bufferedSink!!)
                        //必须调用flush，否则最后一部分数据可能不会被写入
                        bufferedSink!!.flush()
                    }

                    /**
                     * 写入，回调进度接口
                     *
                     * @param sink Sink
                     * @return Sink
                     */
                    private fun sink(sink: Sink): Sink {
                        return object : ForwardingSink(sink) {
                            //当前写入字节数
                            var bytesWritten = 0L

                            //总字节长度，避免多次调用contentLength()方法
                            var contentLength = 0L

                            @Throws(IOException::class)
                            override fun write(source: Buffer, byteCount: Long) {
                                super.write(source, byteCount)
                                if (contentLength == 0L) {
                                    //获得contentLength的值，后续不再调用
                                    contentLength = contentLength()
                                }
                                //增加当前写入的字节数
                                bytesWritten += byteCount
                                //回调，如果contentLength()不知道长度，会返回-1
                                val isDone = bytesWritten == contentLength
                                Handler(Looper.getMainLooper()).post {
                                    requestListener.onProgress(
                                        bytesWritten,
                                        contentLength,
                                        isDone
                                    )
                                }
                                //请求结束，移除进度监听
                                if (isDone) mRequestListeners.remove(request.url.toString())
                            }
                        }
                    }
                })
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }

        return if (responseListener != null) {
            //包装响应体并返回
            response.newBuilder()
                .body(object : ResponseBody() {
                    /**
                     * 包装完成的BufferedSource
                     */
                    private var bufferedSource: BufferedSource? = null

                    override fun contentType(): MediaType? {
                        return response.body?.contentType()
                    }

                    override fun contentLength(): Long {
                        return response.body?.contentLength() ?: 0
                    }

                    override fun source(): BufferedSource {
                        if (bufferedSource == null) {
                            //包装
                            bufferedSource = responseSource(response.body!!).buffer()
                        }
                        return bufferedSource!!
                    }

                    /**
                     * 读取，回调进度接口
                     *
                     * @param responseBody ResponseBody
                     * @return Source
                     */
                    private fun responseSource(responseBody: ResponseBody): Source {
                        return object : ForwardingSource(responseBody.source()) {
                            //当前读取字节数
                            var totalBytesRead = 0L

                            //总字节长度，避免多次调用contentLength()方法
                            var contentLength = 0L

                            @Throws(IOException::class)
                            override fun read(sink: Buffer, byteCount: Long): Long {
                                val bytesRead = super.read(sink, byteCount)
                                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                                val isDone = bytesRead == -1L

                                if (contentLength == 0L) {
                                    //获得contentLength的值，后续不再调用
                                    contentLength = contentLength()
                                }
                                //回调，如果contentLength()不知道长度，会返回-1
                                if (contentLength == -1L) {
                                    contentLength = totalBytesRead
                                }
                                Handler(Looper.getMainLooper()).post {
                                    responseListener.onProgress(
                                        totalBytesRead,
                                        contentLength,
                                        isDone
                                    )
                                }
                                //响应结束，移除进度监听
                                if (isDone) mResponseListeners.remove(request.url.toString())
                                return bytesRead
                            }
                        }
                    }
                })
                .build()
        } else {
            response
        }
    }

    /**
     * 设置请求进度监听
     *
     * @param url          请求地址
     * @param listener     进度监听
     */
    fun addRequestProgressListener(url: String, listener: ProgressListener) {
        mRequestListeners[url] = listener
    }

    /**
     * 设置响应进度监听
     *
     * @param url          请求地址
     * @param listener     进度监听
     */
    fun addResponseProgressListener(url: String, listener: ProgressListener) {
        mResponseListeners[url] = listener
    }

    interface ProgressListener {

        /**
         * 进度回调
         *
         * @param currentLength 当前长度
         * @param totalLength   总长度
         * @param done          是否完成或者失效
         */
        fun onProgress(currentLength: Long, totalLength: Long, done: Boolean)
    }
}