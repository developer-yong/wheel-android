package dev.yong.sample.swipeback

import android.os.Bundle
import android.view.View
import dev.yong.sample.R
import dev.yong.sample.databinding.FragmentABinding
import dev.yong.wheel.Router
import dev.yong.wheel.base.ViewBindFragment

class AFragment : ViewBindFragment<FragmentABinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRoot.btnA.setOnClickListener {
            Router.with(requireActivity(), R.id.layout_container)
                .open(BFragment::class.java, null)
        }
        mRoot.btnBack.setOnClickListener {
            Router.finish(this)
        }
    }
}