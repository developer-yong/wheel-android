@file:Suppress("unused")

package dev.yong.wheel.utils

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

/**
 * SnackBar 提示
 *
 * @param v        依附View
 * @param text     提示文本
 * @param duration 显示时间
 */
@JvmOverloads
fun Context.showSnack(
    v: View,
    text: CharSequence,
    @Duration duration: Int = LENGTH_SHORT
) {
    val bar = Snackbar.make(v, text, duration)
    if (!bar.isShown) {
        bar.show()
    }
}

/**
 * SnackBar 提示
 *
 * @param v        依附View
 * @param resId    提示文本资源Id
 * @param duration 显示时间
 */
@JvmOverloads
fun Context.showSnack(
    v: View,
    @StringRes resId: Int,
    @Duration duration: Int = LENGTH_SHORT
) {
    showSnack(v, resources.getText(resId), duration)
}

/**
 * SnackBar 提示
 *
 * @param v        依附View
 * @param text     提示文本
 * @param duration 显示时间
 */
@JvmOverloads
fun Fragment.showSnack(
    v: View,
    text: CharSequence,
    @Duration duration: Int = LENGTH_SHORT
) {
    requireContext().showSnack(v, text, duration)
}

/**
 * SnackBar 提示
 *
 * @param v        依附View
 * @param resId    提示文本资源Id
 * @param duration 显示时间
 */
@JvmOverloads
fun Fragment.showSnack(
    v: View,
    @StringRes resId: Int,
    @Duration duration: Int = LENGTH_SHORT
) {
    requireContext().showSnack(v, resId, duration)
}