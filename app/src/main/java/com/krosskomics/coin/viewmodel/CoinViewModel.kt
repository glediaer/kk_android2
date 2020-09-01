package com.krosskomics.coin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.coin.repository.CoinRepository
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel

class CoinViewModel(application: Application): BaseViewModel(application) {
    private val repository = CoinRepository()
}