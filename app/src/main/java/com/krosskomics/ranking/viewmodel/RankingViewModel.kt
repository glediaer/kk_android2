package com.krosskomics.ranking.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.ranking.repository.RankingRepository


class RankingViewModel(application: Application): BaseViewModel(application) {
    private val repository = RankingRepository()
}