package dev.yong.wheel.base.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import javax.inject.Inject;

import dev.yong.wheel.base.BaseFragment;

/**
 * @author CoderYong
 */
public abstract class BaseMvpFragment<V extends IView, P extends IPresenter<V>> extends BaseFragment {

    @Inject
    protected P mPresenter;

    @Override
    protected void init(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mPresenter != null) {
            mPresenter.takeView(takeVew());
        }
        super.init(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.dropView();
        }
    }

    /**
     * Binds P with a V
     *
     * @return V extends IView
     */
    protected abstract V takeVew();

    @Override
    protected boolean isInject() {
        return true;
    }
}

