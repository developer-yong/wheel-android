package dev.yong.wheel.cache

import java.io.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * @author coderyong
 */
internal class CacheManager(
    private val mCacheDir: File,
    private val mMaxSize: Long,
    private val mMaxCount: Int
) {
    private val mCacheSize: AtomicLong = AtomicLong()
    private val mCacheCount: AtomicInteger = AtomicInteger()
    private val mCacheFiles = Collections
        .synchronizedMap(HashMap<String, File>())
    private val mLastModified = Collections
        .synchronizedMap(HashMap<File, Long>())

    /**
     * 计算 cacheSize和cacheCount
     */
    private fun calculateCacheSizeAndCacheCount() {
        Executors.newSingleThreadExecutor().execute {
            var size = 0
            var count = 0
            val cachedFiles = mCacheDir.listFiles()
            if (cachedFiles != null) {
                for (cachedFile in cachedFiles) {
                    size += cachedFile.length().toInt()
                    count += 1
                    mLastModified[cachedFile] = cachedFile.lastModified()
                    mCacheFiles[getFileKey(cachedFile)] = cachedFile
                }
                mCacheSize.set(size.toLong())
                mCacheCount.set(count)
            }
        }
    }

    private operator fun get(key: String): File? {
        if (!isObsolete(key)) {
            val file = getFile(key)
            mLastModified[file!!] =
                if (file.setLastModified(System.currentTimeMillis())) file.lastModified() else System.currentTimeMillis()
            return file
        }
        return null
    }

    private fun newFile(key: String, timeout: Long, unit: TimeUnit): File {
        val file = getFile(key)
        file?.deleteOnExit()
        var fileName = key.hashCode().toString() + ""
        if (timeout > -1) {
            val time = System.currentTimeMillis() + unit.toMillis(timeout)
            fileName = key.hashCode().toString() + "_" + time
        }
        return File(mCacheDir, fileName)
    }

    private fun getFileKey(cachedFile: File): String {
        return cachedFile.name.split("_".toRegex()).toTypedArray()[0]
    }

    private fun getFile(key: String): File? {
        return mCacheFiles[key.hashCode().toString() + ""]
    }

    private fun getFileTimeout(cachedFile: File): Long {
        val name = cachedFile.name
        var timeStr = "-1"
        if (name.contains("_")) {
            timeStr = cachedFile.name.split("_".toRegex()).toTypedArray()[1]
        }
        return try {
            timeStr.toLong()
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * 添加缓存文件
     *
     * @param key     缓存文件key
     * @param value   缓存数据
     * @param timeout 设置超时删除
     * @param unit    时间单位[TimeUnit]
     */
    fun put(key: String, value: Serializable, timeout: Long, unit: TimeUnit) {
        val out: ByteArrayOutputStream
        var oos: ObjectOutputStream? = null
        try {
            out = ByteArrayOutputStream()
            oos = ObjectOutputStream(out)
            oos.writeObject(value)
            val data = out.toByteArray()
            put(key, data, timeout, unit)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 添加缓存文件
     *
     * @param key     缓存文件key
     * @param value   缓存数据
     * @param timeout 设置超时删除
     * @param unit    时间单位[TimeUnit]
     */
    fun put(key: String, value: String, timeout: Long, unit: TimeUnit) {
        put(key, value.toByteArray(), timeout, unit)
    }


    /**
     * 添加缓存文件
     *
     * @param key     缓存文件key
     * @param value   缓存数据
     * @param timeout 设置超时删除
     * @param unit    时间单位[TimeUnit]
     */
    fun put(key: String, value: ByteArray, timeout: Long, unit: TimeUnit) {
        val file = newFile(key, timeout, unit)
        writeDataToFile(file, value)
        var curCacheCount = mCacheCount.get()
        while (curCacheCount + 1 > mMaxCount) {
            val freedSize = removeNext()
            mCacheSize.addAndGet(-freedSize)
            curCacheCount = mCacheCount.addAndGet(-1)
        }
        mCacheCount.addAndGet(1)
        val valueSize = file.length()
        var curCacheSize = mCacheSize.get()
        while (curCacheSize + valueSize > mMaxSize) {
            val freedSize = removeNext()
            curCacheSize = mCacheSize.addAndGet(-freedSize)
        }
        mCacheSize.addAndGet(valueSize)
        mLastModified[file] =
            if (file.setLastModified(System.currentTimeMillis())) file.lastModified() else System.currentTimeMillis()
        mCacheFiles[getFileKey(file)] = file
    }

    /**
     * 将数据写入到文件
     *
     * @param file  目标文件
     * @param value 缓存数据
     */
    private fun writeDataToFile(file: File, value: ByteArray) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            out.write(value)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 获取String数据
     *
     * @param key 缓存文件key
     * @return String 数据
     */
    fun getString(key: String): String? {
        val bytes = getByte(key)
        return if (bytes == null) null else String(bytes)
    }

    /**
     * 获取序列化数据
     *
     * @param key 缓存文件key
     * @return Serializable 数据
     */
    fun getSerializable(key: String): Serializable? {
        val data = getByte(key)
        if (data != null) {
            var `in`: ByteArrayInputStream? = null
            var ois: ObjectInputStream? = null
            try {
                `in` = ByteArrayInputStream(data)
                ois = ObjectInputStream(`in`)
                return ois.readObject() as Serializable
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    `in`?.close()
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    /**
     * 获取 byte 数据
     *
     * @param key 缓存文件key
     * @return byte 数据
     */
    fun getByte(key: String): ByteArray? {
        var accessFile: RandomAccessFile? = null
        return try {
            val file = get(key)
            if (file == null || !file.exists()) {
                return null
            }
            accessFile = RandomAccessFile(file, "r")
            val byteArray = ByteArray(accessFile.length().toInt())
            accessFile.read(byteArray)
            byteArray
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 判断缓存的String数据是否到期
     *
     * @param key 文件保存的Key
     * @return true：到期了 false：还没有到期
     */
    fun isObsolete(key: String): Boolean {
        val file = getFile(key)
        if (file == null || !file.exists()) {
            return true
        }
        val timeout = getFileTimeout(file)
        var isObsolete = timeout != -1L && timeout < System.currentTimeMillis()
        if (isObsolete) {
            if (file.delete()) {
                mCacheFiles.remove(key)
                mLastModified.remove(file)
            } else {
                isObsolete = false
            }
        }
        return isObsolete
    }

    /**
     * 移除指定缓存数据
     *
     * @param key 缓存文件key
     * @return 是否移除成功
     */
    fun remove(key: String): Boolean {
        val file = getFile(key)
        return file == null || !file.exists() || file.delete()
    }

    /**
     * 清除缓存文件夹
     */
    fun clear(): Boolean {
        mLastModified.clear()
        mCacheFiles.clear()
        mCacheSize.set(0)
        return deleteFile(mCacheDir)
    }

    /**
     * 遍历删除文件
     *
     * @param file 文件目录
     */
    private fun deleteFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        return if (file.isFile) {
            file.delete()
        } else {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (!deleteFile(f)) {
                        return false
                    }
                }
            }
            true
        }
    }

    /**
     * 移除旧的文件
     *
     * @return 移除文件的大小
     */
    private fun removeNext(): Long {
        if (mLastModified.isEmpty()) {
            return 0
        }
        var oldestFile: File? = null
        var oldestModified: Long? = null
        val entries: Set<Map.Entry<File, Long>> = mLastModified.entries
        synchronized(mLastModified) {
            for ((key, lastModified) in entries) {
                if (oldestFile == null) {
                    oldestFile = key
                    oldestModified = lastModified
                } else {
                    if (lastModified < oldestModified!!) {
                        oldestFile = key
                        oldestModified = lastModified
                    }
                }
            }
        }
        if (oldestFile != null) {
            if (oldestFile!!.delete()) {
                mLastModified.remove(oldestFile)
                mCacheFiles.remove(getFileKey(oldestFile!!))
            }
        }
        return 0
    }

    init {
        calculateCacheSizeAndCacheCount()
    }
}