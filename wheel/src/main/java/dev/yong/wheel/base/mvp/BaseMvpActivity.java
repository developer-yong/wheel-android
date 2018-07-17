package dev.yong.wheel.base.mvp;

import javax.inject.Inject;

import dev.yong.wheel.base.BaseActivity;

/**
 * MVPActivity基类
 *
 * @author CoderYong
 */
public abstract class BaseMvpActivity<V extends IView, P extends IPresenter<V>> extends BaseActivity {

    @Inject
    protected P mPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.takeView(takeVew());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    /**
     * Binds P with a V
     *
     * @return V extends IView
     */
    protected abstract V takeVew();
}
