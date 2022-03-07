package dev.yong.wheel.base.mvp

/**
 * @author coderyong
 */
interface IView<P> {

    fun attachPresenter(presenter: P)
}