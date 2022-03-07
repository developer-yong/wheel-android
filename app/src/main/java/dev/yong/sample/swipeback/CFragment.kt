package dev.yong.sample.swipeback

import android.os.Bundle
import android.view.View
import dev.yong.sample.R
import dev.yong.sample.databinding.FragmentCBinding
import dev.yong.wheel.Router
import dev.yong.wheel.base.SwipeBackFragment

class CFragment : SwipeBackFragment<FragmentCBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRoot.btnC.setOnClickListener {
            Router.with(requireActivity(), R.id.layout_container)
                .open(AFragment::class.java, null)
        }
        mRoot.btnBack.setOnClickListener {
            Router.finish(this)
        }
    }
}