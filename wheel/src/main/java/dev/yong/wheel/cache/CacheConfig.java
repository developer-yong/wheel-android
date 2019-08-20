package dev.yong.wheel.cache;

import android.content.Context;

/**
 * @author coderyong
 */
public interface CacheConfig {

    /**
     * 缓存文件夹，默认为{@link Context#getCacheDir()/Cache}
     */
    String CACHE_DIR = "Cache";

    /**
     * 缓存文件最大量，默认50MB
     */
    int MAX_SIZE = 1024 * 1024 * 50;

    /**
     * 缓存数据条数最大量，默认{@link Integer#MAX_VALUE}
     */
    int MAX_COUNT = Integer.MAX_VALUE;
}
