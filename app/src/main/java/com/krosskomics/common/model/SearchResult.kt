package com.krosskomics.common.model

import com.krosskomics.common.data.DataBook
import java.util.*

class SearchResult {
    var msg: String? = null
    var retcode: String? = null
    var list_title: String? = null
    var list_type: String? = null
    var page_rows = 0
    var tot_pages = 0
    var page: String? = null
    var result_txt: String? = null
    var list: ArrayList<DataBook>? = null
}