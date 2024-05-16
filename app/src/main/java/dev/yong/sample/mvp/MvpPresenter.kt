package dev.yong.sample.mvp

import dev.yong.wheel.base.mvp.IPresenter
import dev.yong.wheel.http.Callback
import dev.yong.wheel.http.Transfer

class MvpPresenter : IPresenter<MvpView> {

    private lateinit var mView: MvpView

    override fun attachView(view: MvpView) {
        mView = view
    }

    fun loadData() {
        Transfer.get("https://www.baidu.com/")
            .execute(object : Callback<String> {
                override fun onResponse(t: String) {
                    mView.showWeb(t)
                }
            })
    }
}
