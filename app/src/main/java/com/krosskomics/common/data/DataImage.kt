package com.krosskomics.common.data

import java.util.*

class DataImage {
    var decPath: String? = null
    var ratio = 0.0f
    var indexFormat = 0

    companion object {
        var seq = Comparator<DataImage> { s1, s2 ->
            val rollno1 = s1.indexFormat
            val rollno2 = s2.indexFormat
            rollno1 - rollno2
        }
    }
}