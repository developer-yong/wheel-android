package dev.yong.wheel.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @author coderyong
 */
open class ViewBindFragment<V : ViewBinding> : Fragment() {

    protected lateinit var mRoot: V

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRoot = onCreateViewBinding(inflater)
        return mRoot.root
    }

    @Suppress("UNCHECKED_CAST")
    protected fun onCreateViewBinding(inflater: LayoutInflater): V {
        val vClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<V>
        return vClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(this, inflater) as V
    }
}