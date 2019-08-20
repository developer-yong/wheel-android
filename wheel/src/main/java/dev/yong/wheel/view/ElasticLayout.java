package dev.yong.wheel.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import dev.yong.wheel.R;

/**
 * 实现类似IOS的下拉上拉回弹的果冻效果
 * public void setDragOffset(float offset) 设置控件的滑动阻力，有效值为0.1F~1.0F，值越小阻力越大，默认为0.5F
 * public void setOnDragListener(OnDragListener listener) 给控件设置监听，可以监听滑动情况
 * public void setDragMode(int mode) 设置滑动模式
 *
 * @author coderyong
 */
public class ElasticLayout extends FrameLayout {

    public static final int MODE_BOTH = 0x00;
    public static final int MODE_TOP = 0x01;
    public static final int MODE_BOTTOM = 0x02;

    private static final int INVALID_POINTER = -1;

    /**
     * 系统允许最小的滑动判断值
     */
    private int mTouchSlop;

    private int mActivePointerId = INVALID_POINTER;
    private float mDownY;
    private float mMoveY;
    private float mLastMoveY;

    /**
     * 滑动阻力系数
     */
    private float mDragOffset = 0.5F;
    private boolean mIsDragging;
    private int mDragMode = MODE_BOTH;

    private OnDragListener mOnDragListener;
    private OnTouchListener mDelegateTouchListener;

    public ElasticLayout(Context context) {
        this(context, null);
    }

