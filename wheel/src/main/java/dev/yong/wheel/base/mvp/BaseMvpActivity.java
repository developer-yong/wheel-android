package dev.yong.wheel.base.mvp;

import android.os.Bundle;

import dev.yong.wheel.base.BaseActivity;

/**
 * MVPActivity基类
 *
 * @author CoderYong
 */
public abstract class BaseMvpActivity<V, P extends IPresenter<V>> extends BaseActivity {

    protected P mPresenter = providePresenter();

    @Override
    protected void init(Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = providePresenter();
        }
        if (mPresenter != null) {
            mPresenter.takeView(provideView());
        }
        super.init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
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

