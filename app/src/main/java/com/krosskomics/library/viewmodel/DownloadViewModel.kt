package com.krosskomics.library.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.data.DataSeries
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.repository.SeriesRepository
import java.io.File
import java.util.*
import javax.crypto.spec.SecretKeySpec

class DownloadViewModel(application: Application): BaseViewModel(application) {
    var mEpList = ArrayList<String>()

    var title = ""
    var mPath = ""
    var mThumbnail = ""
    var mSid = ""
    lateinit var mFile: File


    private val repository = SeriesRepository()
//    private val mainResponseLiveData = repository.getMainResponseLiveData()
    private val checkResponseLiveData = repository.getCheckEpResponseLiveData()
    private val imageUrlResponseLiveData = repository.getImageUrlResponseLiveData()

//    override fun requestMain() {
//        requestType = REQUEST_TYPE.REQUEST_TYPE_A
//        repository.requestMain(getApplication(), sid)
//    }
//
//    override fun getMainResponseLiveData(): LiveData<Any> {
//        return mainResponseLiveData
//    }
//
//    fun requestCheckEp() {
//        requestType = REQUEST_TYPE.REQUEST_TYPE_B
//        repository.requestCheckEp(getApplication(), selectEpItem.eid)
//    }
//
//    fun requestImageUrl() {
//        requestType = REQUEST_TYPE.REQUEST_TYPE_C
//        repository.requestImageUrl(getApplication(), downloadEpEid)
//    }
//
//    fun getCheckEpResponseLiveData(): LiveData<Any> {
//        return checkResponseLiveData
//    }
//
//    fun getImageUrlResponseLiveData(): LiveData<Any> {
//        return imageUrlResponseLiveData
//    }
}