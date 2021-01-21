package com.krosskomics.coin.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.coin.viewmodel.TicketHistoryViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*


class TicketHistoryActivity : ToolbarTitleActivity() {
    private val TAG = "TicketHistoryActivity"

    public override val viewModel: TicketHistoryViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TicketHistoryViewModel(application) as T
            }
        }).get(TicketHistoryViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ticket_history
        return R.layout.activity_ticket_history
    }

    override fun initModel() {
        viewModel.listType = "useticket"
        super.initModel()
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_ticket_history))
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_ticket_history)
        super.initLayout()
    }
}