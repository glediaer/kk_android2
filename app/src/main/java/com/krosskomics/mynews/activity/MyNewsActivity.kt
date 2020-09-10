package com.krosskomics.mynews.activity

import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity

class MyNewsActivity : ToolbarTitleActivity() {
    private val TAG = "MyNewsActivity"

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