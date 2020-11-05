package com.krosskomics.common.fragment

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.Gift
import com.krosskomics.common.model.More
import com.krosskomics.common.model.News
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.activity_more.recyclerView
import kotlinx.android.synthetic.main.view_empty_library.*
import kotlinx.android.synthetic.main.view_topbutton.*

open class RecyclerViewBaseFragment : BaseFragment() {
    override val viewModel: FragmentBaseViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FragmentBaseViewModel(requireContext()) as T
            }
        }).get(FragmentBaseViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_contents
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
        if (t is Gift) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            } else {
                t.msg?.let {
                    CommonUtil.showToast(it, context)
                }
            }
        } else if (t is More) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            } else {
                t.msg?.let {
                    CommonUtil.showToast(it, context)
                }
            }
        } else if (t is News) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            } else {
                t.msg?.let {
                    CommonUtil.showToast(it, context)
                }
            }
        }
    }

    open fun initMainView() {
        initRecyclerView()
        if (nestedScrollView != null) {
            nestedScrollView.scrollTo(0, 0)
        } else {
            topButton?.setOnClickListener {
                recyclerView?.layoutManager?.scrollToPosition(0)
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
        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                recyclerViewItemLayoutId
            )
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is More) {
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

    open fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        when (body) {
            is More -> {
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    showMainView()
                    viewModel.items.addAll(it)
                    recyclerView.adapter?.notifyDataSetChanged()
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
                viewModel.totalPage = body.tot_pages
                body.list?.let {
                    viewModel.items.addAll(it)
                    recyclerView?.adapter?.notifyDataSetChanged()
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