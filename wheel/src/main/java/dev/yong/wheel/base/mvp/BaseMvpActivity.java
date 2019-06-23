package dev.yong.wheel.base.mvp;

import android.os.Bundle;

import dev.yong.wheel.base.BaseActivity;

/**
 * MVPActivity基类
 *
 * @author CoderYong
 */
public abstract class BaseMvpActivity<V, P extends IPresenter<V>> extends BaseActivity {

    protected P mPresenter;

    @Override
    protected void init(Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = providePresenter();
        }
        mPresenter.takeView(provideVew());
        super.init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
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
