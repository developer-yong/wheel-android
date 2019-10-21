package dev.yong.wheel.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author coderyong
 */
public class Gson {

    public static <T> T fromJson(String jsonStr, Type type) {
        return new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken<TreeMap<String, Object>>() {
                        }.getType(), (JsonDeserializer<TreeMap<String, Object>>) (json, typeOfClass, context) -> {
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
