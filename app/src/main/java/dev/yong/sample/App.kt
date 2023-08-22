@file:Suppress("unused")

package dev.yong.sample

import android.app.Application
import android.content.Context
import androidx.fragment.app.ListFragment
import androidx.multidex.MultiDex
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import dev.yong.sample.mvp.MvpFragment
import dev.yong.sample.viewpager.ViewPagerFragment
import dev.yong.wheel.AppManager.Companion.init
import dev.yong.wheel.Router
import dev.yong.wheel.web.PARAM_WEB_URL
import dev.yong.wheel.web.WebActivity

/**
 * @author coderyong
 */
class App : Application() {
    companion object {
        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, _: RefreshLayout? ->
                ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate)
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, _: RefreshLayout? ->
                ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        init(this)

//        Router.registerFragmentPage("B", BFragment::class.java)
        Router.registerActivityPage("test", TestActivity::class.java)
        Router.registerFragmentPage("list", ListFragment::class.java)
        Router.registerFragmentPage("mvp", MvpFragment::class.java)
        Router.registerFragmentPage("viewpager", ViewPagerFragment::class.java)
        Router.init(this, object : Router.RouterInterceptor {
            override fun intercept(pagePath: String): Boolean {
                Router.with()
                    .putExtra(PARAM_WEB_URL, pagePath)
                    .start(WebActivity::class.java)
                return false
            }
        })
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}