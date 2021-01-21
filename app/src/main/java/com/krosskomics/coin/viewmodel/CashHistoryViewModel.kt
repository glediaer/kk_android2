package com.krosskomics.coin.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.krosskomics.coin.repository.CashHistoryRepository
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.library.repository.LibraryRepository
import com.krosskomics.mynews.repository.MyNewsRepository
import com.krosskomics.notice.repository.NoticeRepository
import java.io.File
import java.util.ArrayList

class CashHistoryViewModel(context: Context): FragmentBaseViewModel(context) {
    private val repository = CashHistoryRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(listType, page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}