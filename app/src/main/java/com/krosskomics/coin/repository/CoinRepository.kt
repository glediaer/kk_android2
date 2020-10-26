package com.krosskomics.coin.repository

import com.krosskomics.common.model.Coin
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CoinRepository : CommonRepository() {
    fun requestMain() {
        val api: Call<Coin> = ServerUtil.service.inappData
        api.enqueue(object : Callback<Coin> {
            override fun onResponse(call: Call<Coin>, response: Response<Coin>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Coin>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}