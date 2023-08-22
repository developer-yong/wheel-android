package dev.yong.wheel.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.UnsupportedOperationException

/**
 * 输入法工具
 *
 * @author CoderYong
 */
object InputMethodUtils {

    @JvmStatic
    fun hide(v: View) {
        val context = v.context
        if (context != null) {
            //隐藏键盘
            val manager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    @JvmStatic
    fun show(v: View) {
        val context = v.context
        if (context != null) {
            //显示键盘
            val manager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}