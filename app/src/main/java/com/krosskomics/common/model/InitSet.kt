package com.krosskomics.common.model

import com.krosskomics.common.data.DataAge
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataLoginGenre
import com.krosskomics.data.DataLanguage
import java.util.*

class InitSet {
    var retcode: String? = null
    var msg: String? = null
    var ispushnotify: String? = null
    var run_seq: Long = 0
    var loginGenre_img_list: ArrayList<DataLoginGenre>? = null
    var age_list: ArrayList<DataAge>? = null
    var banner_list: ArrayList<DataBanner>? = null
    var lang_list: ArrayList<DataLanguage>? = null
}