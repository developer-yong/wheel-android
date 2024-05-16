package dev.yong.sample.viewpager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dev.yong.sample.databinding.FragmentViewPagerBinding
import dev.yong.sample.swipeback.AFragment
import dev.yong.sample.swipeback.BFragment
import dev.yong.sample.swipeback.CFragment
import dev.yong.wheel.base.ViewBindFragment

class ViewPagerFragment : ViewBindFragment<FragmentViewPagerBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = object : FragmentStateAdapter(this) {

            private val mFragments: MutableList<Fragment> = ArrayList()

            fun addFragment(fragment: Fragment) {
                mFragments.add(fragment)
            }

            override fun createFragment(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getItemCount(): Int {
                return mFragments.size
            }
        }

        adapter.addFragment(AFragment())
        adapter.addFragment(BFragment())
        adapter.addFragment(CFragment())

        mRoot?.run {
            viewpager.adapter = adapter

            TabLayoutMediator(
                tab, viewpager
            ) { tab, position ->
                when (position) {
                    0 -> tab.text = "First"
                    1 -> tab.text = "Second"
                    else -> tab.text = "Third"
                }
            }.attach()
        }
    }
}