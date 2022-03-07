package dev.yong.wheel.web

import android.graphics.Color
import android.os.Bundle
import android.view.View
import dev.yong.wheel.AppManager
import dev.yong.wheel.R
import dev.yong.wheel.Router
import dev.yong.wheel.base.ViewBindActivity
import dev.yong.wheel.databinding.LayoutContainerBinding
import dev.yong.wheel.swipeback.ISwipeBack
import dev.yong.wheel.utils.StatusBar

/**
 * @author coderyong
 */
class WebActivity : ViewBindActivity<LayoutContainerBinding>(), ISwipeBack {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置状态栏颜色
        var navColor = Color.WHITE
        if (intent.extras != null) {
            navColor = intent.extras!!.getInt(PARAM_TITLE_BAR_COLOR, Color.WHITE)
        }
        StatusBar.setColor(this, navColor)

        //显示WebContainer
        Router.with(this, R.id.layout_container)
            .putExtras(intent.extras)
            .open(WebFragment::class.java, WebFragment::class.java.name)
    }

    override fun prevView(): View {
        return AppManager.instance.preActivity!!.window.decorView
    }
}