package com.krosskomics.search.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.model.Search
import com.krosskomics.search.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*

class SearchResultActivity : ToolbarTitleActivity() {
    private val TAG = "SearchResultActivity"

    public override val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(application) as T
            }
        }).get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_contents
        return R.layout.activity_search_result
    }

    override fun initModel() {
        intent?.apply {
            viewModel.keyword = extras?.getString("keyword").toString()
        }
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initToolbar()
        initMainView()
        initHeaderView()
        deleteAllImageView?.setOnClickListener {

        }
    }

    private fun initHeaderView() {
        searchEditText.setText(viewModel.keyword)
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

    override fun initTracker() {
        setTracker(getString(R.string.str_search))
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
                body.list?.let {
                    viewModel.items.addAll(it)
                }

                recyclerView?.adapter?.notifyDataSetChanged()
            }
        }
    }
}