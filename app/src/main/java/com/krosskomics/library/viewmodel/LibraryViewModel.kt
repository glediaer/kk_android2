package com.krosskomics.library.viewmodel

import android.app.Application
import android.content.Context
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.library.repository.LibraryRepository
import com.krosskomics.series.repository.SeriesRepository

class LibraryViewModel(context: Context): FragmentBaseViewModel(context) {
    private val repository = LibraryRepository()


}