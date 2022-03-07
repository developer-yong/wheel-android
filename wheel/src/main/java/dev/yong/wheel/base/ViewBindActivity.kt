package dev.yong.wheel.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import dev.yong.wheel.utils.StatusBar
import dev.yong.wheel.utils.getStatusBarHeight
import java.lang.reflect.ParameterizedType

/**
 * @author coderyong
 */
open class ViewBindActivity<V : ViewBinding> : AppCompatActivity() {

    protected lateinit var mRoot: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRoot = onCreateViewBinding(layoutInflater)
        setContentView(mRoot.root)
//        StatusBar.setColor(
//            this, Color.WHITE,
//            getStatusBarHeight() + getActionBarHeight()
//        )
    }

    @Suppress("UNCHECKED_CAST")
    protected fun onCreateViewBinding(inflater: LayoutInflater): V {
        val vClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<V>
        return vClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(this, inflater) as V
    }

    protected fun hasActionBar(): Boolean {
        return this.supportActionBar != null
    }

    protected fun getActionBarHeight(): Int {
        val attrs = this.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        val height = attrs.getDimension(0, 0f).toInt()
        attrs.recycle()
        return height
    }

}