package com.krosskomics.home.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.material.navigation.NavigationView
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.data.DataLanguage
import com.krosskomics.event.activity.EventActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.home.adapter.ChangeLanguageAdapter
import com.krosskomics.home.adapter.HomeAdapter
import com.krosskomics.home.adapter.HomeBannerAdapter
import com.krosskomics.home.viewmodel.MainViewModel
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.notice.activity.NoticeActivity
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.search.activity.SearchActivity
import com.krosskomics.ongoing.activity.OnGoingActivity
import com.krosskomics.settings.activity.SettingsActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CODE.LOCAL_Nickname
import com.krosskomics.util.CODE.LOCAL_coin
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.view_main_action_item.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_toolbar.toolbar
import java.util.ArrayList

class MainActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "MainActivity"

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
        //        actBinding.includeBottomView.bottomNavigationView.setSelectedItemId(R.id.navigation_menu1);
//        mIsFirstEnter = true
//        if (KJKomicsApp.IS_CHANGE_LANGUAGE) {
//            KJKomicsApp.IS_CHANGE_LANGUAGE = false
//            requestServer()
//        }
//        if (KJKomicsApp.IS_GET_NEW_GIFT) {
//            actBinding.ivGiftNew.setVisibility(View.VISIBLE)
//        } else {
//            actBinding.ivGiftNew.setVisibility(View.GONE)
//        }
    }

    override fun onStart() {
        super.onStart()
        GoogleAnalytics.getInstance(this).reportActivityStart(this)
    }

    override fun onStop() {
        super.onStop()
        GoogleAnalytics.getInstance(this).reportActivityStop(this)
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
    }

    override fun requestServer() {
        viewModel.requestInitSet()
        viewModel.requestMain()
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_home))
    }

    override fun onChanged(t: Any?) {
        if (t == null) {
//            checkNetworkConnection(context, t, errorView)
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
//                handler.postDelayed({
//                    val intent = Intent(context, EventActivity::class.java)
//                    val curDate = Date()
//                    val curMillis = curDate.time
//                    var diffDay = 2
//                    // 이전 저장 데이터
//                    if ("0" != read(
//                            context,
//                            CODE.FLOATING_BANNER_CLOSE_TIME,
//                            "-1"
//                        )
//                    ) {
//                        val savedMillis = read(
//                            context,
//                            CODE.FLOATING_BANNER_CLOSE_TIME,
//                            "-1"
//                        )!!.toLong()
//                        val diffSec = curMillis - savedMillis
//                        diffDay = (diffSec / (60 * 60 * 24) / 1000).toInt()
//                        if (savedMillis == -1L) {
//                            startActivity(intent)
//                        } else {
//                            if (diffDay >= 1) {
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }, 500)
            }
        } else if (t is Main) {
            if ("00" == t.retcode) {
//                mBannerLolling = t.banner_rolling
                setMainBannerView(t.main_banner)
                setMainContentView(t.layout_contents)
            } else if ("201" == t.retcode) {
//                goLoginAlert(context)
            } else if ("908" == t.retcode) {
            } else {
                if ("" != t.msg) {
                    showToast(t.msg, this@MainActivity)
                }
            }
//            actBinding.swipeLayout.setRefreshing(false)
//            mIsFirstEnter = false
        }
    }

    override fun onBackPressed() {
        if(dl_main_drawer_root.isDrawerOpen(GravityCompat.START)) {
            dl_main_drawer_root.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initHeaderView() {
        initToolbar()
        initDrawerView()
        initLanguageRecyclerView()
        changeLangImageView.setOnClickListener(this)
        giftboxImageView.setOnClickListener(this)
        searchImageView.setOnClickListener(this)
    }

    private fun initMainView() {
        homeButton.setOnClickListener(this)
        onGoingButton.setOnClickListener(this)
        waitButton.setOnClickListener(this)
        rankingButton.setOnClickListener(this)
        genreButton.setOnClickListener(this)
    }

    private fun setMainBannerView(items: ArrayList<DataBanner>?) {
        items?.let {
            bannerPager.adapter = HomeBannerAdapter(items)
        }
    }

    private fun setMainContentView(items: ArrayList<DataMainContents>?) {
        items?.let {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = HomeAdapter(items)
        }
    }

    private fun initLanguageRecyclerView() {
        var items = arrayListOf(DataLanguage("English", "en", true),
            DataLanguage("Hindi", "hi", false),
            DataLanguage("Talua", "ta", false))

        recyclerView_language.adapter = ChangeLanguageAdapter(items)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView_language.layoutManager = layoutManager
        (recyclerView_language.adapter as ChangeLanguageAdapter).setOnItemClickListener(object : ChangeLanguageAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.e(TAG, "onItemClick")
            }
        })
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
        nv_main_navigation_root.getHeaderView(0)?.apply {
            // header
            closeImageView.setOnClickListener { dl_main_drawer_root.closeDrawers() }
            alarmImageView.setOnClickListener {  }
            profileImageView.setImageResource(R.drawable.kk_logo_symbol)
            nicknameTextView.text = read(context, LOCAL_Nickname, "Guest")
            editImageView.setOnClickListener {  }
            coinTextView.text = read(context, LOCAL_coin, "0")
            chargeTextView.setOnClickListener {
                startActivity(Intent(context, CoinActivity::class.java))
            }
            // content
            shopView.setOnClickListener { startActivity(Intent(context, CoinActivity::class.java)) }
            libraryView.setOnClickListener { startActivity(Intent(context, LibraryActivity::class.java)) }
            eventView.setOnClickListener { startActivity(Intent(context, EventActivity::class.java)) }
            noticeView.setOnClickListener { startActivity(Intent(context, NoticeActivity::class.java)) }
            settingsView.setOnClickListener { startActivity(Intent(context, SettingsActivity::class.java)) }
        }
    }

    private fun changeLanguageView() {

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.searchImageView -> startActivity(Intent(context, SearchActivity::class.java))
            R.id.changeLangImageView -> {
                changeLanguageView()
            }
            R.id.giftboxImageView -> {
                if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
//                    intent = Intent(context, GiftBoxActivity::class.java)
//                    startActivity(intent)
                } else {
                    goLoginAlert(this@MainActivity)
                }
            }

            // tabview
            R.id.onGoingButton -> startActivity(Intent(context, OnGoingActivity::class.java))
            R.id.waitButton -> startActivity(Intent(context, WaitFreeActivity::class.java))
            R.id.rankingButton -> startActivity(Intent(context, RankingActivity::class.java))
            R.id.genreButton -> startActivity(Intent(context, GenreActivity::class.java))
        }
    }
}