package com.krosskomics.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.krosskomics.more.repository.MoreRepository

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    open var items = arrayListOf<Any>()
    var page = 1
    var totalPage = 1
    var isRefresh = false
    var isReload = false
    var tabIndex = 0
    var requestType = REQUEST_TYPE.REQUEST_TYPE_A
    open var listType = "more"
    var param2: String? = null

    enum class REQUEST_TYPE {
        REQUEST_TYPE_A, REQUEST_TYPE_B, REQUEST_TYPE_C, REQUEST_TYPE_D
    }

    private val repository = MoreRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    open fun requestMain() {
        repository.requestMain(listType, param2, page)
    }

    open fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}