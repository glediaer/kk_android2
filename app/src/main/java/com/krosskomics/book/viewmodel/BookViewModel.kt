package com.krosskomics.book.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.book.repository.BookRepository
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.repository.SeriesRepository

class BookViewModel(application: Application): BaseViewModel(application) {
    private val repository = BookRepository()
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