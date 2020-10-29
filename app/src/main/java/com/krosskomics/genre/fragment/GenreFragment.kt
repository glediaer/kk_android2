package com.krosskomics.genre.fragment

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
import com.krosskomics.common.model.More
import com.krosskomics.genre.viewmodel.GenreViewModel
import com.krosskomics.library.viewmodel.LibraryViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import kotlinx.android.synthetic.main.fragment_genre.*
import kotlinx.android.synthetic.main.view_topbutton.*

class GenreFragment : BaseFragment() {

    override val viewModel: GenreViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GenreViewModel(requireContext()) as T
            }
        }).get(GenreViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_genre_detail
        return R.layout.fragment_genre
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
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
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

    private fun setMainContentView(body: More) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        viewModel.totalPage = body.tot_pages
        body.list?.let {
            viewModel.items.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        listCountTextView.text = "${getString(R.string.str_total)} ${viewModel.items.size}"
    }
}