package com.krosskomics.search.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.Search
import com.krosskomics.search.adapter.SearchTagAdapter
import com.krosskomics.search.viewmodel.SearchViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.setAppsFlyerEvent
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*
import java.util.*

class SearchActivity : ToolbarTitleActivity() {
    private val TAG = "SearchActivity"

    public override val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(application) as T
            }
        }).get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_search_recent
        return R.layout.activity_search
    }

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
        viewModel.recentItems = CommonUtil.getStringArrayPref(this, CODE.RECENT_SEARCH)
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_search))
    }

    override fun initLayout() {
        initToolbar()
        initMainView()
        initHeaderView()
    }

    private fun initHeaderView() {
        searchImageView.setOnClickListener {
            if (it.isSelected) {
                viewModel.keyword = ""
            } else {
                if (searchEditText.text.toString().isNullOrEmpty()) return@setOnClickListener

                viewModel.keyword = searchEditText.text.toString()

                if (viewModel.recentItems.size >= 5) {
                    viewModel.recentItems.removeAt(0)
                }
                viewModel.recentItems.add(viewModel.keyword)
                CommonUtil.setStringArrayPref(context, CODE.RECENT_SEARCH, viewModel.recentItems)

                val eventValue: MutableMap<String, Any?> =
                    HashMap()
                eventValue["af_search_string"] = viewModel.keyword
                setAppsFlyerEvent(this, "af_search", eventValue)

                CommonUtil.downKeyboard(this, searchEditText)
            }
            viewModel.isRefresh = true
            requestServer()
        }

        deleteAllImageView.setOnClickListener {
            viewModel.recentItems.clear()
            CommonUtil.setStringArrayPref(context, CODE.RECENT_SEARCH, viewModel.recentItems)
            recentRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbar.toolbarTrash.visibility = View.GONE
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun onChanged(t: Any?) {
        if (t is Search) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        }
    }

    override fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
            viewModel.tagItems.clear()
        }
        when (body) {
            is Search -> {
                viewModel.recentItems.let {
                    recentRecyclerView.adapter?.notifyDataSetChanged()
                    defaultView.visibility = View.VISIBLE
                    resultView.visibility = View.GONE

                    searchView.layoutParams.height = CommonUtil.dpToPx(context, 203)
                    searchView.setPadding(
                        CommonUtil.dpToPx(context, 20),
                        CommonUtil.dpToPx(context, 60),
                        CommonUtil.dpToPx(context, 20),
                        CommonUtil.dpToPx(context, 20)
                    )
                    searchImageView.isSelected = false
                    if (it.size > 0) {
                        recentView.visibility = View.VISIBLE
                    } else {
                        recentView.visibility = View.GONE
                    }
                }
                body.tag?.let {
                    viewModel.tagItems.addAll(it.tag_text!!)
                    defaultView.visibility = View.VISIBLE
                    resultView.visibility = View.GONE
                }
                body.list?.let {
                    viewModel.items.addAll(it)
                    defaultView.visibility = View.GONE
                    resultView.visibility = View.VISIBLE
                    searchEditText.setText(viewModel.keyword)

                    searchView.layoutParams.height = CommonUtil.dpToPx(context, 60)
                    searchView.setPadding(
                        CommonUtil.dpToPx(context, 20),
                        CommonUtil.dpToPx(context, 0),
                        CommonUtil.dpToPx(context, 20),
                        CommonUtil.dpToPx(context, 0)
                    )
                    searchImageView.isSelected = true
                    if (it.isNullOrEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyDataTextView.text =
                            getString(R.string.msg_empty_search_data_format, viewModel.keyword)
                        emptyDataTextView.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyDataTextView.visibility = View.GONE
                    }
                }

                recyclerView?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initRecyclerView() {
        recentRecyclerView?.layoutManager = LinearLayoutManager(context)
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
        }
        tagRecyclerView?.layoutManager = flexboxLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(context)
        initRecyclerViewAdapter()
    }

    override fun initRecyclerViewAdapter() {
        recentRecyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.recentItems,
                recyclerViewItemLayoutId
            )

        tagRecyclerView.adapter =
            SearchTagAdapter(
                viewModel.tagItems,
                R.layout.item_recommend_keyword
            )

        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                R.layout.item_more
            )
        (recentRecyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnDelteItemClickListener(object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                override fun onItemClick(item: Any) {
                    if (item is String) {
                        viewModel.recentItems.remove(item)
                        CommonUtil.setStringArrayPref(
                            context,
                            CODE.RECENT_SEARCH,
                            viewModel.recentItems
                        )
                    }
                    notifyDataSetChanged()
                }
            })
        }

        (recentRecyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is String) {
                        val eventValue: MutableMap<String, Any?> =
                            HashMap()
                        eventValue["af_search_string"] = viewModel.keyword
                        setAppsFlyerEvent(context, "af_search", eventValue)

                        viewModel.keyword = item
                        viewModel.isRefresh = true

                        if (viewModel.recentItems.size >= 5) {
                            viewModel.recentItems.removeAt(0)
                        }
                        viewModel.recentItems.add(viewModel.keyword)
                        CommonUtil.setStringArrayPref(
                            context,
                            CODE.RECENT_SEARCH,
                            viewModel.recentItems
                        )

                        requestServer()
                    }
                }
            })
        }

        (tagRecyclerView.adapter as SearchTagAdapter).apply {
            setOnItemClickListener(object : SearchTagAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is String) {
                        val eventValue: MutableMap<String, Any?> =
                            HashMap()
                        eventValue["af_search_string"] = viewModel.keyword
                        setAppsFlyerEvent(context, "af_search", eventValue)

                        viewModel.keyword = item
                        viewModel.isRefresh = true

                        if (viewModel.recentItems.size >= 5) {
                            viewModel.recentItems.removeAt(0)
                        }
                        viewModel.recentItems.add(viewModel.keyword)
                        CommonUtil.setStringArrayPref(
                            context,
                            CODE.RECENT_SEARCH,
                            viewModel.recentItems
                        )

                        requestServer()
                    }
                }
            })
        }

        (recyclerView?.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object :
            RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?, position: Int) {
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