package dev.yong.wheel.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CoderYong
 */

public class Preferences {

    @SuppressLint("StaticFieldLeak")
    private static Preferences sInstance;

    private SharedPreferences mPreferences;
    private Context mContext;

    private Preferences(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Preferences getInstance(Context context, String name) {
        if (context == null) {
            throw new NullPointerException("Context must be not null");
        }
        if (sInstance == null) {
            synchronized (Preferences.class) {
                if (sInstance == null) {
                    sInstance = new Preferences(context);
                }
            }
        }
        sInstance.mPreferences = sInstance.mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sInstance;
    }

    public static Preferences getInstance(Context context) {
        return getInstance(context, "config");
    }

    public SharedPreferences.Editor getSPEditor() {
        return mPreferences.edit();
    }

    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    public void putString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public long getLong(String key) {
        return mPreferences.getLong(key, 0);
    }

    public void putLong(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public Set<String> getStringSet(String key) {
        return mPreferences.getStringSet(key, new HashSet<>());
    }

    public void putStringSet(String key, Set<String> setValue) {
        removeFromKey(key);
        mPreferences.edit().putStringSet(key, setValue).apply();
    }

    public void removeFromKey(String key) {
        mPreferences.edit().remove(key).apply();
    }

    public SharedPreferences getSharedFile(String key) {
        return mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return Gson.fromJson(getString(key), clazz);
    }

    public void putObject(String key, Object object) {
        putString(key, Gson.toJson(object));
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }
}
