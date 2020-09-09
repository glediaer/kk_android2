package com.krosskomics.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.More
import com.krosskomics.ongoing.repository.OnGoingRepository

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    var items = arrayListOf<Any>()
    var page = 1
    var totalPage = 1
    var isRefresh = false
    var tabIndex = 0

    private val repository = OnGoingRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    open fun requestMain() {
        repository.requestMain(getApplication(), page)
    }

    open fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}