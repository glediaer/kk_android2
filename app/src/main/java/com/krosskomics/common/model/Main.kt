package com.krosskomics.common.model

import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataMainContents
import java.util.*

class Main {
    var msg: String? = null
    var retcode: String? = null
    var main_banner: ArrayList<DataBanner>? = null
    var layout_contents: ArrayList<DataMainContents>? = null
    var banner_rolling = 0
}