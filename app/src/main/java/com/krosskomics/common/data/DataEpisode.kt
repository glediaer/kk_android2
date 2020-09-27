package com.krosskomics.common.data

import com.google.gson.annotations.SerializedName
import java.util.*

class DataEpisode {
    @SerializedName("eid")
    var eid: String = ""

    @SerializedName("ep_seq")
    var ep_seq = 0

    @SerializedName("ep_title")
    var ep_title: String = ""

    @SerializedName("ep_show_date")
    var ep_show_date: String = ""

    @SerializedName("isupdate")
    var isupdate: String? = null

    @SerializedName("ep_store_price")
    var ep_store_price = 0

    @SerializedName("ep_rent_price")
    var ep_rent_price = 0

    @SerializedName("image")
    var image: String? = null

    @SerializedName("isunlocked")
    var isunlocked: String? = null

    @SerializedName("str_type")
    var str_type // B: black, O: orange
            : String? = null

    @SerializedName("show_str")
    var show_str: String? = null

    @SerializedName("ep_view_type")
    var ep_view_type: String? = null

    @SerializedName("ep_rating")
    var ep_rating: String? = null

    @SerializedName("ep_rent_term")
    var ep_rent_term: String? = null
    var isdownload: String? = "0"
    var isChecked = false
    var isCheckVisible = false
    var possibility_allbuy = false

    // episode check
    @SerializedName("sid")
    var sid: String? = null

    @SerializedName("allow_store")
    var allow_store: String? = null

    @SerializedName("allow_rent")
    var allow_rent: String? = null

    @SerializedName("except_ep_seq")
    var except_ep_seq = 0

    @SerializedName("sticket")
    var sticket = 0

    @SerializedName("rticket")
    var rticket = 0

    @SerializedName("able_store")
    var able_store: String? = null

    @SerializedName("able_rent")
    var able_rent: String? = null

    @SerializedName("rent_text")
    var rent_text: String? = null

    @SerializedName("store_text")
    var store_text: String? = null

    @SerializedName("coin")
    var coin: String? = null
    var download_progress = 0
    var download_max = 0

    // viewer
    @SerializedName("ep_contents_domain")
    var ep_contents_domain: String? = null

    @SerializedName("ep_contents_path")
    var ep_contents_path: String? = null

    @SerializedName("ep_contents")
    var ep_contents: String = ""

    @SerializedName("vviewer")
    var vviewer: String? = null

    @SerializedName("hviewer")
    var hviewer: String? = null

    @SerializedName("allow_comment")
    var allow_comment: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("pre_eid")
    var pre_eid: String? = null

    @SerializedName("next_eid")
    var next_eid: String? = null

    @SerializedName("able_like")
    var able_like: String? = null

    @SerializedName("comment_url")
    var comment_url: String? = null
    var read_ep_img_index: String? = null
    var share_url: String? = null
    var fb_share_url: String? = null
    var share_image: String? = null
    var ep_view_id: String? = null
    var download_expire: String = ""

    companion object {
        var seq = Comparator<DataEpisode> { s1, s2 ->
            val rollno1 = s1.ep_seq
            val rollno2 = s2.ep_seq
            rollno1 - rollno2
        }
    }
}