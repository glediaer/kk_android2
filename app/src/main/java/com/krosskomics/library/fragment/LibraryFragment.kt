package com.krosskomics.library.fragment

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.fragment.BaseFragment
import com.krosskomics.common.model.More
import com.krosskomics.common.viewmodel.FragmentBaseViewModel
import com.krosskomics.library.viewmodel.LibraryViewModel
import com.krosskomics.ongoing.adapter.OnGoingAdapter
import com.krosskomics.series.viewmodel.SeriesViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import kotlinx.android.synthetic.main.view_mytoon_category.*
import kotlinx.android.synthetic.main.view_mytoon_filter.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_topbutton.*

class LibraryFragment : BaseFragment() {
    var currentCategory = 0 // all, unlock, download

    override val viewModel: LibraryViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LibraryViewModel(requireContext()) as T
            }
        }).get(LibraryViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_genre_detail
        return R.layout.fragment_library
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
        if (t is More) {
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
        initCategory()
        initfilter()
        initRecyclerView()
        topButton.setOnClickListener {
            recyclerView?.smoothScrollToPosition(0)
        }
    }

    private fun initfilter() {
        filterRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.recentRadioButton -> {
                    requestServer()
                }
                R.id.scribeRadioButton -> {
                    requestServer()
                }
            }
        }
    }

    private fun initCategory() {
        allTextView.isSelected = true
        unlockTextView.isSelected = false
        downloadTextView.isSelected = false
        allTextView.setOnClickListener {
            resetCategory()
            allTextView.isSelected = true
            activity?.toolbarTrash?.visibility = View.VISIBLE
            filterView.visibility = View.VISIBLE
            networkStateView.visibility = View.GONE
            currentCategory = 0
            viewModel.items.clear()
            requestServer()
        }
        unlockTextView.setOnClickListener {
            resetCategory()
            unlockTextView.isSelected = true
            activity?.toolbarTrash?.visibility = View.GONE
            filterView.visibility = View.GONE
            networkStateView.visibility = View.GONE
            currentCategory = 1
            viewModel.items.clear()
            requestServer()
        }
        downloadTextView.setOnClickListener {
            resetCategory()
            downloadTextView.isSelected = true
            activity?.toolbarTrash?.visibility = View.VISIBLE
            filterView.visibility = View.GONE
            networkStateView.visibility = View.VISIBLE
            currentCategory = 2
            viewModel.items.clear()
            requestServer()
        }
    }

    private fun resetCategory() {
        allTextView.isSelected = false
        unlockTextView.isSelected = false
        downloadTextView.isSelected = false
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
        recyclerView.adapter = OnGoingAdapter(viewModel.items, recyclerViewItemLayoutId)
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

    private fun setMainContentView(body: More) {
        if (body.list.isNullOrEmpty()) {
            showEmptyView()
        }
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        viewModel.totalPage = body.tot_pages
        body.list?.let {
            showRecyclerView()
            viewModel.items.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
            activity?.apply {
                toolbarTrash?.setOnClickListener { _ ->
                    toolbarTrash.visibility = View.GONE
                    toolbarDone.visibility = View.VISIBLE
                    it.forEach {
                        it.isCheckVisible = true
                        it.isChecked = true
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                toolbarDone?.setOnClickListener { _ ->
                    toolbarTrash.visibility = View.VISIBLE
                    toolbarDone.visibility = View.GONE
                    it.forEach {
                        it.isCheckVisible = false
                        it.isChecked = false
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun showEmptyView() {
        super.showEmptyView()
        emptyView?.apply {
            when(currentCategory) {
                0 -> {
                    errorTitle.text = getString(R.string.msg_empty_series)
                    errorMsg.visibility = View.GONE
                    goSeriesButton.text = getString(R.string.str_go_to_series)
                }
                1 -> {
                    errorTitle.text = getString(R.string.msg_empty_unlock_ep)
                    errorMsg.text = getString(R.string.msg_empty_unlock_ep2)
                    errorMsg.visibility = View.VISIBLE
                    goSeriesButton.text = getString(R.string.str_go_to_series)
                }
                1 -> {
                    errorTitle.text = getString(R.string.msg_empty_unlock_ep)
                    errorMsg.text = getString(R.string.msg_empty_unlock_ep2)
                    errorMsg.visibility = View.VISIBLE
                    goSeriesButton.text = getString(R.string.str_go_to_download)
                }
            }
        }
    }
}