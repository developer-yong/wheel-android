package dev.yong.sample.mvp

import android.os.Bundle
import android.view.View
import dev.yong.sample.databinding.FragmentMvpBinding
import dev.yong.wheel.base.SwipeBackFragment
import dev.yong.wheel.base.mvp.registerMvp
import dev.yong.wheel.utils.showSnack
import dev.yong.wheel.utils.visible
import dev.yong.wheel.view.EmptyView

class MvpFragment : SwipeBackFragment<FragmentMvpBinding>(), MvpView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerMvp(this)
        mRoot?.layoutHtml?.prefixTextView?.visible()
//        mRoot.layoutHtml.editText?.isFocusable = true;
//        mRoot.layoutHtml.editText?.isFocusableInTouchMode = true
//        mRoot.layoutHtml.editText?.requestFocus()
//
//        mRoot.layoutHtml.endIconDrawable =
//            ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_delete)
//        mRoot.layoutHtml.setEndIconOnClickListener(View.OnClickListener {  })
//        EmptyView.with(mRoot.etHtml).show()
    }

    override fun showWeb(html: String) {
        mRoot?.run {
            EmptyView.with(etHtml).hide()
        }
        mRoot?.etHtml?.setText(html)
    }

    override fun showEmpty() {
        mRoot?.run {
            EmptyView.with(etHtml).show()
        }
    }

    override fun showError(error: String) {
        mRoot?.run {
            EmptyView.with(etHtml).show()
        }
        mRoot?.root?.showSnack(error)
    }

    override fun attachPresenter(presenter: MvpPresenter) {
//        presenter.loadData()
    }
}
