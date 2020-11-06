package com.krosskomics.more.activity

import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity

class MoreActivity : ToolbarTitleActivity() {
    private val TAG = "MoreActivity"

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_more
        return R.layout.activity_more
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_more))
    }

    override fun initModel() {
        toolbarTitleString = intent?.extras?.getString("title").toString()
        super.initModel()
    }
}