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
        intent?.extras?.apply {
            toolbarTitleString = getString("title").toString()
            viewModel.listType = getString("listType").toString()
            viewModel.param2 = getString("more_param").toString()
        }
        super.initModel()
    }
}