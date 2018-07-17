package dev.yong.wheel.base.mvp;

/**
 * @author coderyong
 */
public abstract class BasePresenter<V extends IView, M extends IModel> implements IPresenter<V> {

    protected M mModel;
    protected V mView;

    public BasePresenter(M model) {
        this.mModel = model;
    }

    @Override
    public void takeView(V view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
        mModel.onDestroy();
        mModel = null;
    }
}
