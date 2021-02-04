package com.krosskomics.event.repository

import android.content.Context
import com.krosskomics.common.model.Event
import com.krosskomics.common.model.More
import com.krosskomics.common.model.News
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventRepository : CommonRepository(){
    fun requestMain(type: String, page: Int) {
        val api: Call<Event> = ServerUtil.service.getEventApi(
            type,
            page
        )
        api.enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}