package dev.yong.sample.swipeback

import android.os.Bundle
import android.view.View
import dev.yong.sample.R
import dev.yong.wheel.AppManager
import dev.yong.wheel.Router
import dev.yong.wheel.base.ViewBindActivity
import dev.yong.wheel.databinding.LayoutContainerBinding
import dev.yong.wheel.swipeback.ISwipeBack
import dev.yong.wheel.swipeback.registerSwipeBack

open class SwipeBackActivity : ViewBindActivity<LayoutContainerBinding>(), ISwipeBack {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerSwipeBack(this)

        Router.with(this, R.id.layout_container)
            .open(AFragment::class.java, null)
    }

    override fun prevView(): View {
        return AppManager.getInstance().preActivity!!.window.decorView
    }

    override fun onBackPressed() {
        Router.popStackImmediate(this, 0, 0)
    }
}

