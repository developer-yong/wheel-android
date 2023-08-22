package dev.yong.wheel.utils

import android.view.View

/**
 * 视图显示
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

/**
 * 视图占位隐藏
 */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * 视图无占位隐藏
 */
fun View.gone() {
    this.visibility = View.GONE
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