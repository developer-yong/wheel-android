package dev.yong.sample.mvp

import dev.yong.wheel.base.mvp.IPresenter
import dev.yong.wheel.http.Callback
import dev.yong.wheel.http.OkHttpHelper

class MvpPresenter : IPresenter<MvpView> {

    private lateinit var mView: MvpView

    override fun attachView(view: MvpView) {
        mView = view
    }

    fun loadData() {
        OkHttpHelper.get("https://www.baidu.com/")
            .enqueue(object : Callback<String> {
                override fun onResponse(t: String) {
                    mView.showWeb(t)
                }

                override fun onFailure(t: Throwable) {
                    mView.showError(t.toString())
                }
            })
    }
}
