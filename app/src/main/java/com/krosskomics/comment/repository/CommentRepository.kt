package com.krosskomics.comment.repository

import android.content.Context
import com.krosskomics.common.model.Comment
import com.krosskomics.common.model.News
import com.krosskomics.common.repository.CommonRepository
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommentRepository : CommonRepository(){
    fun requestMain(type: String, sid: String, eid: String = "0", sort: String, page: Int,
        reportType: String?, reportContent: String?) {
        val api: Call<Comment> = ServerUtil.service.getCommentList(
            type, sid, eid, sort, page, reportType, reportContent
        )
        api.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.body() != null) {
                    mainLiveData.postValue(response.body());
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                mainLiveData.postValue(null)
            }
        })
    }
}