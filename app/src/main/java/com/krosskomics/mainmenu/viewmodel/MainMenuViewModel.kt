package com.krosskomics.mainmenu.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.data.DataWaitFreeTerm
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.mainmenu.repository.MainMenuRepository

class MainMenuViewModel(application: Application): BaseViewModel(application) {
    private val repository =
        MainMenuRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()

    var param1: String = ""

    lateinit var waitFreeTermItems: ArrayList<DataWaitFreeTerm>

    override fun requestMain() {
        when (tabIndex) {
            4 -> repository.requestGenre(param1, param2)
            else -> repository.requestMain(param1, param2)
        }
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }

//    override fun requestGenre() {
//        repository.requestMain(param1, param2)
//    }
//
//    override fun getMainResponseLiveData(): LiveData<Any> {
//        return mainResponseLiveData
//    }
}