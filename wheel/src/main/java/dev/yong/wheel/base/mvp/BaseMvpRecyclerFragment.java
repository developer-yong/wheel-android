package dev.yong.wheel.base.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.yong.wheel.base.BaseRecyclerFragment;

/**
 * @author CoderYong
 */
public abstract class BaseMvpRecyclerFragment<T, V, P extends IPresenter<V>> extends BaseRecyclerFragment<T> {

    protected P mPresenter = providePresenter();

    @Override
    protected void init(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = providePresenter();
        }
        if (mPresenter != null) {
            mPresenter.takeView(provideView());
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
     * @return V
     */
    protected abstract V provideView();

    /**
     * Binds P
     *
     * @return P
     */
    protected abstract P providePresenter();
}

