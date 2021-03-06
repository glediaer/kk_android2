package com.krosskomics.login.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krosskomics.KJKomicsApp
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.Login
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginRepository(val context: Context) : CommonRepository(){
    private lateinit var loginLiveData: MutableLiveData<Login>
    private lateinit var findPasswordLiveData: MutableLiveData<Default>

    var pageType = CODE.LOGIN_MODE
    var id = ""
    var password = ""
    var loginType = ""
    var oprofile = ""
    var snsToken = ""
    var fbEmail = ""
    var fbName = ""


    var language = "en"
    var signOutInfoStep = 1

    fun requestLogin() {
        val api: Call<Login> = ServerUtil.service.setLoginKross(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            id,
            password,
            loginType,
            CommonUtil.read(context, CODE.LOCAL_token, ""),
            ""
        )
        api.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.body() != null) {
                    loginLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loginLiveData.postValue(null)
            }
        })
    }

    fun requestSNSLogin() {
        val api: Call<Login> = ServerUtil.service.setLoginSNS(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            loginType,
            CommonUtil.read(context, CODE.LOCAL_token, ""),
            snsToken
        )
        api.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.body() != null) {
                    loginLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loginLiveData.postValue(null)
            }
        })
    }

    fun requestSignUp() {
        val api: Call<Login> = ServerUtil.service.setJoinKross(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            id,
            password,
            loginType,
            KJKomicsApp.LOGIN_DATA?.gender,
            KJKomicsApp.LOGIN_DATA?.age,
            KJKomicsApp.LOGIN_DATA?.genreString,
            CommonUtil.read(context, CODE.LOCAL_token, ""),
            KJKomicsApp.DEEPLINK_RID,
            snsToken
        )
        api.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                KJKomicsApp.DEEPLINK_RID = ""
                if (response.body() != null) {
                    loginLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loginLiveData.postValue(null)
            }
        })
    }

    fun requestFindPassword() {
        val api: Call<Default> = ServerUtil.service.setAccountKross(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            id,
            "reset_passwd"
        )
        api.enqueue(object : Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                if (response.body() != null) {
                    findPasswordLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                loginLiveData.postValue(null)
            }
        })
    }

    fun getLoginResponseLiveData(): LiveData<Login> {
        loginLiveData = MutableLiveData()
        return loginLiveData
    }

    fun getFindPasswordResponseLiveData(): LiveData<Default> {
        findPasswordLiveData = MutableLiveData()
        return findPasswordLiveData
    }
}