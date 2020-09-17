package com.krosskomics.series.repository

import android.content.Context
import com.krosskomics.common.model.Episode
import com.krosskomics.common.model.More
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SeriesRepository : CommonRepository() {
    fun requestMain(context: Context, sid: String) {
        val api: Call<Episode> = ServerUtil.service.getEpisodeList(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            sid
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
}