package com.krosskomics.waitfree.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.waitfree.repository.WaitFreeRepository


class WaitFreeViewModel(application: Application): BaseViewModel(application) {
    private val repository = WaitFreeRepository()
}