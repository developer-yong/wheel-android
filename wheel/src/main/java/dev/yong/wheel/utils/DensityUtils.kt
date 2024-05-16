@file:Suppress("unused")

package dev.yong.wheel.utils

import android.content.res.Resources
import android.util.TypedValue

fun dp2px(dpValue: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()
}

fun px2dip(pxValue: Int): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (pxValue / density + 0.5f).toInt()
}

fun dp2px(dpValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue, Resources.getSystem().displayMetrics
    )
}

fun px2dip(pxValue: Float): Float {
    val density = Resources.getSystem().displayMetrics.density
    return (pxValue / density + 0.5f)
}

/**
 * 获取屏幕宽度
 */
val screenWidth: Int
    get() {
        var widthPixels = -1
        if (widthPixels <= 0) {
            widthPixels = Resources.getSystem().displayMetrics.widthPixels
        }
        return widthPixels
    }

/**
 * 获取屏幕高度
 */
val screenHeight: Int
    get() {
        var heightPixels = -1
        if (heightPixels <= 0) {
            heightPixels = Resources.getSystem().displayMetrics.heightPixels
        }
        return heightPixels
    }