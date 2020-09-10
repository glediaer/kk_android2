package com.krosskomics.genre.viewmodel

import android.app.Application
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.genre.repository.GenreRepository


class GenreViewModel(application: Application): BaseViewModel(application) {
    private val repository = GenreRepository()
}