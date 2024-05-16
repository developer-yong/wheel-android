package dev.yong.sample.mvvm

import android.os.Bundle
import dev.yong.sample.databinding.FragmentMvvmBinding
import dev.yong.wheel.base.ViewBindFragment
import dev.yong.wheel.base.mvvm.getModelWithActivity

class MVVMFragment : ViewBindFragment<FragmentMvvmBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model = getModelWithActivity(MVVMModel::class.java)
        model.getData().observe(this) {

        }
    }
}