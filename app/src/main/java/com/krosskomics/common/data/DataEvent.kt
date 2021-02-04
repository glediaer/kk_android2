package com.krosskomics.common.data

import com.google.gson.annotations.SerializedName
import java.util.*

class DataEvent {
//    "seq": 42,
//    "atype": "B",
//    "sid": 0,
//    "event_title": "Creator Contest",
//    "link": "https://creators.krosskomics.com/",
//    "image": "https://cdn.krosskomics.com/i/b/e/1589786184770.jpg",
//    "start_date": "05.18.2020",
//    "end_date": "07.31.2020"
    var seq: String = "0"
    var atype: String? = null
    var sid: String? = "0"
    var event_title: String? = null
    var link: String? = null
    var image: String? = null
    var start_date: String? = null
    var end_date: String? = null
}