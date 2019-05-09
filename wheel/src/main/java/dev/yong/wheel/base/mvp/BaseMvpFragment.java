package dev.yong.wheel.base.mvp;

import javax.inject.Inject;

import dev.yong.wheel.base.BaseFragment;

/**
 * @author CoderYong
 */
public abstract class BaseMvpFragment<V extends IView, P extends IPresenter<V>> extends BaseFragment {

    @Inject
    protected P mPresenter;

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.takeView(takeVew());
        }
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

