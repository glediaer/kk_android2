package com.krosskomics.series.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.data.DataSeries
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.repository.SeriesRepository
import java.util.*
import javax.crypto.spec.SecretKeySpec

class SeriesViewModel(application: Application): BaseViewModel(application) {
    var sid = ""
    var item: DataEpisode = DataEpisode()
    var seriesItem = DataSeries()
    var nextEp = ""
    var selectBuyPosibilityCount = 0
    var selectedEpList: MutableList<String>? = null
    var listViewType = 0    // 0: gridtype, 1: listtype

    // download
    var isSelectDownload = false
    var downloadEpEid = "0"
    var downloadEpShowdate = "0"
    var downloadEpTitle = ""
    var selectedDownloadIndex = 0
    var downloadExpire = ""
    var arr_url: Array<String> = emptyArray()
    var isCompleteDownload = false
    var arr_pics = ArrayList<String>()
    var arr_episode = ArrayList<DataEpisode>()
    var downloadPath: String? = null
    var isDownloadException = false
    var secretKeySpec: SecretKeySpec? = null
    var isVerticalView = false // 화면 보기 방식(세로보기, 가로보기)
    var revPager = false

    private val repository = SeriesRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()
    private val checkResponseLiveData = repository.getCheckEpResponseLiveData()
    private val imageUrlResponseLiveData = repository.getImageUrlResponseLiveData()

    override fun requestMain() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_A
        repository.requestMain(getApplication(), sid)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }

    fun requestCheckEp() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_B
        repository.requestCheckEp(getApplication(), item.eid)
    }

    fun requestImageUrl() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_C
        repository.requestImageUrl(getApplication(), downloadEpEid)
    }

    fun getCheckEpResponseLiveData(): LiveData<Any> {
        return checkResponseLiveData
    }

    fun getImageUrlResponseLiveData(): LiveData<Any> {
        return imageUrlResponseLiveData
    }
}