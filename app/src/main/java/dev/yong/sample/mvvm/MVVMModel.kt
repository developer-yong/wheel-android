package dev.yong.sample.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author coderyong
 */
class MVVMModel : ViewModel(){

    private val data: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>().also {
            loadData()
        }
    }

    fun getData(): LiveData<List<String>> {
        return data
    }

    private fun loadData() {

    }
}