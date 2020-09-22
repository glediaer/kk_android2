package com.krosskomics.coin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.coin.repository.CoinRepository
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Episode
import com.krosskomics.series.repository.SeriesRepository
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.ongoing.repository.OnGoingRepository
import com.krosskomics.viewer.repository.ViewerRepository

class CoinViewModel(application: Application): BaseViewModel(application) {

    private val repository = CoinRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_A
        repository.requestMain()
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}