package com.krosskomics.search.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.search.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*

class SearchResultActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "SearchActivity"

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(application) as T
            }
        }).get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search
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
    }

    private fun initHeaderView() {
        searchImageView.setOnClickListener {
            
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

    override fun initTracker() {
    }

    override fun onChanged(t: Any?) {
    }

    private fun initMainView() {
    }

    override fun onClick(v: View?) {
        when(v?.id) {

        }
    }
}