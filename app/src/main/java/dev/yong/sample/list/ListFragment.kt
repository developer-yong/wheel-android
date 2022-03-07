package dev.yong.sample.list

import android.os.Bundle
import android.view.View
import dev.yong.sample.R
import dev.yong.sample.databinding.FragmentListBinding
import dev.yong.sample.map.MapFragment
import dev.yong.sample.mvp.MvpFragment
import dev.yong.sample.swipeback.SwipeBackActivity
import dev.yong.sample.viewpager.ViewPagerFragment
import dev.yong.wheel.Router
import dev.yong.wheel.base.ViewBindFragment
import dev.yong.wheel.base.adapter.BaseRecyclerAdapter
import dev.yong.wheel.utils.Recycler
import dev.yong.wheel.view.BottomDialog
import dev.yong.wheel.web.PARAM_WEB_TITLE

class ListFragment : ViewBindFragment<FragmentListBinding>(),
    BaseRecyclerAdapter.OnItemClickListener {

    private lateinit var mAdapter: ListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = Recycler.with(mRoot.recycler).build()

        mAdapter.setOnItemClickListener(this)

        mAdapter.addData("Mvp")
        mAdapter.addData("滑动返回")
        mAdapter.addData("Web")
        mAdapter.addData("Map")
        mAdapter.addData("viewpager")
        mAdapter.addData("bottom-menu")

    }

    override fun onItemClick(position: Int) {
        when (mAdapter.getChildAt(position)) {
            "Mvp" -> {
                Router.with(requireActivity(), R.id.layout_container)
                    .open(MvpFragment::class.java, null)
            }
            "滑动返回" -> {
                Router.with(requireContext())
                    .start(SwipeBackActivity::class.java)
            }
            "Web" -> {
                Router.with(requireContext())
                    .putExtra(PARAM_WEB_TITLE, "GitHub")
                    .start("https://github.com/")
            }
            "Map" -> {
                Router.with(requireActivity(), R.id.layout_container)
                    .open(MapFragment::class.java, null)
            }
            "viewpager" -> {
                Router.with(requireActivity(), R.id.layout_container)
                    .open(ViewPagerFragment::class.java, null)
            }
            "bottom-menu" -> {
                BottomDialog(requireContext(), R.layout.layout_bottom)
                    .setMargin(30)
                    .show()
            }
        }
    }
}