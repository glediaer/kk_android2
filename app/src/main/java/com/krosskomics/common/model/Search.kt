package com.krosskomics.common.model

import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataTag
import java.util.*

class Search {
    var msg: String? = null
    var retcode: String? = null
    var tag: DataTag? = null
//    var banner: ArrayList<DataRecentSearch>? = null

    // result
    var list_title: String? = null
    var list_type: String? = null
    var page_rows = 0
    var tot_pages = 0
    var page: String? = null
    var result_txt: String? = null
    var list: ArrayList<DataBook>? = null
}