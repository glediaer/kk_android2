package com.krosskomics.common.activity

import android.view.View
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.android.synthetic.main.view_toolbar.view.*

open class ToolbarTitleActivity : RecyclerViewBaseActivity() {
    private val TAG = "ToolbarTitleActivity"

    override fun initToolbar() {
        super.initToolbar()
        toolbar.apply {
            actionItem.visibility = View.GONE
            toolbarTitle.visibility = View.VISIBLE
            toolbarTitle.text = toolbarTitleString
        }
    }
}