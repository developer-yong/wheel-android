package dev.yong.wheel.swipeback;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import dev.yong.wheel.R;

/**
 * @author coderyong
 */
public class SwipeBackLayout extends FrameLayout {

    /**
     * 视图拖动帮助类
     */
    private ViewDragHelper mViewDragHelper;
    /**
     * 当前滑动的宽度占屏幕宽度的比例 [0~1]
     */
    private float mScrollPercent;

    /**
     * 记录透明度占屏幕宽度的比例 [0~1]
     */
    private float mAlphaPercent;
    /**
     * 拖动时透明区域的颜色
     */
    private int mAlphaColor = 0x99000000;
    /**
     * 是否有透明度（默认为false）
     */
    private boolean mHasAlpha = false;
    /**
     * 上一个视图是否可以滚动（默认为true）
     */
    private boolean mPrevViewScrollable = true;

    /**
     * 阴影图
     */
    private Drawable mShadowDrawable;
    /**
     * 是否有阴影（默认为true）
     */
    private boolean mHasShadow = true;

    private SwipeListener mSwipeListener;

    /**
     * 上一个视图
     */
    private View mPrevView;
    /**
     * 当前Fragment
     */
    private Fragment mFragment;

    private boolean fragmentBeforeClosing = false;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mShadowDrawable = ContextCompat.getDrawable(context, R.drawable.swipeback_shadow_left);
        if (getLayoutParams() == null) {
            setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return mViewDragHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        mAlphaPercent = 1 - mScrollPercent;
        if (mViewDragHelper.continueSettling(true)) {
            //使当前视图下一个动画时间步骤失效
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (mAlphaPercent > 0) {
            if (mHasShadow) {
                drawShadow(canvas, child);
            }
            if (mHasAlpha) {
                drawAlpha(canvas, child);
            }
        }
        return ret;
    }

    /**
     * 绘制阴影
     *
     * @param canvas 画板
     * @param view   视图
     */
    private void drawShadow(Canvas canvas, View view) {
        Rect rect = new Rect();
        view.getHitRect(rect);
        mShadowDrawable.setBounds(rect.left - mShadowDrawable.getIntrinsicWidth(), rect.top, rect.left, rect.bottom);
        mShadowDrawable.setAlpha((int) (mAlphaPercent * 255));
        mShadowDrawable.draw(canvas);
    }

    /**
     * 绘制透明区域,拖动的越远越透明
     *
     * @param canvas 画板
     * @param view   视图
     */
    private void drawAlpha(Canvas canvas, View view) {
        final int baseAlpha = (mAlphaColor & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mAlphaPercent);
        final int color = alpha << 24 | (mAlphaColor & 0xffffff);
        canvas.clipRect(0, 0, view.getLeft(), getHeight());
        canvas.drawColor(color);
    }

    /**
     * 设置拖动时是否有阴影
     *
     * @param hasShadow 是否有阴影
     */
    public void setHasShadow(boolean hasShadow) {
        mHasShadow = hasShadow;
    }

    /**
     * 设置默认透明度
     *
     * @param alphaColor 透明度16进制颜色，默认为0x99000000
     */
    public void setAlphaColor(int alphaColor) {
        this.mAlphaColor = alphaColor;
    }

    /**
     * 设置拖动时是否有透明度
     *
     * @param hasAlpha 是否有透明度
     */
    public void setHasAlpha(boolean hasAlpha) {
        mHasAlpha = hasAlpha;
    }

    /**
     * 设置上一个视图
     *
     * @param prevView 上一个视图View
     */
    public void setPrevView(View prevView) {
        mPrevView = prevView;
    }

    /**
     * 设置上一个视图是否可以滚动
     *
     * @param scrollable 是否有阴影
     */
    public void setPrevViewScrollable(boolean scrollable) {
        mPrevViewScrollable = scrollable;
    }

    public void setSwipeListener(SwipeListener listener) {
        mSwipeListener = listener;
    }

    /**
     * 移动背景
     */
    private void moveBackgroundLayout(View background) {
        //mScrollPercent 变化时: 0 -> 0.95 -> 1
        //背景translationX的变化: -0.4 -> 0 -> 0
        if (background != null) {
            float translationX = (float) (0.4 / 0.95 * (mScrollPercent - 0.95) * background.getWidth());
            if (translationX > 0) {
                translationX = 0;
            }
            background.setTranslationX(translationX);
        }
    }

    public void attachToActivity(Activity activity) {
        TypedArray a = activity.getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        decor.addView(this);
    }

    public void attachToFragment(Fragment fragment, View view) {
        mFragment = fragment;
        addView(view);
    }

    /**
     * Fragment 关闭前
     *
     * @return 是否在关闭前
     */
    public boolean isFragmentBeforeClosing() {
        return fragmentBeforeClosing;
    }

    /**
     * 设置当前Fragment视图与上一个Fragment视图是否在关闭前
     *
     * @param fragmentBeforeClosing true进入关闭前，false没有进入关闭或者已经关闭
     */
    public void setFragmentBeforeClosing(boolean fragmentBeforeClosing) {
        this.fragmentBeforeClosing = fragmentBeforeClosing;
        if (mPrevView != null && mPrevView instanceof SwipeBackLayout) {
            //设置上一个Fragment进入前与当前Fragment关闭前状态同步
            ((SwipeBackLayout) mPrevView).setFragmentBeforeClosing(fragmentBeforeClosing);
        }
    }

    private boolean canSwipeActivity() {
        Activity activity = (Activity) getContext();
        List fragments = null;
        if (activity instanceof AppCompatActivity) {
            fragments = ((AppCompatActivity) activity).getSupportFragmentManager().getFragments();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fragments = activity.getFragmentManager().getFragments();
            }
        }
        int count = 0;
        if (fragments != null) {
            for (Object o : fragments) {
                if (o instanceof SwipeBackFragment) {
                    count++;
                }
            }
            if (count == 1 && fragments.size() > count) {
                count++;
            }
        }
        return count <= 1;
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        /**
         * 当用户触发拖动时调用
         *
         * @param child     拖动的视图
         * @param pointerId {@link ViewDragHelper.Callback#tryCaptureView(View, int)}
         * @return 返回拖动是否可用
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            boolean dragEnable = mViewDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointerId);
            if (dragEnable) {
                if (mSwipeListener != null) {
                    mSwipeListener.onStart(child);
                }
                if (mFragment != null) {
                    if (mPrevView != null) {
                        mPrevView.setVisibility(VISIBLE);
                    }
                } else {
                    Utils.convertActivityToTranslucent((Activity) child.getContext());
                }
            }
            return dragEnable;
        }

        /**
         * 获取视图水平拖动范围
         *
         * @param child 拖动的视图
         * @return 返回0时 不可拖动
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            if (mFragment != null) {
                return 1;
            } else {
                return canSwipeActivity() ? 1 : 0;
            }
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return Math.min(child.getWidth(), Math.max(left + dx / 2, 0));
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
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //滑动宽度占屏幕宽度的比例
            mScrollPercent = Math.abs((float) left / changedView.getWidth());
            if (mPrevViewScrollable && mPrevView != null) {
                moveBackgroundLayout(mPrevView);
            }
            if (mSwipeListener != null) {
                mSwipeListener.onScrollChanged(mScrollPercent);
            }
            invalidate();
        }

        /**
         * 当用户拖动释放时调用
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float x, float y) {
            //如果释放时滑动宽度占屏幕宽度的比例大于50%视图移出屏幕，否则视图还原到初始
            if (mScrollPercent > 0.5F) {
                mViewDragHelper.settleCapturedViewAt(releasedChild.getWidth() + 1, 0);
            } else {
                mViewDragHelper.settleCapturedViewAt(0, 0);
            }
            invalidate();
        }

        /**
         * 拖动状态改变
         *
         * @param state 拖动状态
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                if (mScrollPercent >= 1) {
                    if (mFragment != null && !mFragment.isDetached()) {
                        //Fragment进入关闭
                        setFragmentBeforeClosing(true);
                        FragmentManager manager = mFragment.getFragmentManager();
                        if (manager != null) {
                            //移除当前Fragment
                            manager.beginTransaction().remove(mFragment).commit();
                            manager.popBackStackImmediate();
                        }
                        //Fragment已经关闭
                        setFragmentBeforeClosing(false);
                    } else {
                        //将视图销毁
                        ((Activity) getContext()).finish();
                        //无动画退出
                        ((Activity) getContext()).overridePendingTransition(0, 0);
                    }
                } else {
                    //将上一个视图归位 —— 操作取消
                    if (mPrevView != null) {
                        mPrevView.setTranslationX(0);
                    }
                }
                if (mSwipeListener != null) {
                    mSwipeListener.onEnd(mScrollPercent >= 1);
                }
            }
        }
    }

    /**
     * 拖动监听接口
     */
    public interface SwipeListener {
        /**
         * 滑动时
         *
         * @param scrollPercent 滑动的宽度占屏幕宽度的比例 {@link SwipeBackLayout#mScrollPercent}
         */
        void onScrollChanged(float scrollPercent);

        /**
         * 滑动开始时
         *
         * @param view 当前视图
         */
        void onStart(View view);

        /**
         * 滑动结束时
         *
         * @param isFinish 视图是否结束
         */
        void onEnd(boolean isFinish);
    }
}
