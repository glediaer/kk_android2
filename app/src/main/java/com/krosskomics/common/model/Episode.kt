package com.krosskomics.common.model

import com.google.gson.annotations.SerializedName
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.data.DataSeries
import java.util.*

class Episode {
    @SerializedName("retcode")
    var retcode: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("series")
    var series: DataSeries? = null

    @SerializedName("list")
    var list: ArrayList<DataEpisode>? = null

    @SerializedName("episode")
    var episode: DataEpisode? = null
    var episode_list: ArrayList<DataEpisode>? = null
    var banner_list: ArrayList<DataBanner>? = null
}