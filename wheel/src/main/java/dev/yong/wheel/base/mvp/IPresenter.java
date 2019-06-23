package dev.yong.wheel.base.mvp;

/**
 * @author coderyong
 */
public interface IPresenter<V> {
    /**
     * 使用视图
     *
     * @param view the view associated with this presenter
     */
    void takeView(V view);

    /**
     * Drops the reference to the view when destroyed
     */
    void dropView();

}
