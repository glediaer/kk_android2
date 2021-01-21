package com.krosskomics.coin.repository

import android.content.Context
import com.krosskomics.common.model.CashHistory
import com.krosskomics.common.model.More
import com.krosskomics.common.model.News
import com.krosskomics.common.model.Notice
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TicketHistoryRepository : CommonRepository(){
    fun requestMain(type: String?, page: Int) {
        val api: Call<CashHistory> = ServerUtil.service.getTicketHistory(
            type, page
        )
        api.enqueue(object : Callback<CashHistory> {
            override fun onResponse(call: Call<CashHistory>, response: Response<CashHistory>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<CashHistory>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}