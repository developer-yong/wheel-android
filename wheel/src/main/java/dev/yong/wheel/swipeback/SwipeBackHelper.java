package dev.yong.wheel.swipeback;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

/**
 * 滑动返回帮助类
 * <p>
 *      {@link Build.VERSION#SDK_INT } < {@link Build.VERSION_CODES#LOLLIPOP}
 *      需要在ActivityTheme中添加: <item_coupon name="android:windowIsTranslucent">true</item_coupon>
 * </P>
 *
 * @author coderyong
 */
public class SwipeBackHelper {

    public static Builder with(ISwipeBack iSwipeBack) {
        return new Builder(iSwipeBack);
    }

    public static class Builder {
        ISwipeBack mISwipeBack;
        int mAlphaColor = 0x99000000;
        boolean mHasAlpha = false;
        boolean mHasShadow = true;
        boolean mPrevViewScrollable = true;
        SwipeBackLayout.SwipeListener mSwipeListener;

        Builder(ISwipeBack iSwipeBack) {
            if (iSwipeBack instanceof Activity) {
                mISwipeBack = iSwipeBack;
            } else {
                throw new IllegalStateException("ISwipeBack must be implemented by Activity");
            }
        }

        public Builder alphaColor(int alphaColor) {
            mAlphaColor = alphaColor;
            return this;
        }

        public Builder hasAlpha(boolean hasAlpha) {
            mHasAlpha = hasAlpha;
            return this;
        }

        public Builder hasShadow(boolean hasShadow) {
            mHasShadow = hasShadow;
            return this;
        }

        public Builder prevViewScrollable(boolean prevViewScrollable) {
            mPrevViewScrollable = prevViewScrollable;
            return this;
        }

        public Builder swipeListener(SwipeBackLayout.SwipeListener listener) {
            mSwipeListener = listener;
            return this;
        }

        public void build() {
            if (mISwipeBack.isSupportSwipeBack()) {
                Activity activity = (Activity) mISwipeBack;
                activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                activity.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

                SwipeBackLayout backLayout = new SwipeBackLayout(activity);
                backLayout.attachToActivity(activity);
                backLayout.setPrevView(mISwipeBack.prevView());
                backLayout.setAlphaColor(mAlphaColor);
                backLayout.setHasAlpha(mHasAlpha);
                backLayout.setHasShadow(mHasShadow);
                backLayout.setPrevViewScrollable(mPrevViewScrollable);
                backLayout.setSwipeListener(mSwipeListener);
            }
        }
    }
}
