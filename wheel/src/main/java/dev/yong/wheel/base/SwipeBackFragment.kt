package dev.yong.wheel.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import dev.yong.wheel.swipeback.ISwipeBack
import dev.yong.wheel.swipeback.SwipeBackLayout
import dev.yong.wheel.swipeback.registerSwipeBack

/**
 * @author coderyong
 */
open class SwipeBackFragment<V : ViewBinding> : ViewBindFragment<V>(), ISwipeBack {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        val swipeBackLayout = SwipeBackLayout(rootView.context)
        swipeBackLayout.addView(rootView)
        return swipeBackLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerSwipeBack(this)
    }
}