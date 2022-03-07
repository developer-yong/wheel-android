package dev.yong.wheel.swipeback

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import dev.yong.wheel.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @author coderyong
 */
class SwipeBackLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 视图拖动帮助类
     */
    private val mViewDragHelper: ViewDragHelper

    /**
     * 当前滑动的宽度占屏幕宽度的比例 [0~1]
     */
    private var mScrollPercent = 0f

    /**
     * 记录透明度占屏幕宽度的比例 [0~1]
     */
    private var mAlphaPercent = 0f

    /**
     * 拖动时透明区域的颜色
     */
    private var mAlphaColor = -0x67000000

    /**
     * 是否有透明度（默认为false）
     */
    private var mHasAlpha = false

    /**
     * 上一个视图是否可以滚动（默认为true）
     */
    private var mPrevViewScrollable = true

    /**
     * 阴影图
     */
    private val mShadowDrawable: Drawable?

    /**
     * 是否有阴影（默认为true）
     */
    private var mHasShadow = true
    private var mSwipeListener: SwipeListener? = null

    /**
     * 上一个视图
     */
    private var mPrevView: View? = null

    /**
     * 当前Fragment
     */
    private var mFragment: Fragment? = null

    /**
     * 设置当前Fragment视图与上一个Fragment视图是否在关闭前
     */
    var isFragmentBeforeClosing = false
        set(fragmentBeforeClosing) {
            field = fragmentBeforeClosing
            if (mPrevView != null && mPrevView is SwipeBackLayout) {
                //设置上一个Fragment进入前与当前Fragment关闭前状态同步
                (mPrevView as SwipeBackLayout).isFragmentBeforeClosing = fragmentBeforeClosing
            }
        }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return try {
            mViewDragHelper.shouldInterceptTouchEvent(event)
        } catch (e: ArrayIndexOutOfBoundsException) {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mViewDragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        mAlphaPercent = 1 - mScrollPercent
        if (mViewDragHelper.continueSettling(true)) {
            //使当前视图下一个动画时间步骤失效
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val ret = super.drawChild(canvas, child, drawingTime)
        if (mAlphaPercent > 0) {
            if (mHasShadow) {
                drawShadow(canvas, child)
            }
            if (mHasAlpha) {
                drawAlpha(canvas, child)
            }
        }
        return ret
    }

    /**
     * 绘制阴影
     *
     * @param canvas 画板
     * @param view   视图
     */
    private fun drawShadow(canvas: Canvas, view: View) {
        val rect = Rect()
        view.getHitRect(rect)
        mShadowDrawable!!.setBounds(
            rect.left - mShadowDrawable.intrinsicWidth,
            rect.top,
            rect.left,
            rect.bottom
        )
        mShadowDrawable.alpha = (mAlphaPercent * 255).toInt()
        mShadowDrawable.draw(canvas)
    }

    /**
     * 绘制透明区域,拖动的越远越透明
     *
     * @param canvas 画板
     * @param view   视图
     */
    private fun drawAlpha(canvas: Canvas, view: View) {
        val baseAlpha = mAlphaColor and -0x1000000 ushr 24
        val alpha = (baseAlpha * mAlphaPercent).toInt()
        val color = alpha shl 24 or (mAlphaColor and 0xffffff)
        canvas.clipRect(0, 0, view.left, height)
        canvas.drawColor(color)
    }

    /**
     * 设置拖动时是否有阴影
     *
     * @param hasShadow 是否有阴影
     */
    fun setHasShadow(hasShadow: Boolean) {
        mHasShadow = hasShadow
    }

    /**
     * 设置默认透明度
     *
     * @param alphaColor 透明度16进制颜色，默认为0x99000000
     */
    fun setAlphaColor(alphaColor: Int) {
        mAlphaColor = alphaColor
    }

    /**
     * 设置拖动时是否有透明度
     *
     * @param hasAlpha 是否有透明度
     */
    fun setHasAlpha(hasAlpha: Boolean) {
        mHasAlpha = hasAlpha
    }

    /**
     * 设置上一个视图
     *
     * @param prevView 上一个视图View
     */
    fun setPrevView(prevView: View?) {
        mPrevView = prevView
    }

    /**
     * 设置上一个视图是否可以滚动
     *
     * @param scrollable 是否有阴影
     */
    fun setPrevViewScrollable(scrollable: Boolean) {
        mPrevViewScrollable = scrollable
    }

    fun setSwipeListener(listener: SwipeListener?) {
        mSwipeListener = listener
    }

    /**
     * 移动背景
     */
    private fun moveBackgroundLayout(background: View?) {
        //mScrollPercent 变化时: 0 -> 0.95 -> 1
        //背景translationX的变化: -0.4 -> 0 -> 0
        if (background != null) {
            var translationX = (0.4 / 0.95 * (mScrollPercent - 0.95) * background.width).toFloat()
            if (translationX > 0) {
                translationX = 0f
            }
            background.translationX = translationX
        }
    }

    fun attachToActivity(activity: Activity) {
        activity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        activity.window.decorView.setBackgroundColor(Color.TRANSPARENT)
        val a = activity.theme
            .obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val background = a.getResourceId(0, 0)
        a.recycle()
        val decor = activity.window.decorView as ViewGroup
        val decorChild = decor.getChildAt(0) as ViewGroup
        decorChild.setBackgroundResource(background)
        decor.removeView(decorChild)
        addView(decorChild)
        decor.addView(this)
    }

    fun attachToFragment(fragment: Fragment) {
        mFragment = fragment
    }

    private fun canSwipeActivity(): Boolean {
        val activity = context as Activity
        var fragments: List<*>? = null
        if (activity is AppCompatActivity) {
            fragments = activity.supportFragmentManager.fragments
        }
        var count = 0
        if (fragments != null) {
            for (o in fragments) {
                if (o is ISwipeBack) {
                    count++
                }
            }
            if (count == 1 && fragments.size > count) {
                count++
            }
        }
        return count <= 1
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        /**
         * 当用户触发拖动时调用
         *
         * @param child     拖动的视图
         * @param pointerId [ViewDragHelper.Callback.tryCaptureView]
         * @return 返回拖动是否可用
         */
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            val dragEnable = mViewDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointerId)
            if (dragEnable) {
                if (mSwipeListener != null) {
                    mSwipeListener!!.onStart(child)
                }
                if (mFragment != null) {
                    if (mPrevView != null) {
                        mPrevView!!.visibility = VISIBLE
                    }
                } else {
                    Utils.convertActivityToTranslucent(child.context as Activity)
                }
            }
            return dragEnable
        }

        /**
         * 获取视图水平拖动范围
         *
         * @param child 拖动的视图
         * @return 返回0时 不可拖动
         */
        override fun getViewHorizontalDragRange(child: View): Int {
            return if (mFragment != null) {
                1
            } else {
                if (canSwipeActivity()) 1 else 0
            }
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return min(child.width, max(left + dx / 2, 0))
        }

        /**
         * 当用户拖动时调用
         *
         * @param changedView 拖动的视图
         * @param left        视图左边位置
         * @param top         视图顶部位置
         * @param dx          上一次X坐标
         * @param dy          上一次Y坐标
         */
        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            //滑动宽度占屏幕宽度的比例
            mScrollPercent = abs(left.toFloat() / changedView.width)
            if (mPrevViewScrollable && mPrevView != null) {
                moveBackgroundLayout(mPrevView)
            }
            if (mSwipeListener != null) {
                mSwipeListener!!.onScrollChanged(mScrollPercent)
            }
            invalidate()
        }

        /**
         * 当用户拖动释放时调用
         */
        override fun onViewReleased(releasedChild: View, x: Float, y: Float) {
            //如果释放时滑动宽度占屏幕宽度的比例大于50%视图移出屏幕，否则视图还原到初始
            if (mScrollPercent > 0.5f) {
                mViewDragHelper.settleCapturedViewAt(releasedChild.width + 1, 0)
            } else {
                mViewDragHelper.settleCapturedViewAt(0, 0)
            }
            invalidate()
        }

        /**
         * 拖动状态改变
         *
         * @param state 拖动状态
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            if (state == ViewDragHelper.STATE_IDLE) {
                if (mScrollPercent >= 1) {
                    if (mFragment != null && !mFragment!!.isDetached) {
                        //Fragment进入关闭
                        isFragmentBeforeClosing = true
                        val manager = mFragment!!.parentFragmentManager
                        //移除当前Fragment
                        manager.beginTransaction().remove(mFragment!!).commit()
                        manager.popBackStackImmediate()
                        //Fragment已经关闭
                        isFragmentBeforeClosing = false
                    } else {
                        //将视图销毁
                        (context as Activity).finish()
                        //无动画退出
                        (context as Activity).overridePendingTransition(0, 0)
                    }
                } else {
                    //将上一个视图归位 —— 操作取消
                    if (mPrevView != null) {
                        mPrevView!!.translationX = 0f
                    }
                }
                if (mSwipeListener != null) {
                    mSwipeListener!!.onEnd(mScrollPercent >= 1)
                }
            }
        }
    }

    /**
     * 拖动监听接口
     */
    interface SwipeListener {
        /**
         * 滑动时
         *
         * @param scrollPercent 滑动的宽度占屏幕宽度的比例 [SwipeBackLayout.mScrollPercent]
         */
        fun onScrollChanged(scrollPercent: Float)

        /**
         * 滑动开始时
         *
         * @param view 当前视图
         */
        fun onStart(view: View?)

        /**
         * 滑动结束时
         *
         * @param isFinish 视图是否结束
         */
        fun onEnd(isFinish: Boolean)
    }

    init {
        mViewDragHelper = ViewDragHelper.create(this, ViewDragCallback())
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
        mShadowDrawable = ContextCompat.getDrawable(context, R.drawable.swipeback_shadow_left)
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}