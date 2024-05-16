package dev.yong.wheel.cache;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wuyongzhi
 * @CreateDate: 2022/4/14 19:12
 * @Description:
 */
@SuppressWarnings("ALL")
public class Cache {

    public static String CACHE_NAME = "wheel_cache";
    private static final String KEY_VALIDITY = "_C_V";

    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    /**
     * 内存缓存数据集合
     */
    private final Map<String, Object> mRAMCacheData = new ConcurrentHashMap<>();
    /**
     * 有效期管理集合
     */
    private final Map<String, String> mValidityMap = new ConcurrentHashMap<>();
    /**
     * 文件缓存
     */
    private FileCache mFileCache;

    public SharedPreferences getPreferences() {
        if (mPreferences == null) {
            try {
                mPreferences = getContext().getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
            } catch (Throwable e) {
                throw new IllegalArgumentException("Cache not initialized, use Cache.init(Context) to initialize.");
            }
        }
        return mPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        if (mEditor == null) {
            mEditor = getPreferences().edit();
        }
        return mEditor;
    }

    public Cache putString(String key, @Nullable String value) {
        return putString(key, value, true);
    }

    public Cache putString(String key, @Nullable String value, boolean compare) {
        return putString(key, value, 0, compare);
    }

    public Cache putString(String key, @Nullable String value, long validity) {
        return putString(key, value, validity, true);
    }

    public Cache putString(String key, @Nullable String value, long validity, boolean compare) {
        if (value == null) {
            value = "";
        }
        if (!compare || needReCache(key, value)) {
            SharedPreferences.Editor editor = getEditor().putString(key, value);
            if (validity > 0) {
                String vValue = System.currentTimeMillis() + ";" + validity;
                editor.putString(key + KEY_VALIDITY, vValue);
            }
            editor.apply();
            mRAMCacheData.put(key, value);
        }
        return this;
    }

    public Cache putInt(String key, int value) {
        return putInt(key, value, true);
    }

    public Cache putInt(String key, int value, boolean compare) {
        return putInt(key, value, 0, compare);
    }

    public Cache putInt(String key, int value, long validity) {
        return putInt(key, value, validity, true);
    }

    public Cache putInt(String key, int value, long validity, boolean compare) {
        if (!compare || needReCache(key, value)) {
            SharedPreferences.Editor editor = getEditor().putInt(key, value);
            if (validity > 0) {
                String vValue = System.currentTimeMillis() + ";" + validity;
                editor.putString(key + KEY_VALIDITY, vValue);
            }
            editor.apply();
            mRAMCacheData.put(key, value);
        }
        return this;
    }

    public Cache putLong(String key, long value) {
        return putLong(key, value, true);
    }

    public Cache putLong(String key, long value, boolean compare) {
        return putLong(key, value, 0, compare);
    }

    public Cache putLong(String key, long value, long validity) {
        return putLong(key, value, validity, true);
    }

    public Cache putLong(String key, long value, long validity, boolean compare) {
        if (!compare || needReCache(key, value)) {
            SharedPreferences.Editor editor = getEditor().putLong(key, value);
            if (validity > 0) {
                String vValue = System.currentTimeMillis() + ";" + validity;
                editor.putString(key + KEY_VALIDITY, vValue);
            }
            editor.apply();
            mRAMCacheData.put(key, value);
        }
        return this;
    }

    public Cache putFloat(String key, float value) {
        return putFloat(key, value, true);
    }

    public Cache putFloat(String key, float value, boolean compare) {
        return putFloat(key, value, 0, compare);
    }

    public Cache putFloat(String key, float value, long validity) {
        return putFloat(key, value, validity, true);
    }

    public Cache putFloat(String key, float value, long validity, boolean compare) {
        if (!compare || needReCache(key, value)) {
            SharedPreferences.Editor editor = getEditor().putFloat(key, value);
            if (validity > 0) {
                String vValue = System.currentTimeMillis() + ";" + validity;
                editor.putString(key + KEY_VALIDITY, vValue);
            }
            editor.apply();
            mRAMCacheData.put(key, value);
        }
        return this;
    }

    public Cache putBoolean(String key, boolean value) {
        return putBoolean(key, value, true);
    }

