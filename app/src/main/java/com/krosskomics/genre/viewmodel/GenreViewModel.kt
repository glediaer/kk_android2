package com.krosskomics.genre.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.genre.repository.GenreRepository

class GenreViewModel(context: Context): FragmentBaseViewModel(context) {
    private val repository = GenreRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(context, page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}