package com.krosskomics.common.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krosskomics.more.repository.MoreRepository

open class FragmentBaseViewModel(val context: Context) : ViewModel() {
    var items = arrayListOf<Any>()
    var page = 1
    var totalPage = 1
    var isRefresh = false
    var tabIndex = 0
    var listType = "more"
    var param2: String? = null

    private val repository = MoreRepository()
    private var mainResponseLiveData = repository.getMainResponseLiveData()

    open fun requestMain() {
        repository.requestMain(listType, param2, page)
    }

    open fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}