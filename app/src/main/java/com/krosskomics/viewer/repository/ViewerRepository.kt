package com.krosskomics.viewer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krosskomics.common.model.Episode
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewerRepository : CommonRepository() {
    lateinit var checkEpLiveData: MutableLiveData<Any>

    fun getCheckEpResponseLiveData(): LiveData<Any> {
        checkEpLiveData = MutableLiveData()
        return checkEpLiveData
    }

    fun requestMain(context: Context, eid: String?) {
        val api: Call<Episode> = ServerUtil.service.getEpisodeViewer(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            eid,
            read(context, CODE.LOCAL_Android_Id, "")
        )
        api.enqueue(object : Callback<Episode> {
            override fun onResponse(call: Call<Episode>, response: Response<Episode>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Episode>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }

    fun requestCheckEp(eid: String?) {
        val api: Call<Episode> = ServerUtil.service.checkEpisode(eid)
        api.enqueue(object : Callback<Episode> {
            override fun onResponse(call: Call<Episode>, response: Response<Episode>) {
                if (response.body() != null) {
                    checkEpLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Episode>, t: Throwable) {
                checkEpLiveData.postValue(null)
            }
        })
    }
}