package com.krosskomics.common.activity

import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
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