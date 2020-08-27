package com.krosskomics.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krosskomics.home.repository.MainRepository

open class CommonViewModel: ViewModel() {
    val repository = MainRepository()

    private val _updateActivityCount = MutableLiveData<Int>()
    val updateActivityCount: LiveData<Int> get() = _updateActivityCount
    val updateActivityCount2: LiveData<Int> get() = _updateActivityCount

    fun insertCountTwo() {
        _updateActivityCount.value = (updateActivityCount.value ?: 0) + 2
    }
}