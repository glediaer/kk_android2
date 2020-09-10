package com.krosskomics.waitfree.viewmodel

import android.app.Application
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.waitfree.repository.WaitFreeRepository


class WaitFreeViewModel(application: Application): BaseViewModel(application) {
    private val repository = WaitFreeRepository()
}