@file:Suppress("unused")

package dev.yong.wheel.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

/**
 * SnackBar 提示
 *
 * @param text     提示文本
 * @param duration 显示时间
 */
@JvmOverloads
fun View.showSnack(
    text: CharSequence,
    @Duration duration: Int = LENGTH_SHORT
) {
    val bar = Snackbar.make(this, text, duration)
    if (!bar.isShown) {
        bar.show()
    }
}

/**
 * SnackBar 提示
 *
 * @param resId    提示文本资源Id
 * @param duration 显示时间
 */
@JvmOverloads
fun View.showSnack(
    @StringRes resId: Int,
    @Duration duration: Int = LENGTH_SHORT
) {
    val bar = Snackbar.make(this, this.resources.getText(resId), duration)
    if (!bar.isShown) {
        bar.show()
    }
}