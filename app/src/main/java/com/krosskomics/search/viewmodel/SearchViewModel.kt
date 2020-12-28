package com.krosskomics.search.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.search.repository.SearchRepository
import java.util.*


class SearchViewModel(application: Application): BaseViewModel(application) {
    private val repository = SearchRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    var tagItems: ArrayList<String> = arrayListOf()
    var recentItems: ArrayList<String> = arrayListOf()

    var keyword = ""

    override fun requestMain() {
        repository.requestMain(getApplication(), page, keyword)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}