    public Cache putBoolean(String key, boolean value, boolean compare) {
        return putBoolean(key, value, 0, compare);
    }

    public Cache putBoolean(String key, boolean value, long validity) {
        return putBoolean(key, value, validity, true);
    }

    public Cache putBoolean(String key, boolean value, long validity, boolean compare) {
        if (!compare || needReCache(key, value)) {
            SharedPreferences.Editor editor = getEditor().putBoolean(key, value);
            if (validity > 0) {
                String vValue = System.currentTimeMillis() + ";" + validity;
                editor.putString(key + KEY_VALIDITY, vValue);
            }
            editor.apply();
            mRAMCacheData.put(key, value);
        }
        return this;
    }

    public SharedPreferences.Editor remove(String key) {
        mRAMCacheData.remove(key);
        return getEditor().remove(key).remove(key + KEY_VALIDITY);
    }

    public SharedPreferences.Editor clear() {
        mRAMCacheData.clear();
        return getEditor().clear();
    }

    public static Map<String, ?> getAll() {
        return getInstance().getPreferences().getAll();
    }

    @Nullable
    public static String getString(String key) {
        return getString(key, "");
    }

    @Nullable
    public static String getString(String key, @Nullable String defValue) {
        if (checkValidityAndRemove(key)) {
            try {
                String value = (String) getInstance().mRAMCacheData.get(key);
                if (!TextUtils.isEmpty(value)) {
                    return value;
                }
            } catch (Throwable ignored) {
            }
            return getInstance().getPreferences().getString(key, defValue);
        }
        return defValue;
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defValue) {
        if (checkValidityAndRemove(key)) {
            try {
                return (int) getInstance().mRAMCacheData.get(key);
            } catch (Throwable ignored) {
            }
            return getInstance().getPreferences().getInt(key, defValue);
        }
        return defValue;
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defValue) {
        if (checkValidityAndRemove(key)) {
            try {
                return (long) getInstance().mRAMCacheData.get(key);
            } catch (Throwable ignored) {
            }
            return getInstance().getPreferences().getLong(key, defValue);
        }
        return defValue;
    }

    public static float getFloat(String key) {
        return getFloat(key, 0F);
    }

    public static float getFloat(String key, float defValue) {
        if (checkValidityAndRemove(key)) {
            try {
                return (float) getInstance().mRAMCacheData.get(key);
            } catch (Throwable ignored) {
            }
            return getInstance().getPreferences().getFloat(key, defValue);
        }
        return defValue;
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        if (checkValidityAndRemove(key)) {
            try {
                return (boolean) getInstance().mRAMCacheData.get(key);
            } catch (Throwable ignored) {
            }
            return getInstance().getPreferences().getBoolean(key, defValue);
        }
        return defValue;
    }

    public static boolean contains(String key) {
        if (checkValidityAndRemove(key)) {
            return getInstance().getPreferences().contains(key);
        }
        return false;
    }

