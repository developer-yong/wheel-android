@file:Suppress("unused")

package dev.yong.wheel.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 获取状态栏高度
 */
@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun getStatusBarHeight(): Int {
    val resources = Resources.getSystem()
    // 获得状态栏高度
    val resourceId = resources.getIdentifier(
        "status_bar_height", "dimen", "android"
    )
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * @author coderyong
 */
object StatusBar {

    /**
     * 设置状态栏颜色
     * <P>
     *     在setContentView()之后使用生效
     * </P>
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param height   状态栏高度
     */
    @JvmStatic
    fun setColor(activity: Activity, @ColorInt color: Int, height: Int = getStatusBarHeight() + 1) {
        if (color != 0) {
            //创建一个假的状态栏
            val statusView = View(activity)
            statusView.setBackgroundColor(color)
            setView(activity, statusView, height)
        }
    }

    /**
     * 设置状态展示的View
     * <P>
     *     在setContentView()之后使用生效
     * </P>
     *
     * @param activity   需要设置的activity
     * @param statusView 状态栏View
     * @param height     状态栏高度
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun setView(activity: Activity, statusView: View, height: Int = getStatusBarHeight()) {

        var bright = false
        val background = statusView.background
        if (background is ColorDrawable) {
            bright = ColorUtils.calculateLuminance(background.color) >= 0.5
        }
        //设置状态栏透明，如果状态栏背景为亮色，将状态栏文字暗色主题
        immersiveStatusBar(activity, bright)

        val contentLayout = activity.findViewById<ViewGroup>(android.R.id.content)
        // 设置Activity layout的fitsSystemWindows
        val child = contentLayout.getChildAt(0)
        if (child != null) {
            child.fitsSystemWindows = true
        }
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, height
        )
        contentLayout.addView(statusView, params)
    }

    /**
     * 沉浸状态栏
     * <P>
     *     在setContentView()之后使用生效
     * </P>
     *
     * @param activity 需要设置的activity
     * @param toDark   状态栏字体是否变为暗色
     */
    @Suppress("DEPRECATION")
    fun immersiveStatusBar(activity: Activity, toDark: Boolean = false) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            val decorView = window.decorView
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    if (toDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
            decorView.systemUiVisibility = option
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
        if (OS.isMIUI()) {
            setMIUIStatusBarTextMode(activity, toDark)
        } else if (OS.isFlyme()) {
            setFlymeStatusBarTextMode(activity, toDark)
        }
    }


    /**
     * 设置MIUI系统状态栏的文字图标颜色（MIUIV6以上）
     * @param activity
     * @param toDark 状态栏文字及图标设为深色
     */
    @Suppress("DEPRECATION")
    @SuppressLint("PrivateApi")
    private fun setMIUIStatusBarTextMode(activity: Activity, toDark: Boolean) {
        val window: Window? = activity.window
        if (window != null) {
            val clazz: Class<*> = window.javaClass
            try {
                val darkModeFlag: Int
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field: Field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField: Method = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                if (toDark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (toDark) {
                        activity.window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    /**
     * 设置Flyme系统状态栏的文字图标颜色
     * @param activity
     * @param toDark 状态栏文字及图标设为深色
     */
    private fun setFlymeStatusBarTextMode(activity: Activity, toDark: Boolean) {
        val window = activity.window
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                value = if (toDark) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
            } catch (_: Exception) {
            }
        }
    }

    /**
     * 隐藏状态栏
     *
     * @param activity 需要设置的activity
     */
    @Suppress("DEPRECATION")
    @JvmStatic
    fun hide(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}