package com.krosskomics.notice.repository

import android.content.Context
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


class NoticeRepository : CommonRepository(){
    fun requestMain(context: Context, page: Int) {
        val api: Call<Notice> = ServerUtil.service.getNoticeList(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "1",
            page
        )
        api.enqueue(object : Callback<Notice> {
            override fun onResponse(call: Call<Notice>, response: Response<Notice>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Notice>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}