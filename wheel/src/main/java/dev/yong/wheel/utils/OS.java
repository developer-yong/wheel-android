package dev.yong.wheel.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 设备系统工具类
 */
public class OS {

    //MIUI标识
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    //Flyme标识
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";

    private final Properties properties;

    /**
     * 是否是Flyme系统
     */
    public static boolean isFlyme() {
        if (propertiesExist(KEY_FLYME_ICON_FALG, KEY_FLYME_SETUP_FALG, KEY_FLYME_PUBLISH_FALG)) {
            return true;
        }
        if (getInstance().containsKey(KEY_FLYME_ID_FALG_KEY)) {
            String romName = getInstance().getProperty(KEY_FLYME_ID_FALG_KEY);
            if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查应用是否安装过
     *
     * @param c           Context
     * @param packageName 应用包名
     */
    public static boolean checkInstalled(Context c, String packageName) {
        try {
            c.getApplicationContext().getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 是否是EMUI系统
     */
    public static boolean isEMUI() {
        return propertiesExist(KEY_EMUI_VERSION_CODE, KEY_EMUI_API_LEVEL,
                KEY_EMUI_CONFIG_HW_SYS_VERSION);
    }

    /**
     * 是否是MIUI系统
     */
    public static boolean isMIUI() {
        return propertiesExist(KEY_MIUI_VERSION_CODE, KEY_MIUI_VERSION_NAME,
                KEY_MIUI_INTERNAL_STORAGE);
    }

    private static boolean propertiesExist(String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        for (String key : keys) {
            String value = getInstance().getProperty(key);
            if (value != null)
                return true;
        }
        return false;
    }

    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Enumeration<Object> keys() {
        return properties.keys();
    }

    public Set<Object> keySet() {
        return properties.keySet();
    }

    public int size() {
        return properties.size();
    }

    public Collection<Object> values() {
        return properties.values();
    }

    private OS() {
        properties = new Properties();
        // 读取系统配置信息build.prop类
        try {
            properties.load(new FileInputStream(
                    new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException ignored) {
        }
    }

    private static class OSHolder {
        private final static OS INSTANCE = new OS();
    }

    public static OS getInstance() {
        return OSHolder.INSTANCE;
    }
}

