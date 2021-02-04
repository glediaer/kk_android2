package com.krosskomics.common.model

import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataEvent
import com.krosskomics.common.data.DataGenre
import com.krosskomics.common.data.DataWaitFreeTerm
import java.util.*

class Event : Default() {
    var list_title: String? = null
    var list_type // NN 일반노페이징, NP 일반페이징, RN 랭킹노페이징,RP 랭킹페이징
            : String? = null
    var page_rows = 0
    var page = 0
    var tot_pages = 0
    var list: ArrayList<DataEvent>? = null
}