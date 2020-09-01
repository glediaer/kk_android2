package com.krosskomics.genre.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.genre.repository.GenreRepository
import com.krosskomics.home.repository.*


class GenreViewModel(application: Application): BaseViewModel(application) {
    private val repository = GenreRepository()
}