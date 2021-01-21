package com.krosskomics.coin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.coin.repository.CoinRepository
import com.krosskomics.coin.repository.TicketHistoryRepository
import com.krosskomics.common.viewmodel.BaseViewModel

class TicketHistoryViewModel(application: Application): BaseViewModel(application) {

    private val repository = TicketHistoryRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(listType, page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}