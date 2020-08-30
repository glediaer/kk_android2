package com.krosskomics.genre.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krosskomics.KJKomicsApp
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GenreRepository : CommonRepository(){
    private lateinit var initSetLiveData: MutableLiveData<InitSet>
    private lateinit var mainLiveData: MutableLiveData<Main>

    fun requestInitSet(context: Context) {
        val api: Call<InitSet> = ServerUtil.service.getInitSet(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            CommonUtil.read(context, CODE.LOCAL_Android_Id, ""),
            CommonUtil.read(context, CODE.LOCAL_token, ""),
            CommonUtil.getAppVersion(context), KJKomicsApp.DEEPLINK_RID,
            CommonUtil.read(context, CODE.LOCAL_REF_SOURCE, "")
        )
        api.enqueue(object : Callback<InitSet> {
            override fun onResponse(call: Call<InitSet>, response: Response<InitSet>) {
                if (response.body() != null) {
                    initSetLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<InitSet>, t: Throwable) {
                initSetLiveData.postValue(null)
            }
        })
    }

    fun requestMain(context: Context) {
        val api: Call<Main> = ServerUtil.service.getMain(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en")
        )
        api.enqueue(object : Callback<Main> {
            override fun onResponse(call: Call<Main>, response: Response<Main>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Main>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }

    fun getVolumesResponseLiveData(): LiveData<InitSet> {
        initSetLiveData = MutableLiveData()
        return initSetLiveData
    }

    fun getMainResponseLiveData(): LiveData<Main> {
        mainLiveData = MutableLiveData()
        return mainLiveData
    }
}