    public ElasticLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ElasticLayout);
        mDragMode = a.getInteger(R.styleable.ElasticLayout_drag_mode, MODE_BOTH);
        a.recycle();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 设置拖动模式
     *
     * @param mode <ul>
     *             <li>{@link #MODE_BOTH} 支持上下拖动</li>
     *             <li>{@link #MODE_TOP} 支持向下拖动</li>
     *             <li>{@link #MODE_BOTTOM} 支持向下拖动</li>
     *             </ul>
     */
    public void setDragMode(int mode) {
        mDragMode = mode;
    }

    /**
     * 获取拖动模式
     * <p>
     * return mode
     * <ul>
     * <li>{@link #MODE_BOTH} 支持上下拖动</li>
     * <li>{@link #MODE_TOP} 支持向下拖动</li>
     * <li>{@link #MODE_BOTTOM} 支持向下拖动</li>
     * </ul>
     */
    public int getDragMode() {
        return mDragMode;
    }

    /**
     * 获得拖动幅度
     *
     * @return float 0F~1F
     */
    public float getDragOffset() {
        return this.mDragOffset;
    }

    /**
     * 设置拖动幅度
     *
     * @param dragOffset 0F~1F
     */
    public void setDragOffset(float dragOffset) {
        this.mDragOffset = dragOffset;
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //判断拦截
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = false;
                mActivePointerId = ev.getPointerId(0);
                float downY = getMotionEventY(ev, mActivePointerId);
                if (downY == -1) {
                    return false;
                }
                mDownY = downY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return super.onInterceptTouchEvent(ev);
                }
                float moveY = getMotionEventY(ev, mActivePointerId);
                if (moveY == -1) {
                    return super.onInterceptTouchEvent(ev);
                }

                if (moveY > mDownY) {
                    //判断是否是下拉操作
                    if (mDragMode == MODE_TOP || mDragMode == MODE_BOTH) {
                        float yDiff = moveY - mDownY;
                        if (yDiff > mTouchSlop && !mIsDragging && !canChildScrollVertically(this, -1)) {
                            mMoveY = mDownY + mTouchSlop;
                            mLastMoveY = mMoveY;
                            mIsDragging = true;
                        }
                    }
                } else if (moveY < mDownY) {
                    //判断是否是上拉操作
                    if (mDragMode == MODE_BOTTOM || mDragMode == MODE_BOTH) {
                        float yDiff = mDownY - moveY;
                        if (yDiff > mTouchSlop && !mIsDragging && !canChildScrollVertically(this, 1)) {
                            mMoveY = mDownY + mTouchSlop;
                            mLastMoveY = mMoveY;
                            mIsDragging = true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                mActivePointerId = INVALID_POINTER;
                break;
            default:
                break;
        }
        return mIsDragging || super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setOnTouchListener(OnTouchListener listener) {
        mDelegateTouchListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDelegateTouchListener != null) {
            return mDelegateTouchListener.onTouch(this, ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float offset;
                float moveY;
                int pointerId = ev.getPointerId(ev.getPointerCount() - 1);
                if (mActivePointerId != pointerId) {
                    mActivePointerId = pointerId;
                    mDownY = getMotionEventY(ev, mActivePointerId);
                    mMoveY = mDownY + mTouchSlop;
                    mLastMoveY = mMoveY;
                }
                offset = getMotionEventY(ev, mActivePointerId) - mLastMoveY;
                //滑动阻力计算
                float tempOffset = 1 - (Math.abs(getTranslationY() + offset) / this.getMeasuredHeight());

                offset = getTranslationY() + offset * mDragOffset * tempOffset;
                mLastMoveY = getMotionEventY(ev, mActivePointerId);
                moveY = getMotionEventY(ev, mActivePointerId) - mMoveY;
                float translationY = getTranslationY();
                switch (mDragMode) {
                    case MODE_BOTH:
                        setTranslationY(offset);
                        break;
                    case MODE_TOP:
                        if (moveY >= 0 || translationY > 0) {
                            //向下滑动
                            if (offset < 0) {
                                //如果还往上滑，就让它归零
                                offset = 0;
                            }
                            if (offset > -1) {
                                offset = -1;
                            }
                            setTranslationY(offset);
                        }
                        break;
                    case MODE_BOTTOM:
                        if (moveY <= 0 || translationY < 0) {
                            //向上滑动
                            if (offset > 0) {
                                //如果还往下滑，就让它归零
                                offset = 0;
                            }
                            setTranslationY(offset);
                        }
                        break;
                    default:
                        break;
                }
                if (mOnDragListener != null) {
                    mOnDragListener.onDragOffset(this, offset);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                reset(200);
                break;
            default:
                break;
        }
        return mIsDragging || super.onInterceptTouchEvent(ev);
    }

    private boolean canChildScrollVertically(ViewGroup view, int direction) {
        boolean canScroll;
        int count = view.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = view.getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup c = (ViewGroup) child;
                if (!c.canScrollVertically(direction) && c.getChildCount() > 0) {
                    canScroll = canChildScrollVertically(c, direction);
                } else {
                    canScroll = c.canScrollVertically(direction) || c.getScrollY() > 0;
                }
            } else {
                canScroll = child.canScrollVertically(direction) || child.getScrollY() > 0;
            }
            if (canScroll) {
                return true;
            }
        }
        return view.canScrollVertically(direction) || view.getScrollY() > 0;
    }

    /**
     * 获取Y轴运动坐标
     *
     * @param ev        Event事件
     * @param pointerId 指针标识
     * @return Y
     */
    private float getMotionEventY(MotionEvent ev, int pointerId) {
        int index = ev.findPointerIndex(pointerId);
        return index < 0 ? -1 : ev.getY(index);
    }

    @Override
    public void setTranslationY(float y) {
        clearAnimation();
        super.setTranslationY(y);
    }

    @Override
    public void setY(float y) {
        clearAnimation();
        super.setY(y);
    }

    public void reset(long duration) {
        clearAnimation();
        ObjectAnimator.ofFloat(this, "translationY", 0F).setDuration(duration).start();
    }

    /**
     * 滚动到指定位置
     *
     * @param y Y轴坐标
     */
    public void scrollTo(float y, long duration) {
        clearAnimation();
        ObjectAnimator.ofFloat(this, "translationY", y).setDuration(duration).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        mDragMode = 0;
        mOnDragListener = null;
    }

    public void setOnDragListener(OnDragListener listener) {
        this.mOnDragListener = listener;
    }

    public interface OnDragListener {
        /**
         * 拖动监听回调，不能操作繁重的任务在这里
         *
         * @param view   拖动的视图
         * @param offset 拖动幅度
         */
        void onDragOffset(View view, float offset);
    }
}