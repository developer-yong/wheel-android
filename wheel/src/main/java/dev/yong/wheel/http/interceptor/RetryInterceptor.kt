package dev.yong.wheel.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author coderyong
 */
class RetryInterceptor(private var mCount: Int) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e: IOException) {
            if (mCount > 0) {
                mCount--
                return intercept(chain)
            }
            throw e
        }
    }
}