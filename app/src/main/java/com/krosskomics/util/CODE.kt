package com.krosskomics.util

object CODE {
    val APP_DOWNLOAD_URL = "https://play.google.com/store/apps/details?id=com.krosskomics"
    //    public static String WEB_URL = "http://106.247.229.91:8089/web/";
    var WEB_URL = "https://krosskomics.com/app/"

    //API 서버
//    var SERVER_URL = "http://106.247.229.91:8089/" // local
//    var SERVER_URL = "http://dev-eociem5902-app.krosskomics.com/" // dev
    var SERVER_URL = "https://app-1204.krosskomics.com/" // aws
    var ENCODE_KEY = "1234567890123456"

    //로그인 파라미터
    var LOGIN_TYPE_FACEBOOK = "fb"
    var LOGIN_TYPE_KROSS = "kk"
    var LOGIN_TYPE_GOOGLE = "gg"
    var LOGIN_DEVICE = "google"
    var CURRENT_LANGUAGE = "current_language"

    //LocalBroadcast
    var LB_MAIN = "LB_MAIN"
    var LB_JOIN = "LB_JOIN"
    var MSG_NAV_REFRESH = "MSG_NAV_REFRESH"
    var LB_CHARGE_COIN = "LB_CHARGE_COIN"
    var MSG_SUCCESS_FIXCHARGE = "msg_success_fixcharge"
    var LB_GIFTBOX = "LB_GIFTBOX"

    //내부 저장소 이름
    var LOCAL_token = "token"
    var LOCAL_id = "id" // id or open id
    var LOCAL_loginType = "loginType"
    var LOCAL_loginYn = "loginYn"
    var LOCAL_user_no = "user_no"
    var LOCAL_email = "email"
    var LOCAL_coin = "coin"
    var Local_oprofile = "oprofile"
    var LOCAL_login_token = "loginToken"
    var LOCAL_Nickname = "nickname"
    var LOCAL_ENC_USER_NO = "enc_user_no"
    var LOCAL_Android_Id = "android_id"
    var LOCAL_REF_SOURCE = "ref_source"
    var LOCAL_IS_VIEWER_TUTO = "false"

    // 푸시설정여부
    var LOCAL_RECIEVE_PUSH = "recieve_push"
    var LOCAL_RECIEVE_UPDATE_PUSH = "recieve_update_push"
    var LOCAL_APP_SECRET = "app_secret"

    // share
    var SHARE_EPISODE_URL = "share_episode_url"
    var MARKET_URL = "market://details?id=com.krosskomics"

    // floating banner 기간
    var FLOATING_BANNER_CLOSE_TIME = "floating_banner_close_time"

    // 앱 최초실행 체크
    var IS_RUN_FIRST_APP = "is_run_first_app"

    // 1:1 문의하기 웹주소
    var CSFORM = WEB_URL + "cs/csform_google.asp"
    var IS_SHOW_AUTUSCROLL = "IS_SHOW_AUTUSCROLL"
    var CONTRY_CODE = "contry_code"

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    val ivBytes = byteArrayOf(
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00
    )
    const val KEY_STRING = "z0p6repbu*2v!fd)5f57ifnl52u3!prc"
    var ENC_PASSWORD = "zhalzkeoqkr246802!@#"
    var base64EncodedPublicKey =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiLAWquyzbr4JW/hySLNGo4Rq8JMxnArTGCi9uv+xzJ5BIPB66rweZJWOYhj+Uc2F4f8otayoLyRsXngQAChxXPiIS+VieFGWq8EelquCIkfRC/z9z7lqit1KC58fNXC6YQHrZsgB1iV5vNYZQoJ1LkcegLrkzslZU1ztjzdIXheq2ApPqTz2Dqo06NFd/aBD2tT8rdvYqERwrFkD/qq9ApiN4uFckPdSls/Kv7ZFT5Jt0isXzUmqpKv2RZ6MU4F4cfGy+dvrJW7mZVIvwuEeCESjJFk48YzE+AxMSrlFjExEMnNvACjsMUPcJBw0b3BjvaPSYQ9x3/HtrAdUym8alQIDAQAB"

    // header
    var AUTH_KEY = "voqvj8dk1pg6ik50ng3m4fg51q1kqax1"
    var PAGING_FOOTER_SIZE = 7
    var INSTAGRAM_PACKAGE_NAME = "com.instagram.android"
    var WHATSAPP_PACKAGE_NAME = "com.whatsapp"
    var MEDIA_TYPE = "image/*"
    var REF_SOURCE = ""

    // api response
    val SUCCESS = "00"

    // login mode
    val LOGIN_MODE = "0"
    val SIGNUP_MODE = "1"

    val VISIBLE_LIST_TOPBUTTON_CNT = 3
}
