package dev.yong.wheel.utils

import android.view.View

/**
 * 视图显示
 */
fun <V : View> V.visible(): V {
    this.visibility = View.VISIBLE
    return this
}

/**
 * 视图占位隐藏
 */
fun <V : View> V.invisible(): V {
    this.visibility = View.INVISIBLE
    return this
}

/**
 * 视图无占位隐藏
 */
fun <V : View> V.gone(): V {
    this.visibility = View.GONE
    return this
}

/**
 * 视图是否处于可见状态
 */
fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

/**
 * 视图是否处于占位隐藏状态
 */
fun View.isInvisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

/**
 * 视图是否处于无占位隐藏状态
 */
fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}