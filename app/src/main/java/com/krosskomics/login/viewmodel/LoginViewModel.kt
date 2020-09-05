package com.krosskomics.login.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Login
import com.krosskomics.common.model.Main
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.home.repository.MainRepository
import com.krosskomics.login.repository.LoginRepository


class LoginViewModel(application: Application): BaseViewModel(application) {
    val repository = LoginRepository(application)
    private val loginResponseLiveData = repository.getLoginResponseLiveData()
    private val findPasswordResponseLiveData = repository.getFindPasswordResponseLiveData()

    fun requestLogin() {
        repository.requestLogin()
    }

    fun requestSNSLogin() {
        repository.requestSNSLogin()
    }

    fun requestSignUp() {
        repository.requestSignUp()
    }

    fun requestFindPassword() {
        repository.requestFindPassword()
    }

    fun getLoginResponseLiveData(): LiveData<Login> {
        return loginResponseLiveData
    }

    fun getFindPasswordResponseLiveData(): LiveData<Default> {
        return findPasswordResponseLiveData
    }
}