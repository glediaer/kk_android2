package com.krosskomics.search.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.search.repository.SearchRepository
import com.krosskomics.wait.repository.WaitRepository


class SearchViewModel(application: Application): BaseViewModel(application) {
    private val repository = SearchRepository()
    private val initSetResponseLiveData = repository.getVolumesResponseLiveData()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    fun requestInitSet() {
        repository.requestInitSet(getApplication())
    }

    fun requestMain() {
        repository.requestMain(getApplication())
    }

    fun getInitSetResponseLiveData(): LiveData<InitSet> {
        return initSetResponseLiveData
    }

    fun getMainResponseLiveData(): LiveData<Main> {
        return mainResponseLiveData
    }
}