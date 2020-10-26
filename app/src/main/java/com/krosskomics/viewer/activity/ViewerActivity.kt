package com.krosskomics.viewer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.createChooser
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.comment.activity.CommentActivity
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.Episode
import com.krosskomics.common.model.PurchaseEpisode
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.getScreenHeight
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.PreCachingLayoutManager
import com.krosskomics.util.ServerUtil
import com.krosskomics.util.ServerUtil.service
import com.krosskomics.viewer.adapter.EpListAdapter
import com.krosskomics.viewer.adapter.ViewerAdapter
import com.krosskomics.viewer.viewmodel.ViewerViewModel
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.android.synthetic.main.view_ep_purchase.*
import kotlinx.android.synthetic.main.view_toolbar_viewer.*
import kotlinx.android.synthetic.main.view_toolbar_viewer.view.*
import kotlinx.android.synthetic.main.view_viewer_footer.*
import kotlinx.android.synthetic.main.view_viewer_footer_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ViewerActivity : ToolbarTitleActivity() {
    private val TAG = "ViewerActivity"

    lateinit var autoTextArray: ArrayList<TextView>
    var autoScrollTimer: CountDownTimer? = null

    override val viewModel: ViewerViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ViewerViewModel(application) as T
            }
        }).get(ViewerViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_viewer
        return R.layout.activity_viewer
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
        toolbarLike.setOnClickListener {
            requestLike()
        }
        toolbarShare.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, viewModel.item.share_url)
                createChooser(this, "공유하기")
                startActivity(this)
            }
        }
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_viewer))
    }

    override fun initLayout() {
        super.initLayout()
        initViewer()
        initEpListView()
        initFooterView()
        initSettingView()
    }

    private fun initSettingView() {
        initAutoScroll()
        settingCloseView.setOnClickListener {
            settingBottomView.visibility = View.GONE
        }
        nightSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

        }
        autoNextSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

        }
        settingSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 10) {
                    seekBar.progress = 10
                }
                val params = window.attributes;

                params.screenBrightness = (progress / 100).toFloat()

                window.attributes = params;
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun initAutoScroll() {
        autoTextArray = arrayListOf(auto1xTextView, auto2xTextView, auto3xTextView)
        auto1xTextView.setOnClickListener {
            autoTextArray.forEach { view ->
                view.isSelected = false
            }
            it.isSelected = true
            setAutoScroll(1)
        }
        auto2xTextView.setOnClickListener {
            autoTextArray.forEach { view ->
                view.isSelected = false
            }
            it.isSelected = true
            setAutoScroll(2)
        }
        auto3xTextView.setOnClickListener {
            autoTextArray.forEach { view ->
                view.isSelected = false
            }
            it.isSelected = true
            setAutoScroll(3)
        }
    }

    private fun setAutoScroll(speed: Int) {
        settingBottomView.visibility = View.GONE
        autoScrollCancel()
//        isAutoScroll = KJKomicsApp.autoscroll
        KJKomicsApp.autoscroll = 0
        val totalScrollTime = Long.MAX_VALUE
        var scrollPeriod = 20L
        var heightToScroll = 20
        when (speed) {
            1 -> {
                scrollPeriod = 20L
                heightToScroll = 15
            }
            2 -> {
                scrollPeriod = 20L
                heightToScroll = 35
            }
            3 -> {
                scrollPeriod = 30L
                heightToScroll = 50
            }
        }
//        hideToggleToolBar()
        recyclerView.post {
            autoScrollTimer = object : CountDownTimer(totalScrollTime, scrollPeriod) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
                        recyclerView.smoothScrollBy(0, heightToScroll)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFinish() {}
            }.start()
        }
    }

    private fun autoScrollCancel() {
        try {
            autoScrollTimer?.cancel()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initEpListView() {
        epCloseView.setOnClickListener {
            epView.visibility = View.GONE
        }
        epRecyclerView?.let {
            it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = EpListAdapter(viewModel.arr_episode, R.layout.item_view_episode, context)
            (it.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is DataEpisode) {
                        loadEpCheck(item.eid)
                    }
                }
            })
        }
    }

    private fun initFooterView() {
        prevView.setOnClickListener { loadEpCheck(viewModel.item.pre_eid) }
        commentView.setOnClickListener {
            startActivity(Intent(context, CommentActivity::class.java))
        }
        settingView.setOnClickListener { settingBottomView.visibility = View.VISIBLE }
        epListView.setOnClickListener { epView.visibility = View.VISIBLE }
        nextView.setOnClickListener { loadEpCheck(viewModel.item.next_eid) }
    }

    private fun initViewer() {
        if (read(context, CODE.LOCAL_IS_VIEWER_TUTO, "false") == "false") {
            llTutorial.visibility = View.VISIBLE
            llTutorial.setOnClickListener(View.OnClickListener {
                llTutorial.visibility = View.GONE
            })
            write(context, CODE.LOCAL_IS_VIEWER_TUTO, "true")
        }
        val handler: Handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                val fadeOutAni =
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.fadeout
                    )
                if (llTutorial.isShown) {

                    llTutorial.animation = fadeOutAni
                    llTutorial.visibility = View.GONE
                }
                vScrollImageView.animation = fadeOutAni
                vScrollImageView.visibility = View.GONE
                hScrollImageView.animation = fadeOutAni
                hScrollImageView.visibility = View.GONE
            }
        }
        handler.sendEmptyMessageDelayed(0, 3000) // ms, 3초후 종료시킴
        if (viewModel.isVerticalView) {
            vScrollImageView.visibility = View.VISIBLE
            hScrollImageView.visibility = View.GONE
            vScrollImageView.setOnClickListener { it.visibility = View.GONE }
        } else {
            vScrollImageView.visibility = View.GONE
            hScrollImageView.visibility = View.VISIBLE
            hScrollImageView.setOnClickListener { it.visibility = View.GONE }
        }
    }

    private fun refreshViewer() {
//        if ("1" == viewModel.item.vviewer) {
//            vScrollImageView.visibility = View.VISIBLE
//            hScrollImageView.visibility = View.GONE
//        } else {
//            vScrollImageView.visibility = View.GONE
//            hScrollImageView.visibility = View.VISIBLE
//            if ("L" == viewModel.item.hviewer) {
//                viewModel.revPager = false
//            } else if ("R" == viewModel.item.hviewer) {
//                viewModel.revPager = true
//            }
//        }

//        val handler: Handler = @SuppressLint("HandlerLeak")
//        object : Handler() {
//            override fun handleMessage(msg: Message) {
//                val fadeOutAni =
//                    AnimationUtils.loadAnimation(
//                        context,
//                        R.anim.fadeout
//                    )
//                vScrollImageView.animation = fadeOutAni
//                vScrollImageView.visibility = View.GONE
//                hScrollImageView.animation = fadeOutAni
//                hScrollImageView.visibility = View.GONE
//            }
//        }
//        handler.sendEmptyMessageDelayed(0, 3000) // ms, 3초후 종료시킴
        viewModel.apply {
            items.clear()
            epList.clear()
            arr_episode.clear()
            viewPosition = 0
        }
    }

    override fun initModel() {
        intent?.apply {
            toolbarTitleString = extras?.getString("title").toString()
            viewModel.item.eid = extras?.getString("eid").toString()
            viewModel.isVerticalView = extras?.getBoolean("isVerticalView") ?: true
            viewModel.revPager = extras?.getBoolean("revPager") ?: false
        }
        super.initModel()
        viewModel.getCheckEpResponseLiveData().observe(this, this)
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    private fun loadEpCheck(eid: String?) {
        viewModel.requestCheckEp(eid)
    }

    //에피소드 선택구매 요청
    private fun requestEpisodeSelectPurchase(unlockType: String) {
        var ep_list: String = viewModel.epList.toString()
        ep_list = ep_list.trim { it <= ' ' }.replace(" ", "")
        ep_list = ep_list.substring(1, ep_list.length - 1)
        val setPurchaseEpisode: Call<PurchaseEpisode> =
            service.setPurchaseSelectEpisode(
                read(context, CODE.CURRENT_LANGUAGE, "en"),
                ep_list, unlockType
            )
        setPurchaseEpisode.enqueue(object : Callback<PurchaseEpisode?> {
            override fun onResponse(
                call: Call<PurchaseEpisode?>,
                response: Response<PurchaseEpisode?>
            ) {
                try {
                    if (response.isSuccessful) {
                        viewModel.epList.clear()
                        epPurchaseDialog.visibility = View.GONE

                        response.body()?.let {
                            if ("00" == it.retcode) {
                                requestServer()
                                if ("" != it.user_coin) {
                                    write(context, CODE.LOCAL_coin, it.user_coin)
                                }
                            } else if ("202" == it.retcode) {
                                startActivity(Intent(context, CoinActivity::class.java))
                            } else {
                                if ("" != it.msg) {
                                    showToast(it.msg, context)
                                }
                            }
                        }

                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                call: Call<PurchaseEpisode?>,
                t: Throwable
            ) {
//                    hideProgress()
                try {
//                        checkNetworkConnection(
//                            context,
//                            t,
//                            viewError
//                        )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun requestSetImageIndex(visibleItemPosition: Int) {
        if (BuildConfig.DEBUG) {
            Log.e(
                TAG, "requestSetImageIndex visibleItemPosition : $visibleItemPosition"
            )
        }
        if (TextUtils.isEmpty(viewModel.item.sid)) return
        val api = service.setImageIndex(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "save_img_index", viewModel.item.sid, viewModel.item.eid, visibleItemPosition.toString(),
            viewModel.item.ep_view_id
        )
        api.enqueue(object : Callback<Default> {
            override fun onResponse(
                call: Call<Default>,
                response: Response<Default>
            ) {
                try {
                    if (response.isSuccessful) {
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                try {
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun requestEpViewOut() {
        if (BuildConfig.DEBUG) {
            Log.e(
                TAG, "requestEpViewOut viewPosition : ${viewModel.viewPosition}"
            )
        }
        if (TextUtils.isEmpty(viewModel.item.sid)) return
        val api = service.setEpViewOut(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "ep_view_out", viewModel.item.ep_view_id, viewModel.viewPosition.toString()
        )
        api!!.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                try {
                    if (response.isSuccessful) {
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onChanged(t: Any?) {
        if (t is Episode) {
            when(viewModel.requestType) {
                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_A -> {
                    if ("00" == t.retcode) {
                        setMainContentView(t)
                    }
                }
                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_B -> {
                    when(t.retcode) {
                        "00" -> showEp(t.episode?.eid!!)
                        "201" -> goLoginAlert(context)
                        "202" -> goCoinAlert(context)
                        "205" -> {
                            // 구매팝업
                            // ablestore == 1 소장구매 가능
                            // ablerent == 1 렌트 가능
                            showPurchaseRentDialog(t.episode)
                        }
                        else -> {
                            t.msg?.let {
                                CommonUtil.showToast(it, context)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun showEp(eid: String) {
        viewModel.viewPosition = (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstCompletelyVisibleItemPosition()
        if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
            requestSetImageIndex(viewModel.viewPosition)
        } else {
            requestEpViewOut()
        }
//        autoScrollCancel()
        viewModel.item.eid = eid
//        epRecyclerView.adapter.setEpNo(eid)
        requestServer()
    }

    override fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        if (body is Episode) {
            refreshViewer()
            viewModel.item = body.episode!!
            viewModel.item.let {
                if (viewModel.isFirstRequest) {
                    viewModel.listBanner.clear()
                    // 고정 배너
                    if (!body.banner_list.isNullOrEmpty()) {
                        viewModel.listBanner.addAll(body.banner_list!!)
                        (recyclerView.adapter as ViewerAdapter).setFooterBannerData(viewModel.listBanner)
                    }
                }
                toolbar.toolbarLike.isSelected = "0" == it.able_like
                // 댓글
                if ("0" == it.allow_comment) {
                    commentView.visibility = View.GONE
                } else {
                    commentView.visibility = View.VISIBLE
                }
                if ("" != it.read_ep_img_index) {
                    viewModel.viewPosition = it.read_ep_img_index!!.toInt()
                }
                it.ep_contents.let { contents ->
                    makeList(contents, true)
                }
                //이전 있음
                prevView.isEnabled = "0" != it.pre_eid
                //다음 있음
                nextView.isEnabled = "0" != it.next_eid

                //episode
                viewModel.arr_episode.clear()
                viewModel.allbuy_possibility_count = 0

                body.episode_list?.forEach { epItem ->
                    viewModel.arr_episode.add(epItem)
                    //전체구매
                    if ("0" == epItem.isunlocked) {
                        epItem.isChecked = true
                        epItem.isCheckVisible = false
                        epItem.possibility_allbuy = true
                        if (it.ep_seq < epItem.ep_seq) {
                            viewModel.allbuy_possibility_count++
                            viewModel.epList.add(epItem.eid)
                        }
                    }
                    epItem.isEpSelect = it.ep_seq == epItem.ep_seq
//                    it.adapter?.notifyDataSetChanged()
//                    // 구매가능한 마지막 회차
//                    if (viewModel.allbuy_possibility_count == 1 && !isSetLastBuySeq) {
//                        lastBuySeq = java.lang.String.valueOf(curEpisode.ep_seq)
//                        isSetLastBuySeq = true
//                    }
//                    // 구매가능한 첫 회차
//                    if (allbuy_possibility_count > before_allBuyPosibilityCount) {
//                        firstBuySeq = java.lang.String.valueOf(curEpisode.ep_seq)
//                        before_allBuyPosibilityCount = allbuy_possibility_count
//                    }
                }
                epRecyclerView.adapter?.notifyDataSetChanged()

                viewModel.isFirstRequest = false
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = ViewerAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as ViewerAdapter).setOnItemClickListener(object : ViewerAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                autoScrollCancel()
            }
        })
        recyclerView.addOnScrollListener(onScrollListener)
    }

    /**
     * 좋아요 설정
     */
    private fun requestLike() {
        val api: Call<Default> = ServerUtil.service.setLike(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "like", viewModel.item.sid
        )
        api.enqueue(object : Callback<Default> {
            override fun onResponse(
                call: Call<Default>,
                response: Response<Default>
            ) {
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if ("00" == it.retcode) {
                                toolbar.toolbarLike.isSelected = !toolbar.toolbarLike.isSelected
                            } else if ("201" == it.retcode) {
                                goLoginAlert(context)
                            }
                            it.msg?.let { msg ->
                                showToast(msg, context)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun makeList(urls: String, isReload: Boolean) {
        val arr_url = urls.split(",".toRegex()).toTypedArray()
        if (viewModel.revPager) {
            for (i in arr_url.indices.reversed()) {
                viewModel.items.add(arr_url[i])
            }
        } else {
            viewModel.items.addAll(
                Arrays.asList(
                    *arr_url
                )
            )
        }
        //단행본의 세로보기 추가 viewer_type = 01(세로가능) and hviewer_type =00(가로 불가) 일경우 = 세로스크롤만 가능
        try {
            if ("1" == viewModel.item.vviewer) {
                recyclerView?.apply {
                    layoutManager = PreCachingLayoutManager(context)
                    (layoutManager as PreCachingLayoutManager).orientation = LinearLayoutManager.VERTICAL
                    (layoutManager as PreCachingLayoutManager).setExtraLayoutSpace(getScreenHeight(context))

                    recyclerView.adapter = ViewerAdapter(viewModel.items, recyclerViewItemLayoutId, context)
                    (recyclerView.adapter as ViewerAdapter).setFooterBannerData(viewModel.listBanner)
                }
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

//                actBinding.pagerView.setVisibility(View.GONE)
//                actBinding.layProgress.setVisibility(View.GONE)
//                actBinding.ivAuto.setVisibility(View.VISIBLE)

                //recyclerview
//                if (isReload) {
//                    (recyclerView.adapter as ViewerAdapter).addRange(arr_url, viewPosition)
//                } else {
//                    webtoonAdapter.addAll(arr_url)
//                }
//                recyclerView.layoutManager = PreCachingLayoutManager(context)
//                layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
//                layoutManager.setExtraLayoutSpace(getScreenHeight(this))
//                actBinding.listViewRecycler.getRecycledViewPool().clear()
//                actBinding.listViewRecycler.setLayoutManager(layoutManager)
//                actBinding.listViewRecycler.setAdapter(webtoonAdapter)
//                actBinding.listViewRecycler.setVisibility(View.VISIBLE)
//                // 이전에 webtoon 보던곳으로 스크롤
//                if (0 != viewPosition) {
//                    layoutManager.scrollToPositionWithOffset(
//                        if (revPager) com.krosskomics.page.ShowActivity.arr_pics.size - viewPosition - 1 else viewPosition,
//                        0
//                    )
//                }
            } else {
                recyclerView?.apply {
                    layoutManager = PreCachingLayoutManager(context)
                    (layoutManager as PreCachingLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
                    (layoutManager as PreCachingLayoutManager).setExtraLayoutSpace(getScreenHeight(context))

                    recyclerView.adapter = ViewerAdapter(viewModel.items, R.layout.item_viewer_comic, context)
                    (recyclerView.adapter as ViewerAdapter).setFooterBannerData(viewModel.listBanner)
                    val snapHelper = PagerSnapHelper()
                    snapHelper.attachToRecyclerView(recyclerView)
                }
//                if (revPager) {
//                    com.krosskomics.page.ShowActivity.arr_pics.add(0, arr_url[0])
//                } else {
//                    com.krosskomics.page.ShowActivity.arr_pics.add(arr_url[0]) // 마지막 페이지 강제 생성
//                }
//                val adapter = ShowListBookAdapter(
//                    this,
//                    com.krosskomics.page.ShowActivity.arr_pics,
//                    revPager,
//                    reloadInterface
//                )
//                actBinding.pagerView.setAdapter(adapter)
//                actBinding.listViewRecycler.setVisibility(View.GONE)
//                actBinding.pagerView.setVisibility(View.VISIBLE)
//                actBinding.ivAuto.setVisibility(View.GONE)
//                actBinding.pagerView.setOnTouchListener(OnTouchListener { v, event -> false })
//                actBinding.layProgress.setVisibility(View.VISIBLE)
//                if (revPager) {
//                    actBinding.pagerView.setCurrentItem(com.krosskomics.page.ShowActivity.arr_pics.size - 1)
//                    actBinding.progress.setRotation(180)
//                }
//                actBinding.txtProgress.setText("1/" + com.krosskomics.page.ShowActivity.arr_pics.size)
//                actBinding.progress.setMax(com.krosskomics.page.ShowActivity.arr_pics.size)
//                actBinding.progress.setProgress(1)
//                actBinding.progress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
//                    override fun onStopTrackingTouch(seekBar: SeekBar) {
//                        // TODO Auto-generated method stub
//                    }
//
//                    override fun onStartTrackingTouch(seekBar: SeekBar) {
//                        // TODO Auto-generated method stub
//                    }
//
//                    override fun onProgressChanged(
//                        seekBar: SeekBar,
//                        progress: Int,
//                        fromUser: Boolean
//                    ) {
//                        scroll_txt = "up"
//                        var pos = progress
//                        if (revPager) {
//                            //오른쪽으로 슬라이딩
//                            pos = com.krosskomics.page.ShowActivity.arr_pics.size - progress
//                            actBinding.pagerView.setCurrentItem(pos)
//                            scroll_txt = "right"
//                        } else {
//                            //왼쪽 슬라이딩
//                            actBinding.pagerView.setCurrentItem(progress - 1)
//                            scroll_txt = "left"
//                        }
//                    }
//                })
//
//                // webtoon -> comic 보던곳으로 스크롤
//                if (viewPosition != 0) {
//                    if (revPager) {
//                        actBinding.pagerView.setCurrentItem(com.krosskomics.page.ShowActivity.arr_pics.size - viewPosition - 1)
//                    } else {
//                        actBinding.pagerView.setCurrentItem(viewPosition)
//                    }
//                }
            }
        } catch (e: java.lang.Exception) {
            e.stackTrace
        }

//        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun showPurchaseRentDialog(episode: DataEpisode?) {
        episode?.let {
            epPurchaseDialog.visibility = View.VISIBLE
            titleTextView.text = viewModel.item.title

            myKeyTextView.text = read(context, CODE.LOCAL_coin, "0")
            discountRateTextView.text = "0 %"
            totalTextView.text = "${it.ep_rent_price}"

            totalPurchaseImageView.visibility = View.GONE

            viewModel.items.forEach { item ->
                if (item is DataEpisode) {
                    item.isCheckVisible = true
                    item.isChecked = item.ep_seq == episode.ep_seq
//                    calcPurchaseCurrentToLastEp(item.ep_seq)
                }
            }
            recyclerView.adapter?.notifyDataSetChanged()

            if ("1" == episode.allow_rent) {
                rentalButton.isEnabled
                rentalButton.isSelected = true
            } else if ("1" == episode.allow_store) {
                purchaseButton.isEnabled
                purchaseButton.isSelected = true
            }

            allBuyCal()

            rentalButton.setOnClickListener { view ->
                view.isSelected = true
                purchaseButton.isSelected = false
                unlockButton.isEnabled = true

                allBuyCal()
            }
            purchaseButton.setOnClickListener { view ->
                view.isSelected = true
                rentalButton.isSelected = false
                unlockButton.isEnabled = true

                allBuyCal()
            }
            totalPurchaseImageView.setOnClickListener { view ->
                // 전체구매
                view.visibility = View.GONE
                viewModel.items.forEach { item ->
                    if (item is DataEpisode) {
                        item.isCheckVisible = true
                        item.isChecked = true
                    }
                }
                allBuyAll()
            }
            unlockButton.setOnClickListener {
                if ("1" == episode.allow_rent) {
                    if (viewModel.allbuyRentCoin > read(context, CODE.LOCAL_coin, "0")!!.toInt()) {
                        val intent = Intent(context, CoinActivity::class.java) //충전 페이지
                        startActivity(intent)
                    } else {
                        // rent request
                        requestEpisodeSelectPurchase("rent")
                    }
                } else {
                    if (viewModel.allbuy_coin > read(context, CODE.LOCAL_coin, "0")!!.toInt()) {
                        val intent = Intent(context, CoinActivity::class.java) //충전 페이지
                        startActivity(intent)
                    } else {
                        // purchase request
                        requestEpisodeSelectPurchase("store")
                    }
                }
                resetDefaultView()
            }
            epPurchaseDialog.setOnClickListener { epPurchaseDialog.visibility = View.GONE }
        }
    }

    //전체구매 코인 계산
    fun allBuyCal(): Int {
        viewModel.let {
            it.allbuy_possibility_count = 0
            it.allbuy_count = 0
            it.allbuySaveRate = 0f
            it.allbuy_coin = 0
            it.allbuyRentCoin = 0
            it.arr_episode.forEach { item ->
                if (item.isChecked) {
                    it.allbuy_coin = it.allbuy_coin + item.ep_store_price
                    it.allbuyRentCoin = it.allbuyRentCoin + item.ep_rent_price
                    it.allbuy_count++
                }
                if (item.possibility_allbuy) {
                    it.allbuy_possibility_count++
                }
            }// 대여
            // 소장
            when {
                it.allbuy_count in 2..2 -> {
                    it.allbuySaveRate = 0f
                }
                it.allbuy_count in 3..9 -> {
                    it.allbuySaveRate = 0.1f
                }
                it.allbuy_count in 10..29 -> {
                    it.allbuySaveRate = 0.2f
                }
                it.allbuy_count >= 30 -> {
                    it.allbuySaveRate = 0.3f
                }
            }
            it.allbuy_coin = it.allbuy_coin - Math.round(it.allbuy_coin * it.allbuySaveRate)

            it.allbuyRentCoin = it.allbuyRentCoin - Math.round(it.allbuyRentCoin * it.allbuySaveRate)

            discountRateTextView.text = "${(it.allbuySaveRate * 100).toInt()} %"

            if (purchaseButton.isSelected) {
                totalTextView.text = "${viewModel.allbuy_coin}"
            }
            if (rentalButton.isSelected) {
                totalTextView.text = "${viewModel.allbuyRentCoin}"
            }

            unlockButton.isEnabled = it.allbuy_count > 0

            epPurchaseCountTextView.text = "${viewModel.allbuy_count}"
            return it.allbuy_coin
        }
    }

    //전체구매 리스트 전체 선택
    fun allBuyAll(): Int {
        viewModel.let {
            it.allbuy_possibility_count = 0
            it.allbuy_count = 0
            it.allbuySaveRate = 0f
            it.allbuy_coin = 0
            it.allbuyRentCoin = 0
            it.arr_episode.forEach { item ->
                if (item.possibility_allbuy) {
                    item.isChecked = true
                    item.isCheckVisible = true
                    it.epList.add(it.allbuy_count, item.eid)
                    it.allbuy_coin = it.allbuy_coin + item.ep_store_price
                    it.allbuyRentCoin = it.allbuyRentCoin + item.ep_rent_price
                    it.allbuy_count++
                }
            }
            recyclerView.adapter?.notifyDataSetChanged()
            when {
                it.allbuy_count in 2..2 -> {
                    it.allbuySaveRate = 0f
                }
                it.allbuy_count in 3..9 -> {
                    it.allbuySaveRate = 0.1f
                }
                it.allbuy_count in 10..29 -> {
                    it.allbuySaveRate = 0.2f
                }
                it.allbuy_count >= 30 -> {
                    it.allbuySaveRate = 0.3f
                }
            }
            it.allbuy_coin = it.allbuy_coin - Math.round(it.allbuy_coin * it.allbuySaveRate)

            it.allbuyRentCoin = it.allbuyRentCoin - Math.round(it.allbuyRentCoin * it.allbuySaveRate)

            discountRateTextView.text = "${(it.allbuySaveRate * 100).toInt()} %"

            if (purchaseButton.isSelected) {
                totalTextView.text = "${viewModel.allbuy_coin}"
            }
            if (rentalButton.isSelected) {
                totalTextView.text = "${viewModel.allbuyRentCoin}"
            }
            unlockButton.isEnabled = it.allbuy_count > 0

            epPurchaseCountTextView.text = "${viewModel.allbuy_count}"
            return it.allbuy_coin
        }
    }

    private fun resetDefaultView() {
        epPurchaseDialog.visibility = View.GONE
    }

    private val onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        private val INTERVAL_TIME: Long = 2000
        private var scrollTime: Long = 0
        override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
            if (recyclerView.layoutManager?.childCount!! > 0 && scrollState == RecyclerView.SCROLL_STATE_IDLE && //layoutManager.findFirstVisibleItemPosition() == layoutManager.getItemCount() - 1 &&
                recyclerView.layoutManager?.getChildAt(recyclerView.layoutManager?.childCount!! - 1)
                    ?.bottom!! <= recyclerView.layoutManager?.height!!
            ) {
                if (!autoNextSwitch.isChecked) return
                val tempTime = System.currentTimeMillis()
                val intervalTime = tempTime - scrollTime
                when {
                    "0" == viewModel.item.next_eid -> {
                        showToast(
                            getString(R.string.msg_scroll_end_last_view),
                            context
                        )
                        return
                    }
                    intervalTime in 0..INTERVAL_TIME -> {
                        loadEpCheck(viewModel.item.next_eid)
                    }
                    else -> {
                        scrollTime = tempTime
                        showToast(
                            getString(R.string.msg_scroll_end_next_view),
                            context)
                    }
                }
            }
        }
    }
}