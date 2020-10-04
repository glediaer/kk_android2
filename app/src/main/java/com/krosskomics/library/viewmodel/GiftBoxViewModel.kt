package com.krosskomics.library.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.library.repository.GiftBoxRepository

class GiftBoxViewModel(context: Context): FragmentBaseViewModel(context) {
    val repository = GiftBoxRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()


    override fun requestMain() {
        repository.requestMain(context, page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}