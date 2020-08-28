package com.krosskomics.home.activity

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.model.InitSet
import com.krosskomics.common.model.Main
import com.krosskomics.home.viewmodel.MainViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

class MainActivity : BaseActivity(), Observer<Any> {
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
    }

    override fun requestServer() {
        viewModel.requestInitSet()
        viewModel.requestMain()
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_home))
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun onChanged(t: Any) {
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
//                setMainBannerView(t.main_banner)
//                setMainContentView(t)
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
        initRecyclerView()
    }

    private fun initRecyclerView() {
//        recyclerView.adapter
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
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher)
        }
    }
}
