package com.krosskomics.viewer.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Episode
import com.krosskomics.series.repository.SeriesRepository
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.ongoing.repository.OnGoingRepository
import com.krosskomics.viewer.repository.ViewerRepository

class ViewerViewModel(application: Application): BaseViewModel(application) {
    var item: DataEpisode = DataEpisode()

    private val repository = ViewerRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()
    private val checkResponseLiveData = repository.getCheckEpResponseLiveData()

    override fun requestMain() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_A
        repository.requestMain(getApplication(), item.eid)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }

    fun requestCheckEp() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_B
        repository.requestCheckEp(getApplication(), item.eid)
    }

    fun getCheckEpResponseLiveData(): LiveData<Any> {
        return checkResponseLiveData
    }
}