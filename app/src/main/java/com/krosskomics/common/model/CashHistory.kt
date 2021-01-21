package com.krosskomics.common.model

import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataCashHistory
import com.krosskomics.common.data.DataGenre
import com.krosskomics.common.data.DataWaitFreeTerm
import java.util.*

class CashHistory : Default() {
//    "page_rows": 50,
//    "page": "1",
//    "tot_pages": 1,
//    "cash": 391,
//    "bonus_cash": "300",
    var page_rows = 1
    var page = 1
    var tot_pages = 1
    var cash: String? = null
    var bonus_cash: String? = null
    var list: ArrayList<DataCashHistory>? = null
}