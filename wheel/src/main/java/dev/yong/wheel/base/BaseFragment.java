package dev.yong.wheel.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.simple.eventbus.EventBus;

import butterknife.ButterKnife;
import dev.yong.swipeback.SwipeBackFragment;
import dev.yong.wheel.AppManager;

/**
 * @author CoderYong
 */
public abstract class BaseFragment extends SwipeBackFragment {

    protected Context mContext;

    /**
     * 滑动返回是否可用
     */
    protected boolean mSwipeBackEnable = false;

    @Nullable
    @Override
    public View contentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (createLayoutId() == 0) {
            throw new Resources.NotFoundException("Not found layout resources, resources id: " + createLayoutId());
        }
        return inflater.inflate(createLayoutId(), container, false);
    }

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        if (AppManager.getInstance().isUseEventBus()) {
            EventBus.getDefault().register(this);
        }
        init(view, savedInstanceState);
        init();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AppManager.getInstance().isUseEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 用于视图、数据、监听等一些初始化操作
     */
    protected void init() {
    }

    /**
     * 用于视图、数据、监听等一些初始化操作
     */
    protected void init(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    /**
     * 创建布局id
     *
     * @return 返回布局id
     */
    protected abstract int createLayoutId();

    public void startActivity(Class<?> clazz) {
        startActivity(new Intent(mContext, clazz));
    }

    /**
     * 启动意图
     *
     * @param bundle 携带Bundle数据
     * @param clazz  启动action Activity
     */
    public void startActivity(Bundle bundle, Class<?> clazz) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return super.isSupportSwipeBack() && mSwipeBackEnable;
    }
}

