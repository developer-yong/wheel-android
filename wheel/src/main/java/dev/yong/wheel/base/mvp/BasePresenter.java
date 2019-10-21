package dev.yong.wheel.base.mvp;

/**
 * @author coderyong
 */
public class BasePresenter<V> implements IPresenter<V> {

    protected V mView;

    @Override
    public void takeView(V view) {
        mView = view;
    }

    @Override
    public void dropView() {

    }
}
