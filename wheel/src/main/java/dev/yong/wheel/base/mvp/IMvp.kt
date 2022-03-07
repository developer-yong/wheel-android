@file:Suppress("unused", "UNCHECKED_CAST")

package dev.yong.wheel.base.mvp

import java.lang.reflect.ParameterizedType

/**
 * 注册Mvp开发模式
 *
 * @param view IView实现对象
 * @param <V> View实现类型
 * @param <P> IPresenter实现类型
 */
inline fun <reified V : IView<P>, reified P : IPresenter<V>> registerMvp(view: V) {
    val gInterface = V::class.java.genericInterfaces[0]
    gInterface as ParameterizedType
    val pClass = gInterface.actualTypeArguments[0] as Class<P>
    val presenter = pClass.newInstance()
    presenter.attachView(view)
    view.attachPresenter(presenter)
}

/**
 * 注册Mvp开发模式
 *
 * @param view IView实现对象
 * @param presenter IPresenter实现对象
 * @param <V> View实现类型
 * @param <P> IPresenter实现类型
 */
fun <V : IView<P>, P : IPresenter<V>> registerMvp(view: V, presenter: P) {
    view.attachPresenter(presenter)
    presenter.attachView(view)
}