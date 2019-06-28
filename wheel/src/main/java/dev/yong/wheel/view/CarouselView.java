package dev.yong.wheel.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author coderyong
 */
public class CarouselView extends FrameLayout implements ViewPager.OnPageChangeListener {

    public ViewPager mPager;
    private LinearLayout mLayoutDot;

    private float mDensity;

    private int pageCount;
    private float indicatorSize = 10.0F;
    private float indicatorSpacing = 10.0F;
    @DrawableRes
    private int indicatorEnableSelector;
    private int gravity = Gravity.CENTER;

    private boolean enableRunning = true;
    private long delayMillis = 3000;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentItem = mPager.getCurrentItem();
            if (currentItem == pageCount) {
                currentItem = 0;
                mPager.setCurrentItem(currentItem, false);
            }
            mPager.setCurrentItem(++currentItem, true);
        }
    };

    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDensity = context.getResources().getDisplayMetrics().density;

        //添加ViewPager
        mPager = new ViewPager(context);
        mPager.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        addView(mPager);

        //添加指示器根布局
        mLayoutDot = new LinearLayout(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        mLayoutDot.setLayoutParams(params);
        mLayoutDot.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLayoutDot);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                enableRunning = false;
                break;
            default:
                enableRunning = true;
                break;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        enableRunning = positionOffset == 0;
        if (mHandler != null) {
            if (enableRunning) {
                mHandler.sendEmptyMessageDelayed(0, delayMillis);
            } else {
                mHandler.removeCallbacksAndMessages(null);
            }
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mLayoutDot != null) {
            if (position == 0) {
                position = pageCount - 1;
            } else if (position == pageCount + 1) {
                position = 0;
            } else {
                position = position - 1;
            }
            for (int i = 0; i < mLayoutDot.getChildCount(); i++) {
                mLayoutDot.getChildAt(i).setEnabled(i == position);
            }
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position % pageCount);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    private int dip2px(float dpValue) {
        return (int) (dpValue * mDensity + 0.5F);
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
        if (pageCount > 0) {
            enableRunning = pageCount > 1;

            mPager.setAdapter(new ImageAdapter());
            mPager.setCurrentItem(1);
            mPager.addOnPageChangeListener(this);

            //开启handler的线程 delayMillis 毫秒之后发出此消息
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, delayMillis);
            }
            updateIndicator();
        }
    }

    private void updateIndicator() {
        //避免该方法重复调用添加
        if (mLayoutDot.getChildCount() > 0) {
            mLayoutDot.removeAllViews();
        }
        for (int i = 0; i < pageCount; i++) {
            View view = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(dip2px(indicatorSize), dip2px(indicatorSize));
            params.leftMargin = i == 0 ? 0 : dip2px(indicatorSpacing);
            if (indicatorEnableSelector != 0) {
                view.setBackgroundResource(indicatorEnableSelector);
            }
            view.setEnabled(i + 1 == mPager.getCurrentItem());
            view.setLayoutParams(params);
            int padding = dip2px(indicatorSpacing);
            mLayoutDot.setPadding(padding, padding, padding, padding);
            mLayoutDot.addView(view);
        }
        mLayoutDot.setGravity(gravity);
        mLayoutDot.setVisibility(pageCount > 1 ? VISIBLE : GONE);
    }

    public void setIndicatorSize(float indicatorSize) {
        this.indicatorSize = indicatorSize;
        updateIndicator();
    }

    public void setIndicatorSpacing(float indicatorSpacing) {
        this.indicatorSpacing = indicatorSpacing;
        updateIndicator();
    }

    public void setIndicatorEnableSelector(@DrawableRes int enableSelector) {
        this.indicatorEnableSelector = enableSelector;
        updateIndicator();
    }

    public void setIndicatorGravity(int gravity) {
        this.gravity = gravity;
        updateIndicator();
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    private ItemInstantiateFactory mItemInstantiateFactory;

    public void setItemInstantiateFactory(ItemInstantiateFactory factory) {
        mItemInstantiateFactory = factory;
    }

    public interface ItemInstantiateFactory {

        /**
         * 创建ItemView实例
         *
         * @param container ViewPager容器
         * @param position  View对应位置
         * @return View实例
         */
        @NonNull
        View instantiateItem(ViewGroup container, int position);
    }

    private OnImageViewCreateListener mOnImageViewCreateListener;

    public void setOnImageViewCreateListener(OnImageViewCreateListener listener) {
        mOnImageViewCreateListener = listener;
    }

    public interface OnImageViewCreateListener {

        /**
         * ImageView 创建时监听回调方法
         *
         * @param imageView imageView
         * @param position  对应位置
         */
        void onCreate(ImageView imageView, int position);
    }

    private OnPageChangeListener mOnPageChangeListener;

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public interface OnPageChangeListener {

        /**
         * 页面选中时调用
         *
         * @param position 选中位置
         */
        void onPageSelected(int position);

        /**
         * 页面滑动时调用
         *
         * @param position             滑动位置
         * @param positionOffset       滑动偏移量
         * @param positionOffsetPixels 滑动偏移量像素
         */
        default void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        /**
         * 页面滚动状态改变时调用
         *
         * @param state ViewPager 滚动状态
         */
        default void onPageScrollStateChanged(int state) {
        }
    }

    class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageCount > 0 ? pageCount + 2 : 0;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (mItemInstantiateFactory != null) {
                View itemView = mItemInstantiateFactory.instantiateItem(container, position);
                container.addView(itemView);
                return itemView;
            } else {
                //对Viewpager页号求模去除View列表中要显示的项
                ImageView imageView = new ImageView(container.getContext());
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, WRAP_CONTENT);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(params);
                if (mOnImageViewCreateListener != null) {
                    if (position == 0) {
                        position = pageCount - 1;
                    } else if (position == pageCount + 1) {
                        position = 0;
                    } else {
                        position = position - 1;
                    }
                    mOnImageViewCreateListener.onCreate(imageView, position);
                }
                container.addView(imageView);
                return imageView;
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            super.finishUpdate(container);
            if (mPager.getCurrentItem() == 0) {
                if (!enableRunning) {
                    mPager.setCurrentItem(pageCount, false);
                }
            } else if (mPager.getCurrentItem() == (pageCount + 2) - 1) {
                mPager.setCurrentItem(1, false);
            }
        }
    }
}