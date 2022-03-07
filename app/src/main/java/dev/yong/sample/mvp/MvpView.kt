package dev.yong.sample.mvp

import dev.yong.wheel.base.mvp.IView

interface MvpView : IView<MvpPresenter> {

    fun showWeb(html: String)

    fun showEmpty()

    fun showError(error: String)
}
