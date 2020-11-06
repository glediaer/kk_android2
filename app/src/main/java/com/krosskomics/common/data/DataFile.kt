package com.krosskomics.common.data

class DataFile {
    var title: String? = null
    var showSeq: String? = null
    var ep_title: String? = null
    var fileSize: String? = null
    var realFileSize: Long = 0
    var filePath: String? = null
    var expireDate: String? = null
    var isExpire = false
    var isVerticalView: String? = null
    var revPager: String? = null
    var image: String? = null
    var show_str: String? = null
    var ep_show_date: String? = null
    var eid: String? = null

    // 삭제
    var isChecked = false
    var isCheckVisible = false
}