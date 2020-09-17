package com.krosskomics.series.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.series.repository.SeriesRepository
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.ongoing.repository.OnGoingRepository

class SeriesViewModel(application: Application): BaseViewModel(application) {
    var sid = ""

    private val repository = SeriesRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(getApplication(), sid)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}