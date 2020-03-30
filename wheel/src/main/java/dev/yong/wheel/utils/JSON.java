package dev.yong.wheel.utils;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JSON {

    /**
     * 将JSON字符串解析为 Map<String, Object> 实例
     *
     * @param jsonStr JSON字符串
     * @return Map<String, Object> 实例
     */
    public static Map<String, Object> parseMap(String jsonStr) {
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {
        };
        return fromJson(jsonStr, typeToken.getType());
    }

    /**
     * 将JSON字符串解析为 T 实例
     *
     * @param jsonStr JSON字符串
     * @param clazz   T.class
     * @param <T>     泛型类
     * @return T 实例
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        return new GsonBuilder().create().fromJson(jsonStr, clazz);
    }

    /**
     * 将JSON字符串解析为 List<T> 实列
     *
     * @param jsonStr JSON字符串
     * @param clazz   T.class
     * @param <T>     泛型类
     * @return List<T> 实例
     */
    public static <T> List<T> parseArray(String jsonStr, Class<T> clazz) {
        TypeToken<?> typeToken = TypeToken.getArray(clazz);
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        return Arrays.asList(new GsonBuilder().create().fromJson(jsonStr, typeToken.getType()));
    }

    public static <T> T fromJson(String jsonStr, Type type) {
        TypeToken<TreeMap<String, Object>> typeToken = new TypeToken<TreeMap<String, Object>>() {
        };
        return new GsonBuilder()
                .registerTypeAdapter(typeToken.getType(), (JsonDeserializer<TreeMap<String, Object>>) (json, typeOfClass, context) -> {
                    TreeMap<String, Object> map = new TreeMap<>();
                    JsonObject jsonObject = json.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                    for (Map.Entry<String, JsonElement> entry : entrySet) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                    return map;
                })
                .create().fromJson(jsonStr, type);
    }

    public static String toJson(Object object) {
        return toJson(object, object.getClass());
    }

    public static String toJson(Object object, Type typeOfClass) {
        return new GsonBuilder().create().toJson(object, typeOfClass);
    }
}
