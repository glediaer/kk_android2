package com.krosskomics.common.model

import com.krosskomics.common.data.DataCoin
import java.util.*

class Coin {
    var retcode: String? = null
    var msg: String? = null
    var product_list: ArrayList<DataCoin>? = null

    // 결제 완료 후
    var user_coin = 0
    var price: String? = null
    var currency: String? = null
    var first_order: String? = null
    var order_id = 0
}