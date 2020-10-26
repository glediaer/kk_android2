package com.krosskomics.library.viewmodel

import android.app.Application
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.repository.SeriesRepository
import java.io.File
import java.util.*

class DownloadViewModel(application: Application): BaseViewModel(application) {
    var mEpList = ArrayList<String>()

    var title = ""
    var mPath = ""
    var mThumbnail = ""
    var mSid = ""

    var revPager = false
    var isVerticalView = true
    lateinit var mFile: File


    private val repository = SeriesRepository()
}