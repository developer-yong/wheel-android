package dev.yong.swipeback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.List;

/**
 * @author coderyong
 */
public abstract class SwipeBackFragment extends Fragment {

    protected SwipeBackLayout mSwipeBackLayout;
    /**
     * 滑动返回监听
     */
    protected SwipeBackLayout.SwipeListener mSwipeListener;

    @Override
    public final View onCreateView(@Nullable LayoutInflater inflater,
                                   @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = contentView(inflater, container, savedInstanceState);
        if (isSupportSwipeBack()) {
            mSwipeBackLayout = new SwipeBackLayout(contentView.getContext());
            mSwipeBackLayout.setAlphaColor(alphaColor());
            mSwipeBackLayout.setHasAlpha(hasAlpha());
            mSwipeBackLayout.setHasShadow(hasShadow());
            mSwipeBackLayout.setPrevViewScrollable(prevViewScrollable());
            mSwipeBackLayout.setSwipeListener(mSwipeListener);
            mSwipeBackLayout.setPrevView(prevView());
            mSwipeBackLayout.attachToFragment(this, contentView);
            return mSwipeBackLayout;
        } else {
            return contentView;
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (mSwipeBackLayout != null && mSwipeBackLayout.isFragmentBeforeClosing()) {
            return new Animation() {
            };
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    /**
     * 初始化视图
     * <p>
     * 该方法已被{@link #onCreateView}调用
     * 目的是为了将Fragment根布局绑定到SwipeBackLayout上以便处理滑动返回
     * </P>
     *
     * @param inflater           {@link Fragment#onCreateView}
     * @param container          {@link Fragment#onCreateView}
     * @param savedInstanceState {@link Fragment#onCreateView}
     * @return 根布局视图
     */
    public abstract View contentView(@Nullable LayoutInflater inflater,
                                     @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public int alphaColor() {
        return 0x99000000;
    }

    public boolean hasAlpha() {
        return false;
    }

    public boolean hasShadow() {
        return true;
    }

    public boolean prevViewScrollable() {
        return true;
    }

    public boolean isSupportSwipeBack() {
        return true;
    }

    public void setSwipeListener(SwipeBackLayout.SwipeListener listener) {
        mSwipeListener = listener;
    }

    public View prevView() {
        Fragment preFragment = null;
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            List<Fragment> fragments = manager.getFragments();
            if (fragments != null && fragments.size() > 1) {
                int index = fragments.indexOf(this);
                for (int i = index - 1; i >= 0; i--) {
                    Fragment fragment = fragments.get(i);
                    if (fragment != null && fragment.getView() != null) {
                        preFragment = fragment;
                        break;
                    }
                }
            }
        }
        if (preFragment != null) {
            return preFragment.getView();
        }
        return null;
    }
}
