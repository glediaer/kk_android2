package com.krosskomics.common.data

import java.util.ArrayList

class DataEpList {
    var retcode: String? = null
    var page_rows = 0
    var page = 0
    var tot_pages = 0
    var list: ArrayList<DataEpisode> = arrayListOf()
}