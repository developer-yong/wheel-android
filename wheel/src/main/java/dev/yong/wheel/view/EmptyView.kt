package dev.yong.wheel.view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import dev.yong.wheel.R

/**
 * @author coderyong
 */
object EmptyView {

    @JvmStatic
    fun with(target: View): Builder {
        return Builder(target)
    }

    class Builder(private val mTarget: View) {

        fun show(@LayoutRes emptyLayoutRes: Int = R.layout.layout_empty) {
            show(View.inflate(mTarget.context, emptyLayoutRes, null))
        }

        fun show(emptyView: View) {
            if (mTarget.visibility == View.VISIBLE) {
                val parent = mTarget.parent as ViewGroup
                val index = parent.indexOfChild(mTarget)
                emptyView.layoutParams = mTarget.layoutParams
                mTarget.visibility = View.GONE
                emptyView.tag = mTarget
                parent.addView(emptyView, index)
            }
        }

        fun hide() {
            val parent = mTarget.parent as ViewGroup
            val index = parent.indexOfChild(mTarget) - 1
            val emptyView = parent.getChildAt(index)
            if (emptyView != null && emptyView.tag == mTarget) {
                parent.removeViewAt(index)
            }
            mTarget.visibility = View.VISIBLE
        }
    }
}