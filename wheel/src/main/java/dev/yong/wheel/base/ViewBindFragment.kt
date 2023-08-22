package dev.yong.wheel.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dev.yong.wheel.view.ProgressDialog
import java.lang.reflect.ParameterizedType

/**
 * @author coderyong
 */
open class ViewBindFragment<V : ViewBinding> : Fragment() {

    protected lateinit var mRoot: V
    private var mLoadingDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRoot = onCreateViewBinding(inflater)
        return mRoot.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {}
    }

    @Suppress("UNCHECKED_CAST")
    protected fun onCreateViewBinding(inflater: LayoutInflater): V {
        val vClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<V>
        return vClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(this, inflater) as V
    }

    open fun showLoading(message: String = "") {
        if (mLoadingDialog == null) {
            context?.let {
                mLoadingDialog = ProgressDialog(it)
            }
        }
        mLoadingDialog?.show(message)
    }

    open fun cancelLoading() {
        mLoadingDialog?.dismiss()
        mLoadingDialog = null
    }
}