package com.krosskomics.restful

import com.krosskomics.common.model.*
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface InterfaceRestful {
    // 버전 체크
    @get:POST("app/google/24/version")
    val getVersion: Call<Version>

    // 앱 체크
    @FormUrlEncoded
    @POST("app/google/37/app")
    fun getCheckApp(@Field("deviceid") deviceid: String?): Call<AppToken>

    // 쿠키가져오기
    @FormUrlEncoded
    @POST("app/google/37/getdata.php")
    fun getCookieData(
        @Field("get_type") get_type: String?,
        @Field("pm") pm: String?
    ): Call<Cookie>

    // 크로스 코믹스 가입 회원
    @FormUrlEncoded
    @POST("app/google/43/login")
    fun setLoginKross(
        @Field("lang") lang: String?,
        @Field("email") email: String?,
        @Field("passwd") passwd: String?,
        @Field("login_type") login_type: String?,
        @Field("firebase_token") firebase_token: String?,
        @Field("sns_token") sns_token: String?
    ): Call<Login>

    @FormUrlEncoded
    @POST("app/google/43/login")
    fun setLoginSNS(
        @Field("lang") lang: String?,
        @Field("login_type") login_type: String?,
        @Field("firebase_token") firebase_token: String?,
        @Field("sns_token") sns_token: String?
    ): Call<Login>

    // 자동로그인
    @FormUrlEncoded
    @POST("app/google/41/login")
    fun setAutoLogin(
        @Field("lang") lang: String?,
        @Field("login_type") login_type: String?,
        @Field("firebase_token") firebase_token: String?
    ): Call<Login>

    // 로그인 중복 체크
    @FormUrlEncoded
    @POST("app/google/37/account")
    fun setAccountKross(
        @Field("lang") lang: String?,
        @Field("email") email: String?,
        @Field("atype") atype: String?
    ): Call<Default>

    // 계정삭제
    @FormUrlEncoded
    @POST("app/google/37/account")
    fun setDeleteUser(
        @Field("lang") lang: String?,
        @Field("atype") atype: String?
    ): Call<Default>

    // 크로스 코믹스 회원가입
    @FormUrlEncoded
    @POST("app/google/41/signup")
    fun setJoinKross(
        @Field("lang") lang: String?,
        @Field("email") email: String?,
        @Field("passwd") passwd: String?,
        @Field("login_type") login_type: String?,
        @Field("gender") gender: String?,
        @Field("age") age: Int?,
        @Field("genre") genre: String?,
        @Field("firebase_token") firebase_token: String?,
        @Field("rid") rid: String?,
        @Field("sns_token") sns_token: String?
    ): Call<Login>

    // 초기화 데이터
    @FormUrlEncoded
    @POST("app/google/41/sdata")
    fun getInitSet(
        @Field("lang") lang: String?,
        @Field("deviceid") deviceid: String?,
        @Field("firebase_token") firebase_token: String?,
        @Field("app_version") app_version: String?,
        @Field("rid") rid: String?,
        @Field("ref_source") ref_source: String?
    ): Call<InitSet>

    // kr main
    @FormUrlEncoded
    @POST("app/google/39/main")
    fun getMain(@Field("lang") lang: String?): Call<Main>

    // seriesmenu
    @FormUrlEncoded
    @POST("/app/google/50/seriesmunu.php")
    fun getSeriesMenu(@Field("m") m: String, @Field("p") p: String?): Call<More>

    @FormUrlEncoded
    @POST("/app/google/50/seriesmunu.php")
    fun getSeriesGenre(@Field("m") m: String, @Field("p") p: String?): Call<Genre>

    // more 리스트
    @FormUrlEncoded
    @POST("/app/google/50/serieslist.php")
    fun getMoreList(
        @Field("t") t: String?,
        @Field("p") p: String?,
        @Field("page") page: Int
    ): Call<More>

    // news 리스트
    @FormUrlEncoded
    @POST("app/google/37/list")
    fun getNewsList(
        @Field("lang") lang: String?,
        @Field("param") param: String?,
        @Field("page") page: Int
    ): Call<News>

    // comment 리스트, 신고
    @FormUrlEncoded
    @POST("app/google/50/comment.php")
    fun getCommentList(
        @Field("t") t: String,
        @Field("sid") sid: String,
        @Field("eid") eid: String,
        @Field("s") sort: String,
        @Field("page") page: Int,
        @Field("reportType") r: String?,
        @Field("reportContent") c: String?
    ): Call<Comment>

    @FormUrlEncoded
    @POST("app/google/37/list")
    fun getNoticeList(
        @Field("lang") lang: String?,
        @Field("param") param: String?,
        @Field("page") page: Int
    ): Call<Notice>

    // 시리즈 데이터 호출
    @FormUrlEncoded
    @POST("app/google/50/series.php")
    fun getSeriesData(
        @Field("sid") sid: String?
    ): Call<Episode>

    // ep 데이터 호출
    @FormUrlEncoded
    @POST("/app/google/50/episode.php")
    fun getEpList(
        @Field("sid") sid: String?,
        @Field("s") s: String?,
        @Field("page") page: Int
    ): Call<EpisodeMore>

    // 에피소드 소장체크
    @FormUrlEncoded
    @POST("/app/google/50/checkepisode.php")
    fun checkEpisode(
        @Field("eid") eid: String?
    ): Call<Episode>

    //에피소드 선택구매
    @FormUrlEncoded
    @POST("app/google/37/unlockepisode/bulk")
    fun setPurchaseSelectEpisode(
        @Field("lang") lang: String?,
        @Field("eids") eids: String?,
        @Field("unlock_type") unlock_typ: String?
    ): Call<PurchaseEpisode>

    // 뷰어 호출
    @FormUrlEncoded
    @POST("app/google/37/view")
    fun getEpisodeViewer(
        @Field("lang") lang: String?,
        @Field("eid") eid: String?,
        @Field("deviceid") deviceid: String?
    ): Call<Episode>

    // library list
    @FormUrlEncoded
    @POST("app/google/37/list/user")
    fun getLibraryList(
        @Field("lang") lang: String?,
        @Field("list_type") list_type: String?,
        @Field("page") page: Int,
        @Field("sort") sort: String?
    ): Call<More>

    // 소장목록
    @FormUrlEncoded
    @POST("app/google/37/list/user")
    fun getUnlockedEpList(
        @Field("lang") lang: String?,
        @Field("list_type") list_type: String?,
        @Field("page") page: Int,
        @Field("sid") sid: String?
    ): Call<UnLockedSeriesEp>

    // 푸시, 노티 등 셀렉터
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setNotiSelector(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("sid") sid: String?,
        @Field("action") action: String?,
        @Field("firebase_token") firebase_token: String?,
        @Field("deviceid") deviceid: String?
    ): Call<Default>

    // 유저 정보 설정
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setUserProfile(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("email") email: String?,
        @Field("preemail") preemail: String?
    ): Call<Default>

    // 패스워드 변경
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setPassword(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("passwd") passwd: String?,
        @Field("prepasswd") prepasswd: String?
    ): Call<Default>

    // 언어 변경
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setLanguage(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("ulang") ulang: String?
    ): Call<Default>

    // 좋아요
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setLike(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("sid") sid: String?
    ): Call<Default>

    // 작품 삭제
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setDeleteContents(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("sids") sids: String?
    ): Call<Default>

    // 에피소드 삭제
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setDeleteEpisodes(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("sid") sid: String?,
        @Field("eids") eids: String?
    ): Call<Default>

    // 쿠폰
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setPromotionCode(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("coupon") coupon: String?
    ): Call<Coin>

    // 뷰어 최종 인덱스
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setImageIndex(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("sid") sid: String?,
        @Field("eid") eid: String?,
        @Field("img_index") img_index: String?,
        @Field("ep_view_id") ep_view_id: String?
    ): Call<Default>

    // 로그아웃시 호출
    @FormUrlEncoded
    @POST("app/google/37/user")
    fun postLogout(
        @Field("lang") lang: String?,
        @Field("gtype") gtype: String?,
        @Field("login_seq") login_seq: Long
    ): Call<Default>

    // 앱 종료시 호출
    @FormUrlEncoded
    @POST("app/google/37/edata")
    fun postFinishApp(
        @Field("lang") lang: String?,
        @Field("run_seq") run_seq: Long,
        @Field("login_seq") login_seq: Long
    ): Call<Default>

    // 검색 초기 데이터
    @FormUrlEncoded
    @POST("app/google/37/search")
    fun getSearchMain(
        @Field("lang") lang: String?,
        @Field("page") page: Int,
        @Field("k") k: String?
    ): Call<Search>

    // 선물함
    @FormUrlEncoded
    @POST("app/google/37/list/user")
    fun getGiftBox(
        @Field("lang") lang: String?,
        @Field("list_type") list_type: String?,
        @Field("page") page: Int
    ): Call<Gift>

    // 선물 받기
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun setGetGift(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("seq") seq: String?
    ): Call<Gift>

    // 히스토리
    @FormUrlEncoded
    @POST("app/google/37/list/user")
    fun getHistory(
        @Field("lang") lang: String?,
        @Field("list_type") list_type: String?,
        @Field("page") page: Int
    ): Call<Gift>

    // 인앱 리스트
    @get:POST("app/google/37/product")
    val inappData: Call<Coin>

    // 인앱 결제 후 코인 지급
    @FormUrlEncoded
    @POST("app/google/37/purchase")
    fun getInappPurchase(
        @Field("productid") productid: String?,
        @Field("orderid") orderid: String?,
        @Field("purchasetime") purchasetime: Long,
        @Field("purchasestate") purchasestate: Int,
        @Field("purchasetoken") purchasetoken: String?
    ): Call<Coin>

    // 에피소드 유효성 체크
    @FormUrlEncoded
    @POST("app/google/37/checkdata")
    fun getCheckData(
        @Field("lang") lang: String?,
        @Field("check_type") check_type: String?,
        @Field("sid") sid: String?
    ): Call<CheckData>

    // 뷰어 호출
    @FormUrlEncoded
    @POST("app/google/37/download")
    fun getDownloadEpisode(
        @Field("lang") lang: String?,
        @Field("eid") eid: String?
    ): Call<Episode>

    // 친구초대
    @FormUrlEncoded
    @POST("app/google/37/user")
    fun getUser(
        @Field("lang") lang: String?,
        @Field("gtype") gtype: String?
    ): Call<User>

    // 뷰어 체류시간 체크
    @FormUrlEncoded
    @POST("app/google/37/set")
    fun setEpViewOut(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("ep_view_id") ep_view_id: String?,
        @Field("img_index") img_index: String?
    ): Call<Default>

    // 다운로드 완료 알림
    @FormUrlEncoded
    @POST("app/google/37/user/set")
    fun sendDownloadComplete(
        @Field("lang") lang: String?,
        @Field("set_type") set_type: String?,
        @Field("eid") eid: String?
    ): Call<Default>

    // 캐시 히스토리
    @FormUrlEncoded
    @POST("app/google/50/cash.php")
    fun getCashHistory(
        @Field("t") type: String?,
        @Field("page") page: Int
    ): Call<CashHistory>

    // 티 히스토리
    @FormUrlEncoded
    @POST("app/google/50/ticket.php")
    fun getTicketHistory(
        @Field("t") type: String?,
        @Field("page") page: Int
    ): Call<CashHistory>
}