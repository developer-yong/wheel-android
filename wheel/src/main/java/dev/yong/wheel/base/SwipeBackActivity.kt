package dev.yong.wheel.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import dev.yong.wheel.swipeback.ISwipeBack
import dev.yong.wheel.swipeback.registerSwipeBack

/**
 * @author coderyong
 */
open class SwipeBackActivity<V : ViewBinding> : ViewBindActivity<V>(), ISwipeBack {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerSwipeBack(this)
    }
}