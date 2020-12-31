package com.krosskomics.common.data

import java.io.Serializable

class DataBook : Serializable {
    // kross
    var sid: String? = null
    var title: String? = null
    var link: String? = null
    var image: String? = null
    var like_cnt: String? = null
    var sub_cnt: String? = null
    var isnew: String? = null
    var isupdate: String? = null
    var bseq: String? = null
    var atype: String? = null
    var filePath: String? = null
    var writer1: String? = null
    var writer2: String? = null
    var writer3: String? = null
    var genre1: String? = null
    var genre2: String? = null
    var genre3: String? = null
    var rank: String? = null
    var ispush: String? = null
    var subject: String? = null

    var dp_pub_day: String? = null // "WED",			연재 요일 표시	공백이면 연재아님
    var iswop: String? = null   //  "iswop": "1",			기다무 여부
    var dp_wop_term: String? = null //"dp_wop_term": "12hours"			기다무 주기 표시
    var wop_term: String? = null //"wop_term": 12,			기다무 주기

    // 삭제
    var isChecked = false
    var isCheckVisible = false
}