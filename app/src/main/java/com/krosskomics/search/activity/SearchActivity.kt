package com.krosskomics.search.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.krosskomics.common.data.DataRecentSearch
import com.krosskomics.common.model.Search
import com.krosskomics.search.adapter.SearchTagAdapter
import com.krosskomics.search.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*

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
            if (searchEditText.text.toString().isNullOrEmpty()) return@setOnClickListener

            val intent = Intent(context, SearchResultActivity::class.java)
            val bundle = Bundle().apply {
                putString("keyword", searchEditText.text.toString())
            }
            intent.putExtras(bundle)
            startActivity(intent)
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
        }
        when (body) {
            is Search -> {
                body.banner?.let {
                    viewModel.items.addAll(it)
                }
                body.tag?.let {
                    viewModel.tagItems.addAll(it.tag_text!!)
                }

                recyclerView?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
        }
        tagRecyclerView?.layoutManager = flexboxLayoutManager
        initRecyclerViewAdapter()
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                recyclerViewItemLayoutId
            )

        tagRecyclerView.adapter =
            SearchTagAdapter(
                viewModel.tagItems,
                R.layout.item_recommend_keyword
            )
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnDelteItemClickListener(object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                override fun onItemClick(item: Any) {
                    if (item is DataRecentSearch) {
                        Log.e(TAG, "onItemClick")
                    }
                }
            })
        }

        (tagRecyclerView.adapter as SearchTagAdapter).apply {
            setOnItemClickListener(object : SearchTagAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is String) {
                        val intent = Intent(context, SearchResultActivity::class.java)
                        val bundle = Bundle().apply {
                            putString("keyword", item)
                        }
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
            })
        }
    }
}