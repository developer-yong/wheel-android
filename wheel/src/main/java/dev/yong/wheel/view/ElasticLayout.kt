@file:Suppress("unused")

package dev.yong.wheel.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.Keep
import dev.yong.wheel.R
import kotlin.math.abs

/**
 * 实现类似IOS的下拉上拉回弹的果冻效果
 * public void setDragOffset(float offset) 设置控件的滑动阻力，有效值为0.1F~1.0F，值越小阻力越大，默认为0.5F
 * public void setOnDragListener(OnDragListener listener) 给控件设置监听，可以监听滑动情况
 * public void setDragMode(int mode) 设置滑动模式
 *
 * @author coderyong
 */
class ElasticLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    /**
     * 系统允许最小的滑动判断值
     */
    private val mTouchSlop: Int
    private var mActivePointerId = INVALID_POINTER
    private var mDownY = 0f
    private var mMoveY = 0f
    private var mLastMoveY = 0f

    /**
     * 滑动阻力系数
     */
    var dragOffset = 0.5f
    var isDragging = false
        private set

    /**
     * 拖动模式
     *
     *  * [.MODE_BOTH] 支持上下拖动
     *  * [.MODE_TOP] 支持向下拖动
     *  * [.MODE_BOTTOM] 支持向下拖动
     *
     */
    var dragMode = MODE_BOTH
    private var mOnDragListener: OnDragListener? = null
    private var mDelegateTouchListener: OnTouchListener? = null
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //判断拦截
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false
                mActivePointerId = ev.getPointerId(0)
                val downY = getMotionEventY(ev, mActivePointerId)
                if (downY == -1f) {
                    return false
                }
                mDownY = downY
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return super.onInterceptTouchEvent(ev)
                }
                val moveY = getMotionEventY(ev, mActivePointerId)
                if (moveY == -1f) {
                    return super.onInterceptTouchEvent(ev)
                }
                if (moveY > mDownY) {
                    //判断是否是下拉操作
                    if (dragMode == MODE_TOP || dragMode == MODE_BOTH) {
                        val yDiff = moveY - mDownY
                        if (yDiff > mTouchSlop && !isDragging && !canChildScrollVertically(
                                this,
                                -1
                            )
                        ) {
                            mMoveY = mDownY + mTouchSlop
                            mLastMoveY = mMoveY
                            isDragging = true
                        }
                    }
                } else if (moveY < mDownY) {
                    //判断是否是上拉操作
                    if (dragMode == MODE_BOTTOM || dragMode == MODE_BOTH) {
                        val yDiff = mDownY - moveY
                        if (yDiff > mTouchSlop && !isDragging && !canChildScrollVertically(
                                this,
                                1
                            )
                        ) {
                            mMoveY = mDownY + mTouchSlop
                            mLastMoveY = mMoveY
                            isDragging = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                mActivePointerId = INVALID_POINTER
            }
            else -> {
            }
        }
        return isDragging || super.onInterceptTouchEvent(ev)
    }

    override fun setOnTouchListener(listener: OnTouchListener) {
        mDelegateTouchListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mDelegateTouchListener != null) {
            return mDelegateTouchListener!!.onTouch(this, ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                var offset: Float
                val pointerId = ev.getPointerId(ev.pointerCount - 1)
                if (mActivePointerId != pointerId) {
                    mActivePointerId = pointerId
                    mDownY = getMotionEventY(ev, mActivePointerId)
                    mMoveY = mDownY + mTouchSlop
                    mLastMoveY = mMoveY
                }
                offset = getMotionEventY(ev, mActivePointerId) - mLastMoveY
                //滑动阻力计算
                val tempOffset = 1 - abs(translationY + offset) / this.measuredHeight
                offset = translationY + offset * dragOffset * tempOffset
                mLastMoveY = getMotionEventY(ev, mActivePointerId)
                val moveY: Float = getMotionEventY(ev, mActivePointerId) - mMoveY
                val translationY = translationY
                when (dragMode) {
                    MODE_BOTH -> setTranslationY(offset)
                    MODE_TOP -> if (moveY >= 0 || translationY > 0) {
                        //向下滑动
                        if (offset < 0) {
                            //如果还往上滑，就让它归零
                            offset = 0f
                        }
                        if (offset > -1) {
                            offset = -1f
                        }
                        setTranslationY(offset)
                    }
                    MODE_BOTTOM -> if (moveY <= 0 || translationY < 0) {
                        //向上滑动
                        if (offset > 0) {
                            //如果还往下滑，就让它归零
                            offset = 0f
                        }
                        setTranslationY(offset)
                    }
                    else -> {
                    }
                }
                if (mOnDragListener != null) {
                    mOnDragListener!!.onDragOffset(this, offset)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> reset(200)
            else -> {
            }
        }
        return isDragging || super.onInterceptTouchEvent(ev)
    }

    private fun canChildScrollVertically(view: ViewGroup, direction: Int): Boolean {
        var canScroll: Boolean
        val count = view.childCount
        for (i in 0 until count) {
            val child = view.getChildAt(i)
            canScroll = if (child is ViewGroup) {
                if (!child.canScrollVertically(direction) && child.childCount > 0) {
                    canChildScrollVertically(child, direction)
                } else {
                    child.canScrollVertically(direction) || child.scrollY > 0
                }
            } else {
                child.canScrollVertically(direction) || child.scrollY > 0
            }
            if (canScroll) {
                return true
            }
        }
        return view.canScrollVertically(direction) || view.scrollY > 0
    }

    /**
     * 获取Y轴运动坐标
     *
     * @param ev        Event事件
     * @param pointerId 指针标识
     * @return Y
     */
    private fun getMotionEventY(ev: MotionEvent, pointerId: Int): Float {
        val index = ev.findPointerIndex(pointerId)
        return if (index < 0) -1F else ev.getY(index)
    }

    @Keep
    override fun setTranslationY(y: Float) {
        clearAnimation()
        super.setTranslationY(y)
    }

    @Keep
    override fun setY(y: Float) {
        clearAnimation()
        super.setY(y)
    }

    fun reset(duration: Long) {
        clearAnimation()
        ObjectAnimator.ofFloat(this, "translationY", 0f)
            .setDuration(duration).start()
    }

    /**
     * 滚动到指定位置
     *
     * @param y Y轴坐标
     */
    fun scrollTo(y: Float, duration: Long) {
        clearAnimation()
        ObjectAnimator.ofFloat(this, "translationY", y)
            .setDuration(duration).start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
        dragMode = 0
        mOnDragListener = null
    }

    fun setOnDragListener(listener: OnDragListener?) {
        mOnDragListener = listener
    }

    interface OnDragListener {
        /**
         * 拖动监听回调，不能操作繁重的任务在这里
         *
         * @param view   拖动的视图
         * @param offset 拖动幅度
         */
        fun onDragOffset(view: View?, offset: Float)
    }

    companion object {
        const val MODE_BOTH = 0x00
        const val MODE_TOP = 0x01
        const val MODE_BOTTOM = 0x02
        private const val INVALID_POINTER = -1
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ElasticLayout)
        dragMode = a.getInteger(R.styleable.ElasticLayout_drag_mode, MODE_BOTH)
        a.recycle()
        mTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop
    }
}