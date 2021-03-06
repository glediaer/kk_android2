package com.krosskomics.common.activity

import android.content.Intent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CashHistoryActivity
import com.krosskomics.comment.activity.CommentActivity
import com.krosskomics.comment.activity.CommentReportActivity
import com.krosskomics.comment.viewmodel.CommentViewModel
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.*
import com.krosskomics.common.model.*
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.genre.activity.GenreDetailActivity
import com.krosskomics.mainmenu.adapter.GenreAdapter
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel
import com.krosskomics.mainmenu.adapter.RankingAdapter
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.getNetworkInfo
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.setAppsFlyerEvent
import com.krosskomics.util.CommonUtil.showErrorView
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_main_content.errorView
import kotlinx.android.synthetic.main.activity_main_content.nestedScrollView
import kotlinx.android.synthetic.main.activity_main_content.recyclerView
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.activity_more.emptyView
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.fragment_genre.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_network_error.view.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.android.synthetic.main.view_toolbar.actionItem
import kotlinx.android.synthetic.main.view_toolbar.toolbarTitle
import kotlinx.android.synthetic.main.view_toolbar.view.*
import kotlinx.android.synthetic.main.view_topbutton.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

open class RecyclerViewBaseActivity : BaseActivity(), Observer<Any> {
    private val TAG = "RecyclerViewBaseActivity"

