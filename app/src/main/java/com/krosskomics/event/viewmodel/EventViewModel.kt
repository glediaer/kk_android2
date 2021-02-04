package com.krosskomics.event.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.event.repository.EventRepository
import com.krosskomics.mynews.repository.MyNewsRepository

class EventViewModel(application: Application): BaseViewModel(application) {
    private val repository = EventRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(listType,page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}