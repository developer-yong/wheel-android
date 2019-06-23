package dev.yong.wheel.base.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import dev.yong.wheel.base.BaseFragment;

/**
 * @author CoderYong
 */
public abstract class BaseMvpFragment<V, P extends IPresenter<V>> extends BaseFragment {

    protected P mPresenter;

    @Override
    protected void init(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = providePresenter();
        }
        mPresenter.takeView(provideVew());
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
     * @return V
     */
    protected abstract V provideVew();

    /**
     * Binds P
     *
     * @return P
     */
    protected abstract P providePresenter();
}

