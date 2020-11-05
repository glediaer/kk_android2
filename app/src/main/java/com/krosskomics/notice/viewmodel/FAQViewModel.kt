package com.krosskomics.notice.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.library.repository.LibraryRepository
import com.krosskomics.mynews.repository.MyNewsRepository
import com.krosskomics.notice.repository.FAQRepository
import com.krosskomics.notice.repository.NoticeRepository
import java.io.File
import java.util.ArrayList

class FAQViewModel(context: Context): FragmentBaseViewModel(context) {
    private val repository = FAQRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    override fun requestMain() {
        repository.requestMain(context, page)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}