package com.krosskomics.library.repository

import android.content.Context
import com.krosskomics.common.model.More
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LibraryRepository : CommonRepository() {
    var listType = "R" // R, U(unlock),
    var page = 1
    var sortType = "S"   // S

    fun requestMain(context: Context, page: Int) {
        val api: Call<More> = ServerUtil.service.getLibraryList(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            listType,
            page,
            sortType
        )
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
}