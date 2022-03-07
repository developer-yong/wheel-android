package dev.yong.sample.swipeback

import android.os.Bundle
import android.view.View
import dev.yong.sample.R
import dev.yong.sample.databinding.FragmentBBinding
import dev.yong.wheel.Router
import dev.yong.wheel.base.SwipeBackFragment

class BFragment : SwipeBackFragment<FragmentBBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRoot.btnB.setOnClickListener {
            Router.with(requireActivity(), R.id.layout_container)
                .open(CFragment::class.java, null)
        }
        mRoot.btnBack.setOnClickListener {
            Router.finish(this)
        }
    }
}