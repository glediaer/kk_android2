package com.krosskomics.common.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class CommonRepository {
    protected lateinit var mainLiveData: MutableLiveData<Any>

    fun getMainResponseLiveData(): LiveData<Any> {
        mainLiveData = MutableLiveData()
        return mainLiveData
    }
}