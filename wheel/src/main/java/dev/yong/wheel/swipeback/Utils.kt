@file:Suppress("unused")

package dev.yong.wheel.swipeback

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.os.Build
import androidx.annotation.RequiresApi

object Utils {

    /**
     * Convert a translucent themed Activity
     * [android.R.attr.windowIsTranslucent] to a fullscreen opaque
     * Activity.
     *
     *
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the [android.view.Surface] of
     * the Activity behind to be released.
     *
     *
     * This call has no effect on non-translucent activities or on activities
     * with the [android.R.attr.windowIsFloating] attribute.
     */
    fun convertActivityFromTranslucent(activity: Activity?) {
        try {
            val method = Activity::class.java.getDeclaredMethod("convertFromTranslucent")
            method.isAccessible = true
            method.invoke(activity)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * Convert a translucent themed Activity
     * [android.R.attr.windowIsTranslucent] back from opaque to
     * translucent following a call to
     * [.convertActivityFromTranslucent] .
     *
     *
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     *
     *
     * This call has no effect on non-translucent activities or on activities
     * with the [android.R.attr.windowIsFloating] attribute.
     */
    fun convertActivityToTranslucent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity)
        } else {
            convertActivityToTranslucentBeforeL(activity)
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    private fun convertActivityToTranslucentBeforeL(activity: Activity?) {
        try {
            val classes = Activity::class.java.declaredClasses
            var translucentConversionListenerClazz: Class<*>? = null
            for (clazz in classes) {
                if (clazz.simpleName.contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz
                }
            }
            val method = Activity::class.java.getDeclaredMethod(
                "convertToTranslucent",
                translucentConversionListenerClazz
            )
            method.isAccessible = true
            method.invoke(
                activity, null
            )
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    @SuppressLint("DiscouragedPrivateApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun convertActivityToTranslucentAfterL(activity: Activity) {
        try {
            val getActivityOptions = Activity::class.java.getDeclaredMethod("getActivityOptions")
            getActivityOptions.isAccessible = true
            val options = getActivityOptions.invoke(activity)
            val classes = Activity::class.java.declaredClasses
            var translucentConversionListenerClazz: Class<*>? = null
            for (clazz in classes) {
                if (clazz.simpleName.contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz
                }
            }
            val convertToTranslucent = Activity::class.java.getDeclaredMethod(
                "convertToTranslucent",
                translucentConversionListenerClazz, ActivityOptions::class.java
            )
            convertToTranslucent.isAccessible = true
            convertToTranslucent.invoke(activity, null, options)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}