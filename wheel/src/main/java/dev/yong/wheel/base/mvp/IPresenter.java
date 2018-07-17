package dev.yong.wheel.base.mvp;

/**
 * @author coderyong
 */
public interface IPresenter<V extends IView> {
    /**
     * Binds presenter with a view when resumed. The Presenter will perform initialization here.
     *
     * @param view the view associated with this presenter
     */
    void takeView(V view);

    /**
     * Drops the reference to the view when destroyed
     */
    void dropView();

}
