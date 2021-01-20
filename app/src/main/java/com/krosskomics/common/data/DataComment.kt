package com.krosskomics.common.data

import java.io.Serializable

class DataComment : Serializable {
//    "seq": 1,
//    "ep_seq": 1,
//    "nick": "tyum",
//    "comment": "😁 एक शर्मीली लड़की दिल दे बैठती है dsfsdfsdf 😍",
//    "like_cnt": 0,
//    "reg_date": "01.15.2021",
//    "isregister": "1"
//    “0” like 안했음, “1” like 했음
    var seq: String? = null
    var ep_seq: String? = null
    var nick: String? = null
    var comment: String? = null
    var like_cnt: String? = null
    var reg_date: String? = null
    var isregister: String? = null
    var islike: String? = "0"

    // 삭제
    var isSelect = false
}