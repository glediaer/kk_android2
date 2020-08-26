package com.krosskomics.common.model

import com.krosskomics.common.data.DataBook
import java.util.*

class More {
    var msg: String? = null
    var retcode: String? = null
    var list_title: String? = null
    var list_type // NN 일반노페이징, NP 일반페이징, RN 랭킹노페이징,RP 랭킹페이징
            : String? = null
    var page_rows = 0
    var page = 0
    var tot_pages = 0
    var list: ArrayList<DataBook>? = null
}