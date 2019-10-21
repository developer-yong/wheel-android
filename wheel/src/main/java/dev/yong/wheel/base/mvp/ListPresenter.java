package dev.yong.wheel.base.mvp;

/**
 * @author coderyong
 */
public class ListPresenter<T> implements IPresenter<ListView<T>> {

    protected ListView<T> mView;

    @Override
    public void takeView(ListView<T> view) {
        mView = view;
    }

    @Override
    public void dropView() {

    }
}
