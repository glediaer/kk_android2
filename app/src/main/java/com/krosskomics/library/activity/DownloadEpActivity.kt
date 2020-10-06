package com.krosskomics.library.activity

import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity

class DownloadEpActivity : ToolbarTitleActivity() {
    private val TAG = "DownloadEpActivity"

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_series
        return R.layout.activity_mynews
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_downloaded))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_downloaded)
        super.initLayout()
    }
}