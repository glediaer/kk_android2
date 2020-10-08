package com.krosskomics.common.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.Coin
import com.krosskomics.common.model.Episode
import com.krosskomics.common.model.More
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.genre.adapter.GenreAdapter
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.ranking.adapter.RankingAdapter
import com.krosskomics.search.activity.SearchActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_topbutton.*

open class RecyclerViewBaseActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
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
        return R.layout.activity_ongoing
    }

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initToolbar()
        initTabView()
        initMainView()
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_ongoing))
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun onChanged(t: Any?) {
        if (t is More) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        } else if (t is Episode) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        } else if (t is Coin) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        }
    }

    open fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        if (body is More) {
            viewModel.totalPage = body.tot_pages
            body.list?.let {
                viewModel.items.addAll(it)
                recyclerView?.adapter?.notifyDataSetChanged()
            }
        } else if (body is Episode) {
            body.list?.let {
                viewModel.items.addAll(it)
                recyclerView?.adapter?.notifyDataSetChanged()
            }
        } else if (body is Coin) {
            body.product_list?.let {
                viewModel.items.addAll(it)
                recyclerView?.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initMainView() {
        recyclerView?.let { initRecyclerView() }
        topButton?.setOnClickListener {
            recyclerView?.smoothScrollToPosition(0)
        }
    }

    private fun initRecyclerView() {
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
                if (!recyclerView.canScrollVertically(1)) {
                    //TODO 화면이 바닥에 닿을때 처리
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
            when(viewModel.tabIndex) {
                3 -> RankingAdapter(viewModel.items, recyclerViewItemLayoutId, context)
                4 -> GenreAdapter(KJKomicsApp.MAIN_CONTENTS)
                else -> CommonRecyclerViewAdapter(
                    viewModel.items,
                    recyclerViewItemLayoutId
                )
            }

        if (viewModel.tabIndex != 4) {
            (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is DataBook) {
                        val intent = Intent(context, SeriesActivity::class.java).apply {
                            putExtra("sid", item.sid)
                            putExtra("title", item.title)
                        }
                        startActivity(intent)
                    }
                }
            })
        }
    }

    private fun initTabView() {
        resetTabState()
        when(viewModel.tabIndex) {
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.searchImageView -> startActivity(Intent(context, SearchActivity::class.java))
            R.id.giftboxImageView -> {
                if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                    intent = Intent(context, LibraryActivity::class.java)
                    startActivity(intent)
                } else {
                    goLoginAlert(context)
                }
            }
            // tabview
            R.id.homeButton -> finish()
            R.id.onGoingButton -> startActivity(Intent(context, RecyclerViewBaseActivity::class.java))
            R.id.waitButton -> {
                startActivity(Intent(context, WaitFreeActivity::class.java))
                finish()
            }
            R.id.rankingButton -> {
                startActivity(Intent(context, RankingActivity::class.java))
                finish()
            }
            R.id.genreButton -> {
                startActivity(Intent(context, GenreActivity::class.java))
                finish()
            }
        }
    }
}