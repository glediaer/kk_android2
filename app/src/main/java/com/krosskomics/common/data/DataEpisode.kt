package com.krosskomics.common.data

import com.google.gson.annotations.SerializedName
import java.util.*

class DataEpisode {
    var eid: String = ""
    var ep_seq = 0
    var ep_title: String = ""
    var ep_show_date: String = ""
    var isupdate: String? = null
    var ep_store_price = 0
    var ep_rent_price = 0
    var image: String? = null
    var isunlocked: String? = null  //unlock 여부 (구매시 0 인것만 구매가능)
    var str_type // B: black, O: orange
            : String? = null
    var show_str: String? = null
    var ep_view_type: String? = null
    var ep_rating: String? = null
    var ep_rent_term: String? = null
    var isdownload: String? = "0"
    var isChecked = false
    var isCheckVisible = false
    var possibility_allbuy = false

    // episode check
    var sid: String? = null
    var allow_store: String? = null
    var allow_rent: String? = null
    var except_ep_seq = 0
    var sticket = 0
    var rticket = 0
    var able_store: String? = null
    var able_rent: String? = null
    var rent_text: String? = null
    var store_text: String? = null
    var coin: String? = null
    var download_progress = 0
    var download_max = 0

    // check ep
    var isexcept_ep = "0" //"0",				기다무 / 티켓 사용 제외 여부(0:제외아님(사용가능), 1:제외(사용불가))
    var dp_except_ep: String? = null //"Latest 10 episodes do not apply for Wait or Pay.",				기다무 / 티켓 사용불가시 표시
    var iswop = "0" //"1",				기다무 여부 (0: 기다무 아님, 1:기다무)
    var reset_wop: String? = null //"11hours",				기다무 리셋 표시
    var reset_wop_ratio = 0 //99,				기다무 리셋 남은기간 백분율
    var user_cash = "0" //0,				cash
    var user_bonus_cash = "0" //"0"				bonus cash

//    "isexcept_ep": "0",				기다무 / 티켓 사용 제외 여부(0:제외아님(사용가능), 1:제외(사용불가))
//    "dp_except_ep": "Latest 10 episodes do not apply for Wait or Pay.",				기다무 / 티켓 사용불가시 표시
//    "iswop": "1",				기다무 여부 (0: 기다무 아님, 1:기다무)
//    "reset_wop": "11hours",				기다무 리셋 표시
//    "reset_wop_ratio": 99,				기다무 리셋 남은기간 백분율
//    "user_cash": 0,				cash
//    "user_bonus_cash": "0"				bonus cash

    // viewer
    var ep_contents_domain: String? = null
    var ep_contents_path: String? = null
    var ep_contents: String = ""
    var vviewer: String? = null
    var hviewer: String? = null
    var allow_comment: String? = null
    var title: String? = null
    var pre_eid: String? = null
    var next_eid: String? = null
    var able_like: String? = null
    var comment_url: String? = null
    var read_ep_img_index: String? = null
    var share_url: String? = null
    var fb_share_url: String? = null
    var share_image: String? = null
    var ep_view_id: String? = null
    var download_expire: String = ""
    var isEpSelect = false

    var isread: String? = null  //    "isread": "0",										에피소드 뷰 여부 (0:안봄, 1:봄)
    var ep_type: String? = null  //    "F",										에피소드 구분 (F: 무료, W:기다리면 무료, C:유료)
    var dp_list_txt: String? = null  //   "December 01, 2019",										리스트 형일때 표시
    var dp_tile_txt: String? = null  //   "12.01.2019"										타일 형일때 표시

    companion object {
        var seq = Comparator<DataEpisode> { s1, s2 ->
            val rollno1 = s1.ep_seq
            val rollno2 = s2.ep_seq
            rollno1 - rollno2
        }
    }
}