package com.krosskomics.coin.activity

import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_cash_history.*
import kotlinx.android.synthetic.main.activity_genre_detail.*
import kotlinx.android.synthetic.main.view_toolbar_black.*

class CashHistoryActivity : ToolbarViewPagerActivity() {
    private val TAG = "CashHistoryActivity"

    override var tabIndex = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_cash_history
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_cash_history))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_cash_history)
        adapterType = 2
        super.initLayout()

        val cash = CommonUtil.read(context, CODE.LOCAL_coin, "0")
        myCashTextView.text = getString(R.string.str_my_cash_format, cash)
    }

    override fun initModel() {
        tabIndex = intent?.getIntExtra("tabIndex", 0) ?: 0
        super.initModel()
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

    override fun initTabItems() {
        tabItems = listOf(getString(R.string.str_charged), getString(R.string.str_used))
    }
}