package com.krosskomics.series.viewmodel

import android.app.Application
import com.krosskomics.series.repository.SeriesRepository
import com.krosskomics.common.viewmodel.BaseViewModel

class SeriesViewModel(application: Application): BaseViewModel(application) {
    private val repository = SeriesRepository()
}