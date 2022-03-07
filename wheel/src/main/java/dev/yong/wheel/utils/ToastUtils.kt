package dev.yong.wheel.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Toast显示位置
 * @see android.widget.Toast.setGravity
 */
var GRAVITY = Gravity.BOTTOM
var X_OFFSET = 0
var Y_OFFSET = 0

/**
 * Toast间距
 * @see android.widget.Toast.setMargin
 */
var HORIZONTAL_MARGIN = 0F
var VERTICAL_MARGIN = 0F

/**
 * Toast 提示
 *
 * @param message  提示内容
 * @param duration 提示时长
 */
fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(this, message, duration)
    toast.setGravity(GRAVITY, X_OFFSET, Y_OFFSET)
    toast.setMargin(HORIZONTAL_MARGIN, VERTICAL_MARGIN)
    toast.show()
}

/**
 * Toast 提示
 *
 * @param resId    提示字符串资源ID
 * @param duration 提示时长
 */
fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(resId), duration)
}

/**
 * Toast 提示
 *
 * @param view     自定义提示View
 * @param duration 提示时长
 */
@Suppress("DEPRECATION")
fun Context.toast(view: View, duration: Int = Toast.LENGTH_SHORT) {
    val toast = Toast(this)
    toast.view = view
    toast.duration = duration
    toast.setGravity(GRAVITY, X_OFFSET, Y_OFFSET)
    toast.setMargin(HORIZONTAL_MARGIN, VERTICAL_MARGIN)
    toast.show()
}


/**
 * Toast 提示
 *
 * @param message  提示内容
 * @param duration 提示时长
 */
fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(message, duration)
}

/**
 * Toast 提示
 *
 * @param resId    提示字符串资源ID
 * @param duration 提示时长
 */
fun Fragment.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(resId, duration)
}

/**
 * Toast 提示
 *
 * @param view     自定义提示View
 * @param duration 提示时长
 */
fun Fragment.toast(view: View, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(view, duration)
}