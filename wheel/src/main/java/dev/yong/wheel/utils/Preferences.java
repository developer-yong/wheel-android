package dev.yong.wheel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import dev.yong.wheel.AppManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author CoderYong
 */
public class Preferences {

    public static Builder with(Context context) {
        return with(context, "config");
    }

    public static Builder with(String name) {
        return with(AppManager.getInstance().getApplication(), name);
    }

    public static Builder with(Context context, String name) {
        return new Builder(context, name);
    }

    public static class Builder {

        private SharedPreferences mPreferences;

        public Builder(Context context, String name) {
            mPreferences = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
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

        public <T> T getObject(String key, Class<T> clazz) {
            return JSON.parseObject(getString(key), clazz);
        }

        public void putObject(String key, Object object) {
            putString(key, JSON.toJson(object));
        }

        public <T> T getList(String key, Class<T> clazz) {
            return JSON.parseObject(getString(key), clazz);
        }

        public void putList(String key, List<?> list) {
            putString(key, JSON.toJson(list));
        }

        public void clear() {
            mPreferences.edit().clear().apply();
        }
    }
}
