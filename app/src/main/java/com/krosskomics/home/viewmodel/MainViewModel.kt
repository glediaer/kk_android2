package com.krosskomics.home.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.home.repository.MainRepository


class MainViewModel(application: Application): BaseViewModel(application) {
    private val repository = MainRepository()
    private val initSetResponseLiveData = repository.getVolumesResponseLiveData()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    fun requestInitSet() {
        repository.requestInitSet(getApplication())
    }

    override fun requestMain() {
        repository.requestMain(getApplication())
    }

    fun getInitSetResponseLiveData(): LiveData<InitSet> {
        return initSetResponseLiveData
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}