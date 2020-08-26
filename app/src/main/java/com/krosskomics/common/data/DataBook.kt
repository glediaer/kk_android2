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

    // 삭제
    var isChecked = false
    var isCheckVisible = false
}