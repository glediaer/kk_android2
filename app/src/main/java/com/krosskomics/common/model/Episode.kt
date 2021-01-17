package com.krosskomics.common.model

import com.google.gson.annotations.SerializedName
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataEpList
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.data.DataSeries
import java.util.*

class Episode : Default() {
    var series: DataSeries? = null

//    var list: ArrayList<DataEpisode> = arrayListOf()
    var list: DataEpList? = null

    var episode: DataEpisode? = null
    var episode_list: ArrayList<DataEpisode>? = null
    var banner_list: ArrayList<DataBanner>? = null
}