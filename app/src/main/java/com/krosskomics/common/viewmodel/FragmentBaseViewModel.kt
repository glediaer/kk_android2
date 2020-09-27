package com.krosskomics.common.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krosskomics.library.repository.LibraryRepository
import com.krosskomics.ongoing.repository.OnGoingRepository

open class FragmentBaseViewModel(val context: Context) : ViewModel() {
    var items = arrayListOf<Any>()
    var page = 1
    var totalPage = 1
    var isRefresh = false
    var tabIndex = 0

    private val repository = LibraryRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    open fun requestMain() {
        repository.requestMain(context, page)
    }

    open fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}