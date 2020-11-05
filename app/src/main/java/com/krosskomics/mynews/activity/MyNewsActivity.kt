package com.krosskomics.mynews.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.mynews.viewmodel.MyNewsViewModel

class MyNewsActivity : ToolbarTitleActivity() {
    private val TAG = "MyNewsActivity"

    public override val viewModel: MyNewsViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MyNewsViewModel(application) as T
            }
        }).get(MyNewsViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_mynews
        return R.layout.activity_mynews
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_my_news))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_my_news)
        super.initLayout()
    }
}