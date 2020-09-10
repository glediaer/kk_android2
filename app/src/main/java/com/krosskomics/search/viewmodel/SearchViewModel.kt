package com.krosskomics.search.viewmodel

import android.app.Application
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.search.repository.SearchRepository


class SearchViewModel(application: Application): BaseViewModel(application) {
    private val repository = SearchRepository()
}