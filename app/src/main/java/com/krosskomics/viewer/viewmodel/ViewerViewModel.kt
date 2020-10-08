package com.krosskomics.viewer.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.viewer.repository.ViewerRepository
import java.util.*

class ViewerViewModel(application: Application): BaseViewModel(application) {
    var item: DataEpisode = DataEpisode()
    var isVerticalView = true // 화면 보기 방식(세로보기, 가로보기)
    var revPager = false
    var viewPosition = 0
    var arr_episode = ArrayList<DataEpisode>()
    var isFirstRequest = true
    var listBanner: ArrayList<DataBanner> = ArrayList()

    // purchase
    var allbuy_count = 0
    var allbuySaveRate = 0f
    var allbuy_coin = 0
    var allbuyRentCoin = 0
    var allbuy_possibility_count = 0
    var epList = ArrayList<String>()

    private val repository = ViewerRepository()
    private val mainResponseLiveData = repository.getMainResponseLiveData()
    private val checkResponseLiveData = repository.getCheckEpResponseLiveData()

    override fun requestMain() {
        requestType = REQUEST_TYPE.REQUEST_TYPE_A
        repository.requestMain(getApplication(), item.eid)
    }

    override fun getMainResponseLiveData(): LiveData<Any> {
        return mainResponseLiveData
    }

    fun requestCheckEp(eid: String?) {
        requestType = REQUEST_TYPE.REQUEST_TYPE_B
        repository.requestCheckEp(getApplication(), eid)
    }

    fun getCheckEpResponseLiveData(): LiveData<Any> {
        return checkResponseLiveData
    }
}