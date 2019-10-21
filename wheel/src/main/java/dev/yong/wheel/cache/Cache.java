package dev.yong.wheel.cache;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonSyntaxException;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dev.yong.wheel.AppManager;
import dev.yong.wheel.utils.Gson;

import static dev.yong.wheel.cache.CacheConfig.CACHE_DIR;
import static dev.yong.wheel.cache.CacheConfig.MAX_COUNT;
import static dev.yong.wheel.cache.CacheConfig.MAX_SIZE;

/**
 * @author coderyong
 */
public class Cache {

    private static Map<String, Cache> sInstanceMap = new HashMap<>();
    private CacheManager mManager;
    private File mCacheFile;

    public static Cache getInstance() {
        return getInstance(MAX_SIZE, MAX_SIZE);
    }

    public static Cache getInstance(long maxSize, int maxCount) {
        return getInstance(AppManager.getInstance().getApplication(), CACHE_DIR, maxSize, maxCount);
    }

    public static Cache getInstance(@NonNull Context context) {
        return getInstance(context, CACHE_DIR);
    }

    public static Cache getInstance(@NonNull Context context, long maxSize, int maxCount) {
        return getInstance(context, CACHE_DIR, maxSize, maxCount);
    }

    public static Cache getInstance(@NonNull Context context, String cacheName) {
        return getInstance(context, cacheName, MAX_SIZE, MAX_SIZE);
    }

    public static Cache getInstance(@NonNull Context context, String cacheName, long maxSize, int maxCount) {
        return getInstance(new File(context.getCacheDir(), cacheName), maxSize, maxCount);
    }

    public static Cache getInstance(@NonNull File cacheDir) {
        return getInstance(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static Cache getInstance(@NonNull File cacheDir, long maxSize, int maxCount) {
        Cache cache = sInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (cache == null) {
            cache = new Cache(cacheDir, maxSize, maxCount);
            sInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), cache);
        }
        return cache;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private Cache(File cacheDir, long maxSize, int maxCount) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        mCacheFile = cacheDir;
        mManager = new CacheManager(cacheDir, maxSize, maxCount);
        EventBus.getDefault().register(this);
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
        mManager.put(key, Gson.toJson(value), timeout, unit);
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
    public <T> List<T> getList(String key, Type type) {
        try {
            return Gson.fromJson(mManager.getString(key), type);
        } catch (JsonSyntaxException e) {
            return null;
        }
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
        return mCacheFile.getAbsolutePath();
    }
}
