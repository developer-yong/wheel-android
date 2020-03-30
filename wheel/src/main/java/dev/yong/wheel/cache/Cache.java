package dev.yong.wheel.cache;

import android.content.Context;
import android.text.TextUtils;
import dev.yong.wheel.AppManager;
import dev.yong.wheel.utils.JSON;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author coderyong
 */
public class Cache {

    /**
     * 缓存文件夹，默认为{@link Context#getCacheDir()/Cache}
     */
    public static String CACHE_DIR = "";

    /**
     * 缓存文件最大量，默认50MB
     */
    public static long MAX_SIZE = 1024 * 1024 * 50;

    /**
     * 缓存数据条数最大量，默认{@link Integer#MAX_VALUE}
     */
    public static int MAX_COUNT = Integer.MAX_VALUE;

    private static Map<String, Cache> sInstanceMap = new HashMap<>();
    private CacheManager mManager;
    private File mCacheDir;

    public static Cache getInstance() {
        Cache cache = sInstanceMap.get(CACHE_DIR + myPid());
        if (cache == null) {
            sInstanceMap.put(CACHE_DIR + myPid(), new Cache());
        }
        return cache;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private Cache() {
        if (TextUtils.isEmpty(CACHE_DIR)) {
            CACHE_DIR = AppManager.getInstance().getApplication()
                    .getApplicationContext().getCacheDir().getAbsolutePath();
        }
        mCacheDir = new File(CACHE_DIR);
        if (!mCacheDir.exists() && !mCacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + mCacheDir.getAbsolutePath());
        }
        mManager = new CacheManager(mCacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static void init(String cacheDir, long maxSize, int maxCount) {
        CACHE_DIR = cacheDir;
        MAX_SIZE = maxSize;
        MAX_COUNT = maxCount;
    }

    /**
     * 保存 Serializable 数据到缓存中
     *
     * @param key   缓存文件key
     * @param value 数据内容
     */
    public void put(String key, Serializable value) {
        put(key, value, -1, TimeUnit.MILLISECONDS);
    }

    /**
     * 保存 Serializable 数据到缓存中
     *
     * @param key     缓存文件key
     * @param value   数据内容
     * @param timeout 设置超时
     * @param unit    时间单位
     */
    public void put(String key, Serializable value, long timeout, TimeUnit unit) {
        mManager.put(key, value, timeout, unit);
    }

    /**
     * 保存 List 数据到缓存中
     *
     * @param key   缓存文件key
     * @param value 数据内容
     */
    public void put(String key, List<?> value) {
        put(key, value, -1, TimeUnit.MILLISECONDS);
    }

    /**
     * 保存 List 数据到缓存中
     *
     * @param key     缓存文件key
     * @param value   数据内容
     * @param timeout 设置超时
     * @param unit    时间单位
     */
    public void put(String key, List<?> value, long timeout, TimeUnit unit) {
        mManager.put(key, JSON.toJson(value), timeout, unit);
    }

    /**
     * 保存 Serializable 数据到缓存中
     *
     * @param key   缓存文件key
     * @param value 数据内容
     */
    public void put(String key, byte[] value) {
        put(key, value, -1, TimeUnit.MILLISECONDS);
    }

    /**
     * 保存 Serializable 数据到缓存中
     *
     * @param key     缓存文件key
     * @param value   数据内容
     * @param timeout 设置超时
     * @param unit    时间单位
     */
    public void put(String key, byte[] value, long timeout, TimeUnit unit) {
        mManager.put(key, value, timeout, unit);
    }

    /**
     * 获取序列化数据
     *
     * @param key 缓存文件key
     * @param <T> 数据内容 T extends Serializable
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getSerializable(String key) {
        try {
            return (T) mManager.getSerializable(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * 获取String数据
     *
     * @param key 缓存文件key
     * @return String
     */
    public String getString(String key) {
        return mManager.getString(key);
    }

    /**
     * 获取List数据
     *
     * @param key 缓存文件key
     * @return List
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return JSON.parseArray(mManager.getString(key), clazz);
    }

    /**
     * 获取byte[]数据
     *
     * @param key 缓存文件key
     * @return byte[]
     */
    public byte[] getByte(String key) {
        return mManager.getByte(key);
    }

    /**
     * 判断缓存的String数据是否到期
     *
     * @param key 缓存文件key
     * @return true：到期了 false：还没有到期
     */
    public boolean isObsolete(String key) {
        return mManager.isObsolete(key);
    }

    /**
     * 移除指定缓存数据
     *
     * @param key 缓存文件key
     * @return 是否移除成功
     */
    public boolean remove(String key) {
        return mManager.remove(key);
    }

    /**
     * 清除缓存文件夹
     */
    public boolean clear() {
        return mManager.clear();
    }

    /**
     * 获取缓存路径
     *
     * @return CacheDir AbsolutePath
     */
    public String getCacheDir() {
        return mCacheDir.getAbsolutePath();
    }
}
