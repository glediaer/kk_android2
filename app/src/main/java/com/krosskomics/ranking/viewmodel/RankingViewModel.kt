package com.krosskomics.ranking.viewmodel

import android.app.Application
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.ranking.repository.RankingRepository


class RankingViewModel(application: Application): BaseViewModel(application) {
    private val repository = RankingRepository()
}