@file:Suppress("unused")

package dev.yong.wheel.swipeback

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment

/**
 * 注册滑动返回
 *
 * @param swipeBack 滑动返回实现对象（只能被Activity或者Fragment实现）
 */
fun registerSwipeBack(swipeBack: ISwipeBack) {

    if (swipeBack !is Activity && swipeBack !is Fragment) {
        throw IllegalStateException("ISwipeBack must be implemented by Activity or Fragment")
    }

    val backLayout: SwipeBackLayout
    if (swipeBack is Fragment) {
        val fragment = swipeBack as Fragment
        val rootView = fragment.requireView()
        if (rootView !is SwipeBackLayout) {
            throw IllegalStateException("Fragment rootView must be SwipeBackLayout")
        }
        backLayout = rootView
        backLayout.attachToFragment(fragment)
    } else {
        val activity = swipeBack as Activity
        backLayout = SwipeBackLayout(activity)
        backLayout.attachToActivity(activity)
    }
    backLayout.setPrevView(swipeBack.prevView())
    backLayout.setAlphaColor(swipeBack.alphaColor())
    backLayout.setHasAlpha(swipeBack.hasAlpha())
    backLayout.setHasShadow(swipeBack.hasShadow())
    backLayout.setPrevViewScrollable(swipeBack.prevViewScrollable())
    backLayout.setSwipeListener(swipeBack.swipeListener())
}

/**
 * 滑动返回接口类
 * <P>
 * 该类只能被Activity或者Fragment实现
 * </P>
 * @author coderyong
 */
interface ISwipeBack {

    /**
     * 背景透明度颜色值
     *
     * @return 默认为0x99000000
     */
    fun alphaColor(): Int {
        return -0x67000000
    }

    /**
     * 背景是否有透明度
     *
     * @return 默认为false
     */
    fun hasAlpha(): Boolean {
        return false
    }

    /**
     * 是否带有阴影
     *
     * @return 默认为true
     */
    fun hasShadow(): Boolean {
        return true
    }

    /**
     * 上一个视图是否可滚动
     *
     * @return 默认为true
     */
    fun prevViewScrollable(): Boolean {
        return true
    }

    /**
     * 滑动返回监听
     *
     * @return 滑动返回监听实现类，默认为null
     */
    fun swipeListener(): SwipeBackLayout.SwipeListener? {
        return null
    }

    /**
     * 上一个视图
     *
     *
     * 可以通过上一个Activity[Activity.getWindow]获得
     * 该方法返回null时与[SwipeBackLayout.setPrevViewScrollable] 设为false等同
     *
     *
     * @return View视图
     */
    fun prevView(): View? {
        if (this is Fragment) {
            var preFragment: Fragment? = null
            val manager = parentFragmentManager
            val fragments = manager.fragments
            if (fragments.size > 1) {
                val index = fragments.indexOf(this)
                for (i in index - 1 downTo 0) {
                    val fragment = fragments[i]
                    if (fragment != null && fragment.view != null) {
                        preFragment = fragment
                        break
                    }
                }
            }
            return preFragment?.view
        }
        return null
    }
}