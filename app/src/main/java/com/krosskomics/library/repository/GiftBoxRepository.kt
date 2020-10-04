package com.krosskomics.library.repository

import android.content.Context
import com.krosskomics.common.model.Gift
import com.krosskomics.common.model.More
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GiftBoxRepository : CommonRepository() {
    fun requestMain(context: Context, page: Int) {
        val api: Call<Gift> = ServerUtil.service.getGiftBox(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "UG", page
        )
        api.enqueue(object : Callback<Gift> {
            override fun onResponse(call: Call<Gift>, response: Response<Gift>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Gift>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}