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
    fun requestMain(context: Context, page: Int) {
        val api: Call<Comment> = ServerUtil.service.getCommentList(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "1",
            page
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