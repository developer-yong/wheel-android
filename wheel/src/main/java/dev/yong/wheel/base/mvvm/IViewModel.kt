@file:Suppress("unused")

package dev.yong.wheel.base.mvvm

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * @author coderyong
 */
fun <M : ViewModel> ViewModelStoreOwner.getModel(modelClass: Class<M>): M {
    return ViewModelProvider(this).get(modelClass)
}

fun <M : ViewModel> Fragment.getModelWithActivity(modelClass: Class<M>): M {
    return ViewModelProvider(requireActivity()).get(modelClass)
}