    public static boolean checkValidityAndRemove(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        try {
            SharedPreferences preferences = getInstance().getPreferences();
            String validityInfo = preferences.getString(key + KEY_VALIDITY, "");
            if (!TextUtils.isEmpty(validityInfo)) {
                String[] info = validityInfo.split(";");
                long cacheTime = Long.parseLong(info[0]);
                long validity = Long.parseLong(info[1]);
                long currentTime = System.currentTimeMillis();
                //如果当前时间小于缓存时间或者当前时间大于缓存有效期视为缓存过期
                if (currentTime < cacheTime || currentTime - cacheTime > validity) {
                    getInstance().remove(key).apply();
                    return false;
                }
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    private boolean needReCache(String key, Object value) {
        try {
            Object v = mRAMCacheData.get(key);
            boolean equals = value.equals(v);
            if (!equals && value instanceof String) {
                equals = ((String) value).equals(v);
            }
            if (equals) {
                return false;
            } else {
                mRAMCacheData.put(key, value);
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    /**
     * 缓存Object到Cache内存
     *
     * @param key   缓存Key
     * @param value 缓存内容
     */
    public void putSerializable(String key, Serializable value) {
        putSerializable(key, value, 0);
    }

    /**
     * 缓存Object到Cache内存
     *
     * @param key      缓存Key
     * @param value    缓存内容
     * @param validity 有效期
     */
    public void putSerializable(String key, Serializable value, long validity) {
        if (validity > 0) {
            mValidityMap.put(key + KEY_VALIDITY, System.currentTimeMillis() + ";" + validity);
        }
        mRAMCacheData.put(key, value);
        if (needReCache(key, value)) {
            mFileCache.put(key, value, validity, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 从缓存中获取
     *
     * @param key 缓存Key
     * @return 缓存对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getSerializable(String key) {
        try {
            String validityInfo = mValidityMap.get(key + KEY_VALIDITY);
            if (TextUtils.isEmpty(validityInfo)) {
                Object value = mRAMCacheData.get(key);
                if (value == null) {
                    value = mFileCache.getSerializable(key);
                    if (value != null) {
                        mRAMCacheData.put(key, value);
                    }
                }
                return (T) value;
            } else {
                String[] info = validityInfo.split(";");
                long cacheTime = Long.parseLong(info[0]);
                long validity = Long.parseLong(info[1]);
                long currentTime = System.currentTimeMillis();
                if (currentTime < cacheTime || currentTime - cacheTime < validity) {
                    Object value = mRAMCacheData.get(key);
                    if (value == null) {
                        value = mFileCache.getSerializable(key);
                        if (value != null) {
                            mRAMCacheData.put(key, value);
                        }
                    }
                    return (T) value;
                } else {
                    mValidityMap.remove(key + KEY_VALIDITY);
                    mRAMCacheData.remove(key);
                    mFileCache.remove(key);
                    return null;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * 缓存Object到Cache内存
     *
     * @param key   缓存Key
     * @param value 缓存内容
     */
    public void putObject(String key, Object value) {
        putObject(key, value, 0);
    }

    /**
     * 缓存Object到Cache内存
     *
     * @param key      缓存Key
     * @param value    缓存内容
     * @param validity 有效期
     */
    public void putObject(String key, Object value, long validity) {
        if (validity > 0) {
            mValidityMap.put(key + KEY_VALIDITY, System.currentTimeMillis() + ";" + validity);
        }
        mRAMCacheData.put(key, value);
    }

    /**
     * 从缓存中获取
     *
     * @param key 缓存Key
     * @return 缓存对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key) {
        try {
            String validityInfo = mValidityMap.get(key + KEY_VALIDITY);
            if (TextUtils.isEmpty(validityInfo)) {
                return (T) mRAMCacheData.get(key);
            } else {
                String[] info = validityInfo.split(";");
                long cacheTime = Long.parseLong(info[0]);
                long validity = Long.parseLong(info[1]);
                long currentTime = System.currentTimeMillis();
                if (currentTime < cacheTime || currentTime - cacheTime < validity) {
                    return (T) mRAMCacheData.get(key);
                } else {
                    mValidityMap.remove(key + KEY_VALIDITY);
                    mRAMCacheData.remove(key);
                    return null;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static void init(@NonNull Context context) {
        init(context, CACHE_NAME);
    }

    public static void init(@NonNull Context context, String cacheName) {
        Context appContext = context.getApplicationContext();
        CacheHolder.INSTANCE.mContext = appContext;
        CacheHolder.INSTANCE.mPreferences = appContext.getSharedPreferences(cacheName, Context.MODE_PRIVATE);

        Map<String, ?> all = CacheHolder.INSTANCE.getAll();
        if (all != null) {
            CacheHolder.INSTANCE.mRAMCacheData.putAll(all);
        }
        CacheHolder.INSTANCE.mFileCache = new FileCache(
                context.getCacheDir(), 1024 * 1024 * 50, Integer.MAX_VALUE);
    }

    private Cache() {
    }

    public static Cache getInstance() {
        return CacheHolder.INSTANCE;
    }

    private static class CacheHolder {
        @SuppressLint("StaticFieldLeak")
        private static final Cache INSTANCE = new Cache();
    }

    @SuppressLint("PrivateApi")
    private static Application getContext() throws Exception {
        Application context = (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
        if (context == null) {
            context = (Application) Class.forName("android.app.AppGlobals")
                    .getMethod("getInitialApplication").invoke(null, (Object[]) null);
        }
        return context;
    }
}
