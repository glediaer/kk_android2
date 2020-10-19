package com.krosskomics.search.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krosskomics.KJKomicsApp
import com.krosskomics.common.model.*
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchRepository : CommonRepository() {

    fun requestMain(context: Context, page: Int, keyword: String) {
        val api: Call<Search> = ServerUtil.service.getSearchMain(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"), page,
            keyword
        )
        api.enqueue(object : Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Search>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}