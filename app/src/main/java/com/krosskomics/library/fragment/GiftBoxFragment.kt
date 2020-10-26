package com.krosskomics.library.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.fragment.BaseFragment
import com.krosskomics.common.model.Gift
import com.krosskomics.library.viewmodel.GiftBoxViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_topbutton.*

class GiftBoxFragment : BaseFragment() {

    override val viewModel: GiftBoxViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GiftBoxViewModel(requireContext()) as T
            }
        }).get(GiftBoxViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_mynews
        return R.layout.fragment_giftbox
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
        if (t is Gift) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            } else {
                t.msg?.let {
                    CommonUtil.showToast(it, context)
                }
            }
        }
    }

    private fun initMainView() {
        initRecyclerView()
        topButton.setOnClickListener {
            recyclerView?.smoothScrollToPosition(0)
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

    private fun initRecyclerViewAdapter() {
        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                recyclerViewItemLayoutId
            )
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
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

            setOnDelteItemClickListener(object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                override fun onItemClick(item: Any) {
                    if (item is DataBook) {
                        // remove request

                    }
                }
            })
        }
    }

    private fun setMainContentView(body: Gift) {
        if (body.list.isNullOrEmpty()) {
            showEmptyView()
            return
        }
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        viewModel.totalPage = body.tot_pages
        body.list?.let {
            showMainView()
            viewModel.items.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
            activity?.apply {
                toolbarTrash?.setOnClickListener { _ ->
                    toolbarTrash.visibility = View.GONE
                    toolbarDone.visibility = View.VISIBLE
                    it.forEach {
//                        it.isCheckVisible = true
//                        it.isChecked = true
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                toolbarDone?.setOnClickListener { _ ->
                    toolbarTrash.visibility = View.VISIBLE
                    toolbarDone.visibility = View.GONE
                    it.forEach {
//                        it.isCheckVisible = false
//                        it.isChecked = false
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
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
}