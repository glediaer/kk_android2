package com.krosskomics.mynews.repository

import android.content.Context
import com.krosskomics.common.model.News
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyNewsRepository : CommonRepository(){
    fun requestMain(context: Context, page: Int) {
        val api: Call<News> = ServerUtil.service.getNewsList(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "1",
            page
        )
        api.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}