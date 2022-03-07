package dev.yong.wheel.view

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes

/**
 * @author CoderYong
 */
class BottomDialog(context: Context, private val mContentView: View) : Dialog(context) {

    private var mWidth = WindowManager.LayoutParams.MATCH_PARENT
    private var mHeight = WindowManager.LayoutParams.WRAP_CONTENT
    private var mWindowAnimations = R.style.Animation_InputMethod
    private var mDimAmount = -1f

    //间距
    private var mTopMargin = 0
    private var mBottomMargin = 0
    private var mLeftMargin = 0
    private var mRightMargin = 0

    constructor(context: Context, @LayoutRes layoutRes: Int) : this(
        context,
        View.inflate(context, layoutRes, null)
    )

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val layoutRoot = FrameLayout(context)
        layoutRoot.layoutParams = WindowManager.LayoutParams(mWidth, mHeight)
        layoutRoot.addView(mContentView)
        layoutRoot.setPadding(mLeftMargin, mTopMargin, mRightMargin, mBottomMargin)
        setContentView(layoutRoot)
    }

    override fun onStart() {
        super.onStart()
        val window = window
        if (window != null) {
            val params = window.attributes
            params.width = mWidth
            params.height = mHeight
            params.windowAnimations = mWindowAnimations
            if (mDimAmount > 0) {
                params.dimAmount = mDimAmount
            }
            params.gravity = Gravity.BOTTOM
            window.setBackgroundDrawableResource(R.color.transparent)
        }
    }

    fun setWidth(width: Int): BottomDialog {
        mWidth = width
        return this
    }

    fun setHeight(height: Int): BottomDialog {
        mHeight = height
        return this
    }

    /**
     * Specify custom animations to use for the window, as per
     * [ WindowManager.LayoutParams.windowAnimations][WindowManager.LayoutParams.windowAnimations].  Providing anything besides
     * 0 here will override the animations the window would
     * normally retrieve from its theme.
     */
    fun setWindowAnimations(@StyleRes resId: Int) {
        mWindowAnimations = resId
    }

    /**
     * Set the amount of dim behind the window when using
     * [WindowManager.LayoutParams.FLAG_DIM_BEHIND].  This overrides
     * the default dim amount of that is selected by the Window based on
     * its theme.
     *
     * @param amount The new dim amount, from 0 for no dim to 1 for full dim.
     */
    fun setDimAmount(amount: Float): BottomDialog {
        mDimAmount = amount
        return this
    }

    /**
     * 设置外间距
     * [FrameLayout.setPadding]
     *
     * @param margin the margin in pixels
     */
    fun setMargin(margin: Int): BottomDialog {
        setMargin(margin, margin, margin, margin)
        return this
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
    fun setMargin(left: Int, top: Int, right: Int, bottom: Int): BottomDialog {
        mLeftMargin = left
        mTopMargin = top
        mRightMargin = right
        mBottomMargin = bottom
        return this
    }
}