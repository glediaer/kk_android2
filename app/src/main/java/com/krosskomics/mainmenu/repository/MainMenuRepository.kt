package com.krosskomics.mainmenu.repository

import com.krosskomics.common.model.Genre
import com.krosskomics.common.model.More
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainMenuRepository : CommonRepository(){
    fun requestMain(menu: String, p: String?) {
        val api: Call<More> = ServerUtil.service.getSeriesMenu(menu, p)
        api.enqueue(object : Callback<More> {
            override fun onResponse(call: Call<More>, response: Response<More>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<More>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }

    fun requestGenre(menu: String, p: String?) {
        val api: Call<Genre> = ServerUtil.service.getSeriesGenre(menu, p)
        api.enqueue(object : Callback<Genre> {
            override fun onResponse(call: Call<Genre>, response: Response<Genre>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Genre>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}