package com.krosskomics.common.model

import com.google.gson.annotations.SerializedName
import com.krosskomics.common.data.*
import java.util.*

class EpisodeMore {
    var retcode: String? = null
    var msg: String? = null

    var page_rows = 1
    var page = 1
    var tot_pages = 1

    var list: ArrayList<DataEpisode> = arrayListOf()
}