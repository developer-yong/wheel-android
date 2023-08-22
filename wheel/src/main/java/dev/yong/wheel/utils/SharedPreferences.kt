@file:Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER")

package dev.yong.wheel.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import dev.yong.wheel.AppManager

/**
 * 获取SharedPreferences [Context.getSharedPreferences]
 *
 * @param name 所需的首选项文件名
 * @param mode 操作模式 [Context.MODE_PRIVATE,
 *                     Context.MODE_WORLD_READABLE,
 *                     Context.MODE_WORLD_WRITEABLE,
 *                     Context.MODE_ENABLE_WRITE_AHEAD_LOGGING,
 *                     Context.MODE_NO_LOCALIZED_COLLATORS]
 *
 * @return The single {@link SharedPreferences} instance that can be used
 *         to retrieve and modify the preference values.
 */
fun Context.getSharedPreferences(
    name: String = "config",
    mode: Int = Context.MODE_PRIVATE
): SharedPreferences {
    return getSharedPreferences(name, mode)
}

fun Fragment.getSharedPreferences(
    name: String = "config",
    mode: Int = Context.MODE_PRIVATE
): SharedPreferences {
    val context = context ?: AppManager.getInstance().application
    return context.getSharedPreferences(name, mode)
}

fun SharedPreferences.putString(key: String, value: String?): SharedPreferences {
    this.edit().putString(key, value).apply()
    return this
}

fun SharedPreferences.putStringSet(key: String, value: Set<String>?): SharedPreferences {
    this.edit().putStringSet(key, value).apply()
    return this
}

fun SharedPreferences.putInt(key: String, value: Int): SharedPreferences {
    this.edit().putInt(key, value).apply()
    return this
}

fun SharedPreferences.putLong(key: String, value: Long): SharedPreferences {
    this.edit().putLong(key, value).apply()
    return this
}

fun SharedPreferences.putFloat(key: String, value: Float): SharedPreferences {
    this.edit().putFloat(key, value).apply()
    return this
}

fun SharedPreferences.putBoolean(key: String, value: Boolean): SharedPreferences {
    this.edit().putBoolean(key, value).apply()
    return this
}

fun SharedPreferences.remove(key: String): SharedPreferences {
    this.edit().remove(key).apply()
    return this
}

fun SharedPreferences.clear(): SharedPreferences {
    this.edit().clear().apply()
    return this
}