    protected open val viewModel: BaseViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return BaseViewModel(application) as T
            }
        }).get(BaseViewModel::class.java)
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
        return R.layout.fragment_ongoing
    }

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initToolbar()
        initTabView()
        initMainView()
        initFooterView()
    }

    private fun initFooterView() {
        moveTopView?.setOnClickListener {
            if (nestedScrollView != null) {
                nestedScrollView.scrollTo(0, 0)
            } else {
                recyclerView?.layoutManager?.scrollToPosition(0)
            }
        }
    }

    override fun initErrorView() {
        errorView?.refreshButton?.setOnClickListener {
            if (getNetworkInfo(context) == null) {
                return@setOnClickListener
            }
            errorView?.visibility = View.GONE
            requestServer()
        }

        errorView?.goDownloadEpButton?.setOnClickListener {
            startActivity(Intent(context, LibraryActivity::class.java))
            finish()
        }
    }

    override fun requestServer() {
        if (getNetworkInfo(context) == null) {
            errorView?.visibility = View.VISIBLE
            return
        }
        showProgress(context)
        viewModel.requestMain()
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_home))
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun onChanged(t: Any?) {
        when (t) {
            is More -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is Episode -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is EpisodeMore -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is Coin -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is Search -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is News -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is Comment -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is Genre -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is CashHistory -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
            is Event -> {
                if ("00" == t.retcode) {
                    setMainContentView(t)
                } else {
                    t.msg?.let {
                        CommonUtil.showToast(it, context)
                    }
                }
            }
        }
        hideProgress()
    }

    open fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        when (body) {
            is More -> {
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }

                totalCountTextView?.text =
                    getString(R.string.str_total) + " ${viewModel.items.count()}"
                if (body.list_title?.isNotEmpty() == true) {
                    toolbar.toolbarTitle.text = toolbarTitleString
                }

                if (body.genre?.isNotEmpty() == true) {
                    if (context is GenreDetailActivity) {
                        (context as GenreDetailActivity).initTabItems(body.genre)
                    }
                }
            }
            is Episode -> {
                body.list?.list?.let {
//                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is EpisodeMore -> {
                body.list.let {
//                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is Coin -> {
                body.product_list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is News -> {
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is Comment -> {
                if (body.page == 0) {   // 리스트 아닐때
                    if (context is CommentActivity) {
                        (context as CommentActivity).reloadRequestComment()
                        return
                    }
                }
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
                (context as CommentActivity).initHeaderView()
            }
            is Genre -> {
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is CashHistory -> {
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is Event -> {
                if (body.list.isNullOrEmpty()) {
                    showErrorView(emptyView)
                    return
                }
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    open fun initMainView() {
        recyclerView?.let { initRecyclerView() }
        nestedScrollView?.let { initNestedScrollView() }
        topButton?.setOnClickListener {
            if (nestedScrollView != null) {
                nestedScrollView.scrollTo(0, 0)
            } else {
                recyclerView?.layoutManager?.scrollToPosition(0)
            }
        }
    }

    private fun initNestedScrollView() {
        nestedScrollView?.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > CODE.VISIBLE_NESTEDSCROLL_TOPBUTTON_Y) {
                topButton?.visibility = View.VISIBLE
            } else {
                topButton?.visibility = View.GONE
            }
        }
    }

    open fun initRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (getCurrentItem(recyclerView) > CODE.VISIBLE_LIST_TOPBUTTON_CNT) {
                    topButton?.visibility = View.VISIBLE
                } else {
                    topButton?.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if ((recyclerView.tag)?.toString() == "series") return
                if (!recyclerView.canScrollVertically(1)) {
                    if (viewModel.page < viewModel.totalPage) {
                        viewModel.page++
                        requestServer()
                    }
                }
            }
        })
        initRecyclerViewAdapter()
    }

    open fun initRecyclerViewAdapter() {
        recyclerView?.adapter =
            CommonRecyclerViewAdapter(
                    viewModel.items,
                    recyclerViewItemLayoutId
                )

        if (viewModel.tabIndex != 4) {
            (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object :
                RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    when (item) {
                        is DataBook -> {
                            val intent = Intent(context, SeriesActivity::class.java).apply {
                                putExtra("sid", item.sid)
                                putExtra("title", item.title)
                            }
                            startActivity(intent)
                        }
                        is DataReport -> {
                            (context as CommentReportActivity).resetReportSelect(position)
                        }
                        is DataNews -> {

                        }
                        is DataEvent -> {
                            CommonUtil.setEventAction(context, item)
                        }
                    }
                }
            })

            (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnSubscribeClickListener(object :
                RecyclerViewBaseAdapter.OnSubscribeClickListener {
                override fun onItemClick(item: Any, position: Int, selected: Boolean) {
                    if (item is DataBook) {
                        var action = if (selected) {
                            "S"
                        } else {
                            "C"
                        }
                        requestSubscribe(item.title, item.sid ?: "", action)
                    }
                }
            })

            (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnCommentReportClickListener(
                object : RecyclerViewBaseAdapter.OnCommentReportClickListener {
                    override fun onItemClick(item: Any, position: Int) {
                        if (item is DataComment) {
                            val intent = Intent(context, CommentReportActivity::class.java).apply {
                                if (viewModel is CommentViewModel) {
                                    putExtra("sid", (viewModel as CommentViewModel).sid)
                                    putExtra("seq", (viewModel as CommentViewModel).seq)
                                }
                            }
                            startActivity(intent)
                        }
                    }
                })

            (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnDelteItemClickListener(
                object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                    override fun onItemClick(item: Any) {
                        if (item is DataComment) {
                            // 댓글 삭제 요청
                            if (viewModel is CommentViewModel) {
                                (viewModel as CommentViewModel).type = "del"
                                (viewModel as CommentViewModel).seq = item.seq ?: "0"
                                requestServer()
                            }
                        }
                    }
                })

            (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnLikeClickListener (
                object : RecyclerViewBaseAdapter.OnLikeClickListener {
                    override fun onItemClick(item: Any, position: Int) {
                        if (item is DataComment) {
                            // 좋아요 요청
                            if (viewModel is CommentViewModel) {
                                (viewModel as CommentViewModel).type = "like"
                                (viewModel as CommentViewModel).seq = item.seq ?: "0"
                                requestServer()
                            }
                        }
                    }
                })
        }
    }

    private fun initTabView() {
        resetTabState()
        when (viewModel.tabIndex) {
            1 -> onGoingButton?.isSelected = true
            2 -> waitButton?.isSelected = true
            3 -> rankingButton?.isSelected = true
            4 -> genreButton?.isSelected = true
        }
        homeButton?.setOnClickListener(this)
        onGoingButton?.setOnClickListener(this)
        waitButton?.setOnClickListener(this)
        rankingButton?.setOnClickListener(this)
        genreButton?.setOnClickListener(this)
    }

    private fun resetTabState() {
        onGoingButton?.isSelected = false
        waitButton?.isSelected = false
        rankingButton?.isSelected = false
        genreButton?.isSelected = false
    }

    private fun requestSubscribe(title: String?, sid: String, action: String) {
        val api = ServerUtil.service.setNotiSelector(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "subscribe", sid, action, "",
            CommonUtil.read(context, CODE.LOCAL_Android_Id, "")
        )
        api.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            if ("S" == action) {
                                val eventValue: MutableMap<String, Any?> =
                                    HashMap()
                                eventValue["af_content"] = title + " (" + read(
                                    context,
                                    CODE.CURRENT_LANGUAGE,
                                    "en"
                                ) + ")"
                                eventValue["af_content_id"] = sid
                                setAppsFlyerEvent(context, "af_subscribe", eventValue)
                            }
                        } else if ("202" == item.retcode) {
                            goCoinAlert(context)
                        } else {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                        }
                        hideProgress()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    hideProgress()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                hideProgress()
                try {
//                    checkNetworkConnection(context, t, errorView)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}