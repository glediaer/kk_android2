package com.krosskomics.common.model

import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataGenre
import com.krosskomics.common.data.DataWaitFreeTerm
import java.util.*

class More : Default() {
    var list_title: String? = null
    var list_type // NN 일반노페이징, NP 일반페이징, RN 랭킹노페이징,RP 랭킹페이징
            : String? = null
    var page_rows = 0
    var page = 0
    var tot_pages = 0
    var list: ArrayList<DataBook>? = null

    var today: String? = null
    var wop_term: ArrayList<DataWaitFreeTerm>? = null   //기다무 주기 구분 리스트
    var genre: ArrayList<DataGenre>? = null   //기다무 주기 구분 리스트
}