package dev.yong.sample.service;


import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.yong.sample.data.BaseEntity;
import dev.yong.sample.data.Weather;
import dev.yong.wheel.AppManager;
import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.Reply;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

/**
 * @author coderyong
 */
public interface ApiCacheService {

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<BaseEntity<List<Weather>>>> getWeatherList(
            Observable<BaseEntity<List<Weather>>> observable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * @author coderyong
     */
    class CacheProviders {

        private static ApiCacheService SERVICE;

        public synchronized static ApiCacheService getApiCache() {
            if (SERVICE == null) {
                File cacheDir = AppManager.getInstance().getApplication().getExternalCacheDir();
                assert cacheDir != null;
                SERVICE = new RxCache.Builder()
                        //缓存文件的配置、数据的解析配置
                        .persistence(cacheDir, new GsonSpeaker())
                        //这些配置对应的缓存接口
                        .using(ApiCacheService.class);
            }
            return SERVICE;
        }
    }
}
