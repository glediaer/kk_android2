package com.krosskomics.search.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.search.viewmodel.SearchViewModel

class SearchActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "SearchActivity"

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(application) as T
            }
        }).get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ongoing
    }

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initToolbar()
        initMainView()
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
            R.id.btn_signup -> {
            }
        }
    }
}