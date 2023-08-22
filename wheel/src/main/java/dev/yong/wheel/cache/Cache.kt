@file:Suppress("unused")

package dev.yong.wheel.cache

import android.text.TextUtils
import dev.yong.wheel.AppManager
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author coderyong
 */
class Cache private constructor() {

    private val mManager: CacheManager
    private val mCacheDir: File

    /**
     * 保存 Serializable 数据到缓存中
     *
     * @param key     缓存文件key
     * @param value   数据内容
     * @param timeout 设置超时
     * @param unit    时间单位
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Serializable,
        timeout: Long = -1,
        unit: TimeUnit = TimeUnit.MILLISECONDS
    ) {
        mManager.put(key, value, timeout, unit)
    }

    /**
     * 保存 Serializable 数据到缓存中
     *
     * @param key     缓存文件key
     * @param value   数据内容
     * @param timeout 设置超时
     * @param unit    时间单位
     */
    @JvmOverloads
    fun put(
        key: String,
        value: ByteArray,
        timeout: Long = -1,
        unit: TimeUnit = TimeUnit.MILLISECONDS
    ) {
        mManager.put(key, value, timeout, unit)
    }

    /**
     * 获取序列化数据
     *
     * @param key 缓存文件key
     * @param <T> 数据内容 T extends Serializable
     * @return T
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable?> getSerializable(key: String): T? {
        return try {
            mManager.getSerializable(key) as T
        } catch (e: ClassCastException) {
            null
        }
    }

    /**
     * 获取String数据
     *
     * @param key 缓存文件key
     * @return String
     */
    fun getString(key: String): String? {
        return mManager.getString(key)
    }

    /**
     * 获取byte[]数据
     *
     * @param key 缓存文件key
     * @return byte[]
     */
    fun getByte(key: String): ByteArray? {
        return mManager.getByte(key)
    }

    /**
     * 判断缓存的String数据是否到期
     *
     * @param key 缓存文件key
     * @return true：到期了 false：还没有到期
     */
    fun isObsolete(key: String): Boolean {
        return mManager.isObsolete(key)
    }

    /**
     * 移除指定缓存数据
     *
     * @param key 缓存文件key
     * @return 是否移除成功
     */
    fun remove(vararg key: String): Boolean {
        var success = true
        for (i in key.indices) {
            if (!mManager.remove(key[i])) {
                success = false
                break
            }
        }
        return success
    }

    /**
     * 清除缓存文件夹
     */
    fun clear(): Boolean {
        return mManager.clear()
    }

    /**
     * 获取缓存路径
     *
     * @return CacheDir AbsolutePath
     */
    val cacheDir: String
        get() = mCacheDir.absolutePath

    companion object {
        /**
         * 缓存文件夹，默认为[/Cache]
         */
        var CACHE_DIR = ""

        /**
         * 缓存文件最大量，默认50MB
         */
        var MAX_SIZE = (1024 * 1024 * 50).toLong()

        /**
         * 缓存数据条数最大量，默认[Integer.MAX_VALUE]
         */
        var MAX_COUNT = Int.MAX_VALUE
        private val sInstanceMap: MutableMap<String, Cache> = HashMap()

        @JvmStatic
        fun getInstance(): Cache {
            var cache = sInstanceMap[CACHE_DIR + myPid()]
            if (cache == null) {
                cache = Cache()
                sInstanceMap[CACHE_DIR + myPid()] = cache
            }
            return cache
        }

        private fun myPid(): String {
            return "_" + AppManager.getInstance().application.applicationInfo.packageName
        }

        @JvmStatic
        fun init(cacheDir: String, maxSize: Long = MAX_SIZE, maxCount: Int = MAX_COUNT) {
            CACHE_DIR = cacheDir
            MAX_SIZE = maxSize
            MAX_COUNT = maxCount
            val cache = sInstanceMap[CACHE_DIR + myPid()]
            if (cache == null) {
                sInstanceMap[CACHE_DIR + myPid()] = Cache()
            }
        }
    }

    init {
        if (TextUtils.isEmpty(CACHE_DIR)) {
            CACHE_DIR = AppManager.getInstance().application.cacheDir.absolutePath
        }
        mCacheDir = File(CACHE_DIR)
        if (!mCacheDir.exists() && !mCacheDir.mkdirs()) {
            throw RuntimeException("can't make dirs in " + mCacheDir.absolutePath)
        }
        mManager = CacheManager(mCacheDir, MAX_SIZE, MAX_COUNT)
    }
}