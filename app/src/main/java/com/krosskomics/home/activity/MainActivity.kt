package com.krosskomics.home.activity

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.analytics.GoogleAnalytics
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.KJKomicsApp.Companion.MAIN_CONTENTS
import com.krosskomics.R
import com.krosskomics.coin.activity.CashHistoryActivity
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.coin.activity.TicketHistoryActivity
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.model.CheckData
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.common.view.SpanningLinearLayoutManager
import com.krosskomics.data.DataLanguage
import com.krosskomics.event.activity.EventActivity
import com.krosskomics.home.adapter.ChangeLanguageAdapter
import com.krosskomics.home.adapter.HomeAdapter
import com.krosskomics.home.adapter.MainBannerPagerAdapter
import com.krosskomics.home.viewmodel.MainViewModel
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.mainmenu.activity.*
import com.krosskomics.mynews.activity.MyNewsActivity
import com.krosskomics.notice.activity.NoticeActivity
import com.krosskomics.search.activity.SearchActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.settings.activity.SettingsActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.dpToPx
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil
import com.krosskomics.util.ServerUtil.service
import com.krosskomics.webview.WebViewActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.view_main_action_item.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_main_tab.view.*
import kotlinx.android.synthetic.main.view_network_error.view.*
import kotlinx.android.synthetic.main.view_toolbar.toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "MainActivity"

    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private var backKeyPressedTime: Long = 0
    val DEFALT_HEADER_HEIGHT = 667

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(application) as T
            }
        }).get(MainViewModel::class.java)
    }

    lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (bundle != null) {
            KJKomicsApp.ATYPE = bundle.getString("atype")
            KJKomicsApp.SID = bundle.getString("sid")
        }

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "token : " + read(this, CODE.LOCAL_token, ""))
        }
        if (KJKomicsApp.IS_CHANGE_LANGUAGE) {
            KJKomicsApp.IS_CHANGE_LANGUAGE = false
            requestServer()
        }
        if (KJKomicsApp.IS_GET_NEW_GIFT) {
            newGiftPointView.visibility = View.VISIBLE
        } else {
            newGiftPointView.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        GoogleAnalytics.getInstance(this).reportActivityStart(this)
    }

    override fun onStop() {
        super.onStop()
        GoogleAnalytics.getInstance(this).reportActivityStop(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initModel() {
        viewModel.getInitSetResponseLiveData().observe(this, this)
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initHeaderView()
        initMainView()
        //외부에서 통신받기
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter(CODE.LB_MAIN))
        setPushAction()
        setDeepLink()
        viewModel.requestInitSet()
    }

    override fun requestServer() {
        if (CommonUtil.getNetworkInfo(context) == null) {
            errorView.visibility = View.VISIBLE
            return
        }
        viewModel.requestMain()
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_home))
    }

    override fun initErrorView() {
        errorView.refreshButton.setOnClickListener {
            if (CommonUtil.getNetworkInfo(context) == null) {
                return@setOnClickListener
            }
            errorView.visibility = View.GONE
            requestServer()
        }
    }

    override fun onChanged(t: Any?) {
        if (t == null) {
            checkNetworkConnection(context, t, errorView)
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
            return
        }
        if (t is InitSet) {
            KJKomicsApp.INIT_SET = t
            KJKomicsApp.RUN_SEQ = KJKomicsApp.INIT_SET.run_seq
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "RUN_SEQ : " + KJKomicsApp.RUN_SEQ)
            }
            write(context, CODE.LOCAL_RECIEVE_PUSH, KJKomicsApp.INIT_SET.ispushnotify)
            if (!TextUtils.isEmpty(KJKomicsApp.DEEPLINK_RID)) return
            if (KJKomicsApp.INIT_SET.banner_list != null &&
                KJKomicsApp.INIT_SET.banner_list!!.size > 0
            ) {
                // 하루 체크
                // 오늘 날짜 데이터
                val handler = Handler()
                handler.postDelayed({
                    val intent = Intent(context, HomePopupActivity::class.java)
                    val curDate = Date()
                    val curMillis = curDate.time
                    var diffDay = 2
                    // 이전 저장 데이터
                    if ("0" != read(
                            context,
                            CODE.FLOATING_BANNER_CLOSE_TIME,
                            "-1"
                        )
                    ) {
                        val savedMillis = read(
                            context,
                            CODE.FLOATING_BANNER_CLOSE_TIME,
                            "-1"
                        )!!.toLong()
                        val diffSec = curMillis - savedMillis
                        diffDay = (diffSec / (60 * 60 * 24) / 1000).toInt()
                        if (savedMillis == -1L) {
                            startActivity(intent)
                        } else {
                            if (diffDay >= 1) {
                                startActivity(intent)
                            }
                        }
                    }
                }, 500)
            }
        } else if (t is Main) {
            if ("00" == t.retcode) {
                languageRecyclerView.visibility = View.GONE
                setMainBannerView(t.main_banner, t.banner_rolling)
                setMainContentView(t.layout_contents)
            } else if ("201" == t.retcode) {
                goLoginAlert(context)
            } else if ("908" == t.retcode) {
            } else {
                if ("" != t.msg) {
                    showToast(t.msg, this@MainActivity)
                }
            }
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        }
    }

    override fun onBackPressed() {
        if (dl_main_drawer_root.isDrawerOpen(GravityCompat.START)) {
            dl_main_drawer_root.closeDrawer(GravityCompat.START)
        } else {
            if (!this@MainActivity.isFinishing) {
                // 2000 milliseconds = 2 seconds
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis()
                    Toast.makeText(this, getString(R.string.msg_finish_app), Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    requestAppFinishApi()
                }
            }
        }
    }

    private fun initHeaderView() {
        initToolbar()
        initDrawerView()
        initBottomView()
        swipeLayout.setOnRefreshListener {
            requestServer()
        }
        changeLangImageView.setOnClickListener(this)
        giftboxImageView.setOnClickListener(this)
        searchImageView.setOnClickListener(this)
        logoImageView.setOnClickListener(this)
    }

    private fun initBottomView() {
        floatingEp.visibility = View.VISIBLE
        floatingEpTextView.text = "Epi.\n05"
        floatingLibrary.visibility = View.VISIBLE
        floatingEp.setOnClickListener {

        }
        floatingLibrary.setOnClickListener {
            val intent = Intent(context, LibraryActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())
        }
        bottomViewClose.setOnClickListener {
            bottomBannerView.visibility = View.GONE
        }
        topImageView.setOnClickListener {
            nestedScrollView?.scrollTo(0, 0)
        }
    }

    override fun initToolbar() {
        super.initToolbar()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.kk_ic_drawer)
    }

    private fun initMainView() {
        initTabView()
        initStickyTabView()
    }

    private fun initTabView() {
        onGoingButton.isSelected = false
        waitButton.isSelected = false
        rankingButton.isSelected = false
        genreButton.isSelected = false
        homeButton.setOnClickListener(this)
        onGoingButton.setOnClickListener(this)
        waitButton.setOnClickListener(this)
        rankingButton.setOnClickListener(this)
        genreButton.setOnClickListener(this)
    }

    private fun initStickyTabView() {
        mainStickyTabView.onGoingButton.isSelected = false
        mainStickyTabView.waitButton.isSelected = false
        mainStickyTabView.rankingButton.isSelected = false
        mainStickyTabView.genreButton.isSelected = false
        mainStickyTabView.onGoingButton.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    MainMenuActivity::class.java
                )
            )
        }
        mainStickyTabView.waitButton.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    MainMenuActivity::class.java
                )
            )
        }
        mainStickyTabView.rankingButton.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    MainMenuActivity::class.java
                )
            )
        }
        mainStickyTabView.genreButton.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    MainMenuActivity::class.java
                )
            )
        }
    }

    private fun setMainBannerView(items: ArrayList<DataBanner>?, bannerLolling: Int) {
        items?.let {
            bannerPager.adapter = MainBannerPagerAdapter(context, it, true)

            bannerPager.clipToPadding = false
            bannerPager.setPadding(
                dpToPx(context, 13 + 14),
                0,
                dpToPx(context, 13 + 14),
                0
            )
            bannerPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {}
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        swipeLayout.isEnabled = false
                    } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                        swipeLayout.isEnabled = true
                    }
                }
            })

            val currentItem: Int = it.size * 100
            bannerPager.setCurrentItem(currentItem, true)

            if (bannerLolling > 0) {
                bannerPager.interval = (bannerLolling * 1000).toLong()
                bannerPager.startAutoScroll()
            }
        }
    }

    private fun setMainContentView(items: ArrayList<DataMainContents>?) {
        items?.let {
            nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                if (scrollY >= dpToPx(context, DEFALT_HEADER_HEIGHT)) {
                    mainStickyTabView.visibility = View.VISIBLE
                } else {
                    mainStickyTabView.visibility = View.GONE
                }
            }
            MAIN_CONTENTS = items
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = HomeAdapter(items)
        }
    }

    private fun initLanguageRecyclerView() {
        var items = arrayListOf(
            DataLanguage("English", "en", true),
            DataLanguage("Hindi", "hi", false),
            DataLanguage("Talua", "ta", false)
        )

        languageRecyclerView?.apply {
            visibility = View.VISIBLE
            adapter = ChangeLanguageAdapter(items)
            layoutManager = SpanningLinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            (adapter as ChangeLanguageAdapter).setOnItemClickListener(object :
                ChangeLanguageAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    items.forEachIndexed { index, dataLanguage ->
                        if (position == index) {
                            dataLanguage.isSelect = true
                            write(context, CODE.CURRENT_LANGUAGE, dataLanguage.lang)
                            requestSetLanguage(dataLanguage.lang)
                            requestServer()
                        } else {
                            dataLanguage.isSelect = false
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            })
        }
    }

    private fun initDrawerView() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            dl_main_drawer_root,
            toolbar,
            R.string.common_open_on_phone,
            R.string.str_close
        )
        dl_main_drawer_root.addDrawerListener(drawerToggle)
        drawerToggle.setToolbarNavigationClickListener {
            Log.e(TAG, "setToolbarNavigationClickListener")
        }
        refreshNaviView()
    }

    private fun refreshNaviView() {
        nv_main_navigation_root.getHeaderView(0)?.apply {
            // header
            closeImageView.setOnClickListener {
                closeImageView.visibility = View.GONE
                closeLottieView.visibility = View.VISIBLE
                closeLottieView.playAnimation()
//                dl_main_drawer_root.closeDrawers()
            }
            alarmImageView.setOnClickListener {
                startActivity(Intent(context, MyNewsActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                headerView.isSelected = true
                alarmImageView.visibility = View.VISIBLE
                loginView.visibility = View.VISIBLE
                keysView.visibility = View.VISIBLE
                logoutView.visibility = View.GONE
                logoutTextView.visibility = View.VISIBLE

                if (TextUtils.isEmpty(KJKomicsApp.PROFILE_PICTURE)) {
                    when (read(context, CODE.LOCAL_loginType, "")) {
                        CODE.LOGIN_TYPE_FACEBOOK -> profileImageView.setImageResource(R.drawable.kk_icon_facebook)
                        CODE.LOGIN_TYPE_GOOGLE -> profileImageView.setImageResource(R.drawable.kk_icon_google)
                        else -> profileImageView.setImageResource(R.drawable.kk_logo_symbol)
                    }
                } else {
                    Glide.with(context)
                        .load(KJKomicsApp.PROFILE_PICTURE)
                        .into(profileImageView)
                }
                if (CODE.LOGIN_TYPE_KROSS == read(context, CODE.LOCAL_loginType, "")) {
                    nicknameTextView.text = read(context, CODE.LOCAL_email, "")
                } else {
                    nicknameTextView.text = read(context, CODE.LOCAL_Nickname, "Guest")
                }
                coinTextView.text = read(context, CODE.LOCAL_coin, "0")

                logoutTextView.text = getString(R.string.str_logout)
            } else {
                headerView.isSelected = false
                alarmImageView.visibility = View.GONE
                loginView.visibility = View.GONE
                keysView.visibility = View.GONE
                logoutView.visibility = View.VISIBLE
                logoutTextView.visibility = View.GONE

                logoutTextView.text = getString(R.string.str_login)
                logoutView.setOnClickListener {
                    goLoginAlert(context)
                    dl_main_drawer_root.closeDrawers()
                }
            }
            editImageView.setOnClickListener {
                dl_main_drawer_root.closeDrawers()
            }
            chargeTextView.setOnClickListener {
                startActivity(Intent(context, CoinActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            // content
            shopView.setOnClickListener {
                startActivity(Intent(context, CoinActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            libraryView.setOnClickListener {
                startActivity(Intent(context, LibraryActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            eventView.setOnClickListener {
                startActivity(Intent(context, EventActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            noticeView.setOnClickListener {
                startActivity(Intent(context, NoticeActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            settingsView.setOnClickListener {
                startActivity(Intent(context, SettingsActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            cashHistoryView.setOnClickListener {
                startActivity(Intent(context, CashHistoryActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }
            ticketHistoryView.setOnClickListener {
                startActivity(Intent(context, TicketHistoryActivity::class.java))
                dl_main_drawer_root.closeDrawers()
            }

            termsTextView.setOnClickListener {
                val intent =
                    Intent(context, WebViewActivity::class.java).apply {
                        putExtra("title", termsTextView.text.toString())
                        putExtra(
                            "url", KJKomicsApp.getWebUrl().toString() + "terms/terms"
                        )
                    }
                startActivity(intent)
            }
            logoutTextView.setOnClickListener {
                if (read(context, CODE.LOCAL_loginYn, "N") == "Y") {
                    requestLogOutApi()
                    CommonUtil.logout(context)
                    logoutTextView.text = getString(R.string.str_login)
                    refreshNaviView()
                } else {
                    goLoginAlert(context)
                }
                dl_main_drawer_root.closeDrawers()
            }
        }
    }

    private fun requestLogOutApi() {
        val api = ServerUtil.service.postLogout(
            CommonUtil.read(
                context,
                CODE.CURRENT_LANGUAGE,
                "en"
            ),
            "logout", KJKomicsApp.LOGIN_SEQ
        )
        api.enqueue(object : retrofit2.Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                try {
                    if (response.isSuccessful) {
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 로그인 후 결과 메인 처리 리시버
     */
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("message")
            if (message.equals(CODE.MSG_NAV_REFRESH, ignoreCase = true)) {
                refreshNaviView()
                requestServer()
            }
        }
    }

    private fun changeLanguageView() {
        initLanguageRecyclerView()
    }

    private fun setPushAction() {
        val handler = Handler()
        // push type 분류
        // 0:메인,1:딥링크
//        KJKomicsApp.ATYPE = "H";
//        KJKomicsApp.SID = "12321312";
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "KJKomicsApp.ATYPE : " + KJKomicsApp.ATYPE)
            Log.e(TAG, "KJKomicsApp.SID : " + KJKomicsApp.SID)
        }
        if (TextUtils.isEmpty(KJKomicsApp.ATYPE)) {
            return
        }
        when (KJKomicsApp.ATYPE) {
            "M" -> {
            }
            "H" -> handler.postDelayed({
                if (!TextUtils.isEmpty(KJKomicsApp.SID) &&
                    "0" != KJKomicsApp.SID
                ) {
                    requestCheckData(KJKomicsApp.SID)
                }
            }, 500)
        }
        KJKomicsApp.ATYPE = ""
    }

    private fun setDeepLink() {
        try {
//        KJKomicsApp.DEEPLINK_DATA = "https://krosskomics.com/series/954316/hi/";
//        KJKomicsApp.DEEPLINK_DATA = "https://krosskomics.com/series/585200/en?ref_source=web";
            if (TextUtils.isEmpty(KJKomicsApp.DEEPLINK_DATA) &&
                TextUtils.isEmpty(KJKomicsApp.DEEPLINK_CNO) &&
                TextUtils.isEmpty(KJKomicsApp.DEEPLINK_RID)
            ) {
                return
            }

            // 웹 인텐트 케이스
            if (TextUtils.isEmpty(KJKomicsApp.DEEPLINK_DATA)) {
                if (!TextUtils.isEmpty(KJKomicsApp.DEEPLINK_CNO)) {
                    requestCheckData(KJKomicsApp.DEEPLINK_CNO)
                } else if (!TextUtils.isEmpty(KJKomicsApp.DEEPLINK_RID)) {
                    moveSignUp(context)
                }
            } else {
                Log.e(TAG, "KJKomicsApp.DEEPLINK_DATA : " + KJKomicsApp.DEEPLINK_DATA)
                // 스키마 구분
                val splitData =
                    KJKomicsApp.DEEPLINK_DATA!!.split("//").toTypedArray()
                // 호스트 구분
                Log.e(TAG, "splitData.length : " + splitData.size)
                var splitTemp =
                    splitData[1].split("/").toTypedArray()
                val type = splitTemp[1]
                if ("series" == type) {
//            "https://krosskomics.com/series/914365/en/";
                    KJKomicsApp.DEEPLINK_CNO = splitTemp[2]
                    requestCheckData(KJKomicsApp.DEEPLINK_CNO)
                } else if ("signup" == type) {
//            "https://krosskomics.com/signup/?rid=2020202";
                    val rid = splitTemp[2]
                    splitTemp = rid.split("=").toTypedArray()
                    KJKomicsApp.DEEPLINK_RID = splitTemp[1]
                    moveSignUp(context)
                }
            }
            KJKomicsApp.DEEPLINK_DATA = ""
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun requestSetLanguage(newLanguage: String) {
        val api = service.setLanguage(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "change_language", newLanguage
        )
        api.enqueue(object : retrofit2.Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            var eventName = "af_switch_lang_$newLanguage"
                            val eventValue: MutableMap<String, Any?> = HashMap()
                            CommonUtil.setAppsFlyerEvent(context, eventName, eventValue)
                        } else if ("203" == item.retcode) {
                            goLoginAlert(context)
                        } else {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun requestAppFinishApi() {
        val api = ServerUtil.service.postFinishApp(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            KJKomicsApp.RUN_SEQ, if (KJKomicsApp.LOGIN_SEQ != 0L) KJKomicsApp.LOGIN_SEQ else 0
        )
        api.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                moveTaskToBack(true)
                finish()
                Process.killProcess(Process.myPid())
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                moveTaskToBack(true)
                finish()
                Process.killProcess(Process.myPid())
            }
        })
    }

    /**
     * 앱링크 작품 존재여부 체크 api
     * @param sid
     */
    private fun requestCheckData(sid: String?) {
        val api: Call<CheckData> = service.getCheckData(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "valid_series_lang", sid
        )
        api.enqueue(object : Callback<CheckData> {
            override fun onResponse(
                call: Call<CheckData>,
                response: Response<CheckData>
            ) {
                if (response.isSuccessful) {
                    val body: CheckData? = response.body()
                    if ("00" == body?.retcode) {
                        val handler = Handler()
                        handler.postDelayed({
                            val intent = Intent(context, SeriesActivity::class.java)
                            val b = Bundle()
                            b.putString("sid", sid)
                            intent.putExtras(b)
                            startActivity(intent)
                            if (!TextUtils.isEmpty(KJKomicsApp.DEEPLINK_CNO)) {
                                KJKomicsApp.DEEPLINK_CNO = ""
                            }
                            if (!TextUtils.isEmpty(KJKomicsApp.SID)) {
                                KJKomicsApp.SID = ""
                            }
                        }, 100)
                    } else {
                        if (!TextUtils.isEmpty(body?.msg)) {
                            showToast(body!!.msg, context)
                        }
                        if (!TextUtils.isEmpty(body?.popup_msg)) {
                            showCheckDataAlert(body?.popup_msg)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CheckData?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun showFinishAlert() {
        try {
            val innerView: View =
                layoutInflater.inflate(R.layout.dialog_default, null)
            val dialog = initDialog(innerView)
            val tvTitle = innerView.findViewById<TextView>(R.id.tv_title)
            val msgTextView = innerView.findViewById<TextView>(R.id.tv_msg)
            val btnConfirm =
                innerView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel =
                innerView.findViewById<Button>(R.id.btn_cancel)
            tvTitle.text = getString(R.string.app_name2)
            msgTextView.text = getString(R.string.str_finish_app_msg)
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnConfirm.setOnClickListener {
                dialog.dismiss()
                requestAppFinishApi()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun showCheckDataAlert(msg: String?) {
        try {
            val innerView =
                layoutInflater.inflate(R.layout.dialog_default, null)
            val dialog = initDialog(innerView)
            val tvTitle = innerView.findViewById<TextView>(R.id.tv_title)
            val msgTextView = innerView.findViewById<TextView>(R.id.tv_msg)
            val btnConfirm =
                innerView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel =
                innerView.findViewById<Button>(R.id.btn_cancel)
            tvTitle.text = msg
            msgTextView.visibility = View.GONE
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnConfirm.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(context, SettingsActivity::class.java))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.logoImageView -> requestServer()
            R.id.searchImageView -> startActivity(Intent(context, SearchActivity::class.java))
            R.id.changeLangImageView -> {
                changeLangImageView.isSelected = changeLangImageView.isSelected
                changeLanguageView()
            }
            R.id.giftboxImageView -> {
                if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                    val intent = Intent(context, LibraryActivity::class.java)
                    intent.putExtra("tabIndex", 1)
                    startActivity(intent)
                } else {
                    goLoginAlert(context)
                }
            }

            // tabview
            R.id.onGoingButton -> {
                val intent = Intent(context, MainMenuActivity::class.java).apply {
                    putExtra("tabIndex", 1)
                }
                startActivity(intent)
            }
            R.id.waitButton -> {
                val intent = Intent(context, MainMenuActivity::class.java).apply {
                    putExtra("tabIndex", 2)
                }
                startActivity(intent)
            }
            R.id.rankingButton -> {
                val intent = Intent(context, MainMenuActivity::class.java).apply {
                    putExtra("tabIndex", 3)
                }
                startActivity(intent)
            }
            R.id.genreButton -> {
                val intent = Intent(context, MainMenuActivity::class.java).apply {
                    putExtra("tabIndex", 4)
                }
                startActivity(intent)
            }
        }
    }
}