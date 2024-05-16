package dev.yong.wheel.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @author coderyong
 */
open class ViewBindDialog<LayoutBinding : ViewBinding>(
    context: Context,
    private var mGravity: Int = Gravity.CENTER,
    private var mWindowAnimations: Int = 0,
    private var mDimAmount: Float = -1f,
) : Dialog(context) {

    private var mWidth = WindowManager.LayoutParams.MATCH_PARENT
    private var mHeight = WindowManager.LayoutParams.WRAP_CONTENT

    //间距
    private var mTopMargin = 0
    private var mBottomMargin = 0
    private var mLeftMargin = 0
    private var mRightMargin = 0

    protected var mRoot: LayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutRoot = FrameLayout(context)
        layoutRoot.layoutParams = WindowManager.LayoutParams(mWidth, mHeight)
        mRoot = onCreateViewBinding(LayoutInflater.from(context), layoutRoot)
        layoutRoot.setPadding(mLeftMargin, mTopMargin, mRightMargin, mBottomMargin)
        setContentView(layoutRoot)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun onCreateViewBinding(inflater: LayoutInflater, parent: ViewGroup): LayoutBinding {
        val vClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<LayoutBinding>
        return vClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(this, inflater, parent, true) as LayoutBinding
    }

    override fun onStart() {
        super.onStart()
        window?.run {
            val params = attributes
            params.width = mWidth
            params.height = mHeight
            params.windowAnimations = mWindowAnimations
            if (mDimAmount > 0) {
                params.dimAmount = mDimAmount
            }
            params.gravity = mGravity
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    open fun setWidth(width: Int) {
        mWidth = width
    }

    open fun setHeight(height: Int) {
        mHeight = height
    }

    /**
     * 设置外间距
     * [FrameLayout.setPadding]
     *
     * @param margin the margin in pixels
     */
    open fun setMargin(margin: Int) {
        setMargin(margin, margin, margin, margin)
    }

    /**
     * 设置外间距
     * [FrameLayout.setPadding]
     *
     * @param left   the left margin in pixels
     * @param top    the top margin in pixels
     * @param right  the right margin in pixels
     * @param bottom the bottom margin in pixels
     */
    open fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
        mLeftMargin = left
        mTopMargin = top
        mRightMargin = right
        mBottomMargin = bottom
    }
}