package com.krosskomics.common.data

class DataSeries {
    var sid = 0
    var msg: String? = null
    var retcode: String? = null
    var title: String? = null
    var long_desc: String? = null
    var show_datetime: String? = null
    var rating: String? = null
    var genre1: String? = null
    var genre2: String? = null
    var genre3: String? = null
    var except_ep_seq = 0
    var image: String? = null
    var image1: String? = null
    var writer1: String? = null
    var writer2: String? = null
    var writer3: String? = null
    var like_cnt: String? = null
    var sticket = 0
    var rticket = 0     //대여티켓 갯수
    var ispush: String? = null
    var issubscribed: String? = null
    var first_ep: String? = null
    var read_next_ep: String? = null
    var read_next_ep_seq: String? = null
    var read_next_ep_title: String? = null
    var read_ep = 0
    var read_ep_seq = 0
    var allow_store: String? = null
    var allow_rent: String? = null
    var allow_comment: String? = null  // "allow_comment": "1",
    var rent_term: String? = null
    var vviewer: String? = null
    var hviewer: String? = null

    var dp_pub_day: String? = null //"dp_pub_day": "Every Wednesday", 연재일경우 표시 텍스트	공백이면 연재 아님

    var comment_cnt: String? = null //0,										댓글 갯수
    var iswop: String? = null //"1",										기다무 여부 (0:기다무 아님, 1:기다무)
    var dp_wop_term: String? = null //"12hours",										기다무 주기 표기 텍스트
    var dp_free_txt: String? = null //"First 20 episodes for Free.",				free 표시 텍스트
    var dp_waitorpay_txt: String? = null //"Next 37 episodes for Wait or Pay.",		기다무 표시 텍스트
    var able_wop: String? = null //"0",										기다무 사용 가능 (0:불가, 1:가능)
    var dp_reset_wop: String? = null //"11hours",										기다무 리셋 기간 표시
    var reset_wop_ratio: Int = 0 //95,										기다무 리셋 남은기간 백분율
    var dp_wop_desc: String? = null //If you wait for this com
    var series_notice: ArrayList<String>? = null //[]										시리즈 공지 리스트
//    "comment_cnt": 0,										댓글 갯수
//    "iswop": "1",										기다무 여부 (0:기다무 아님, 1:기다무)
//    "dp_wop_term": "12hours",										기다무 주기 표기 텍스트
//    "dp_free_txt": "First 20 episodes for Free.",										free 표시 텍스트
//    "dp_waitorpay_txt": "Next 37 episodes for Wait or Pay.",										기다무 표시 텍스트
//    "able_wop": "0",										기다무 사용 가능 (0:불가, 1:가능)
//    "dp_reset_wop": "11hours",										기다무 리셋 기간 표시
//    "reset_wop_ratio": 95,										기다무 리셋 남은기간 백분율
//    "dp_wop_desc": "- If you wait for this comic’s period of \ntime, one free ticket will be generated \nagain.\n\n- This free ticket could be used one \nepisode and expired after 3 days.",
//    "series_notice": []										시리즈 공지 리스트
    var isVideo = true
}