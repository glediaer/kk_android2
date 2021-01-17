package com.krosskomics.comment.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.comment.repository.CommentRepository
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.mynews.repository.MyNewsRepository

class CommentViewModel(application: Application): BaseViewModel(application) {
    private val repository = CommentRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()
    var type = "list"
    var sid = "0"
    var eid = "0"
    var sort = "t"   //t:top, r:recent
    var comment = ""   //t:top, r:recent
    var seq = "0"   //t:top, r:recent

    var reportType = ""
    var reportContent = ""

    override fun requestMain() {
        repository.requestMain(type, sid, eid, sort, page, reportType, reportContent)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }
}