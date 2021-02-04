package com.krosskomics.mynews.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.mynews.repository.MyNewsRepository

class MyNewsViewModel(application: Application): BaseViewModel(application) {
    private val repository = MyNewsRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(listType,page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}