package com.krosskomics.common.fragment

import android.content.Intent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataNotice
import com.krosskomics.common.data.DataWaitFreeTerm
import com.krosskomics.common.model.*
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.mainmenu.adapter.GenreAdapter
import com.krosskomics.mainmenu.adapter.RankingAdapter
import com.krosskomics.mainmenu.adapter.WaitFreeTermAdapter
import com.krosskomics.mainmenu.fragment.WaitFreeFragment
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.fragment_genre.*
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_genre_detail.*
import kotlinx.android.synthetic.main.fragment_genre_detail.nestedScrollView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.fragment_waitfree.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import kotlinx.android.synthetic.main.view_topbutton.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class RecyclerViewBaseFragment : BaseFragment() {
    override val viewModel: FragmentBaseViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FragmentBaseViewModel(requireContext()) as T
            }
        }).get(FragmentBaseViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_more
        return R.layout.activity_more
    }

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initMainView()
    }

    override fun requestServer() {
        viewModel.requestMain()
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

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (getCurrentItem(recyclerView) > CODE.VISIBLE_LIST_TOPBUTTON_CNT) {
                    topButton.visibility = View.VISIBLE
                } else {
                    topButton.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
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
            when (viewModel.tabIndex) {
                3 -> RankingAdapter(
                    viewModel.items,
                    recyclerViewItemLayoutId
                )
                4 -> GenreAdapter(viewModel.items)
                else -> CommonRecyclerViewAdapter(
                    viewModel.items,
                    recyclerViewItemLayoutId
                )
            }

        if (viewModel.tabIndex != 4) {
            (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
                setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                    override fun onItemClick(item: Any?, position: Int) {
                        when (item) {
                            is DataBook -> {
                                val intent = Intent(context, SeriesActivity::class.java).apply {
                                    putExtra("sid", item.sid)
                                    putExtra("title", item.title)
                                }
                                startActivity(intent)
                            }
                            is DataNotice -> {
                                expandNoticeItem(position)
                            }
                        }
                    }
                })

                setOnDelteItemClickListener(object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                    override fun onItemClick(item: Any) {
                        if (item is DataBook) {
                            // remove request

                        }
                    }
                })

                (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnSubscribeClickListener(object : RecyclerViewBaseAdapter.OnSubscribeClickListener {
                    override fun onItemClick(item: Any, position: Int, selected: Boolean) {
                        if (item is DataBook) {
                            var action = if (selected) {
                                "S"
                            } else {
                                "C"
                            }
                            requestSubscribe(item.sid ?: "", action)
                        }
                    }
                })
            }
        }
    }

    private fun expandNoticeItem(position: Int) {
        viewModel.items.forEachIndexed { index, item ->
            if (item is DataNotice) {
                item.isSelect = index == position
            }
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    open fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        when (body) {
            is More -> {
                if (body.list.isNullOrEmpty()) {
                    showEmptyView()
                    return
                }
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    showMainView()
                    viewModel.items.addAll(it)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                // 기다무
                if (viewModel.isRefresh) return
                body.wop_term?.let {
                    if (viewModel is MainMenuViewModel) {
                        (viewModel as MainMenuViewModel).waitFreeTermItems = it
                        initWaitFreeTermRecyclerView()
//                        if (targetFragment is WaitFreeFragment) {
//                            (context as WaitFreeFragment).initWaitFreeTermRecyclerView()
//                        }
                    }
                }
            }
            is Gift -> {
                if (body.list.isNullOrEmpty()) {
                    showEmptyView()
                    return
                }
                body.list?.let {
                    showMainView()
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is News -> {
                if (body.list.isNullOrEmpty()) {
                    showEmptyView()
                    return
                }
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is Notice -> {
                if (body.list.isNullOrEmpty()) {
                    showEmptyView()
                    return
                }
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
            is Genre -> {
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            }
        }
        listCountTextView?.text = "${getString(R.string.str_total)} ${viewModel.items.size}"
    }

    fun initWaitFreeTermRecyclerView() {
        remainRecyclerView.apply {
            adapter = WaitFreeTermAdapter(
                (viewModel as MainMenuViewModel).waitFreeTermItems
            )
            (adapter as WaitFreeTermAdapter).setOnItemClickListener(object : WaitFreeTermAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is DataWaitFreeTerm) {
                        viewModel.isRefresh = true
                        viewModel.param2 = item.p_wop_term
                        (viewModel as MainMenuViewModel).waitFreeTermItems.forEachIndexed { index, dataWaitFreeTerm ->
                            dataWaitFreeTerm.isSelect = index == position
                        }
                        (adapter as WaitFreeTermAdapter).notifyDataSetChanged()
                        requestServer()
                    }
                }
            })
        }
    }

    override fun showEmptyView() {
        super.showEmptyView()
        emptyView?.apply {
            errorTitle.text = getString(R.string.msg_empty_gift)
            errorMsg.visibility = View.GONE
            goSeriesButton.visibility = View.GONE
        }
    }

    private fun requestSubscribe(sid: String, action: String) {
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

                        } else if ("202" == item.retcode) {
                            goCoinAlert(context)
                        } else {
                            if ("" != item.msg) {
                                CommonUtil.showToast(item.msg, context)
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