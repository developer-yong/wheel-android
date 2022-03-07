package dev.yong.wheel.base.mvp

/**
 * @author coderyong
 */
interface IPresenter<V> {

    fun attachView(view: V)
}