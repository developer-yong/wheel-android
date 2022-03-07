@file:Suppress("unused")

package dev.yong.wheel.utils

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

object JSON {

    /**
     * 将JSON字符串解析为 Map<String, Object> 实例
     *
     * @param jsonStr JSON字符串
     * @return Map<String, Object> 实例
     */
    @JvmStatic
    fun parseMap(jsonStr: String?): Map<String, Any> {
        val typeToken: TypeToken<Map<String, Any>> = object : TypeToken<Map<String, Any>>() {}
        return fromJson(jsonStr, typeToken.type)
    }

    /**
     * 将JSON字符串解析为 T 实例
     *
     * @param jsonStr JSON字符串
     * @param clazz   T.class
     * @param <T>     泛型类
     * @return T 实例
    </T> */
    @JvmStatic
    fun <T> parseObject(jsonStr: String?, clazz: Class<T>): T {
        return GsonBuilder().create().fromJson(jsonStr, clazz)
    }

    /**
     * 将JSON字符串解析为 List<T> 实列
     *
     * @param jsonStr JSON字符串
     * @param clazz   T.class
     * @param <T>     泛型类
     * @return List<T> 实例
     */
    @JvmStatic
    fun <T> parseArray(jsonStr: String?, clazz: Class<T>): List<T>? {
        val typeToken = TypeToken.getArray(clazz)
        return if (TextUtils.isEmpty(jsonStr)) {
            null
        } else listOf(GsonBuilder().create().fromJson(jsonStr, typeToken.type))
    }

    @JvmStatic
    fun <T> fromJson(jsonStr: String?, type: Type): T {
        val typeToken: TypeToken<TreeMap<String, Any>> =
            object : TypeToken<TreeMap<String, Any>>() {}
        return GsonBuilder()
            .registerTypeAdapter(
                typeToken.type,
                JsonDeserializer { json: JsonElement, _: Type, _: JsonDeserializationContext ->
                    val map = TreeMap<String, Any>()
                    val jsonObject = json.asJsonObject
                    val entrySet = jsonObject.entrySet()
                    for ((key, value) in entrySet) {
                        map[key] = value
                    }
                    map
                } as JsonDeserializer<TreeMap<String, Any>>)
            .create().fromJson(jsonStr, type)
    }

    @JvmStatic
    fun toJson(`object`: Any, typeOfClass: Type = `object`.javaClass): String {
        return GsonBuilder().create().toJson(`object`, typeOfClass)
    }
}