package com.krosskomics.coin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.coin.repository.CoinRepository
import com.krosskomics.common.viewmodel.BaseViewModel

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