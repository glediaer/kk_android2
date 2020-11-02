package com.krosskomics.notice.activity

import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_genre_detail.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.view_action_item.view.*
import kotlinx.android.synthetic.main.view_toolbar.view.*

class NoticeActivity : ToolbarViewPagerActivity() {
    private val TAG = "NoticeActivity"

    override var tabIndex = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_notice
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_notice))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_notice)
        adapterType = 3
        super.initLayout()
    }

    override fun initModel() {
        tabIndex = intent?.getIntExtra("tabIndex", 0) ?: 0
        super.initModel()
    }

    override fun initToolbar() {
        super.initToolbar()

        toolbar.actionItem.apply {
            visibility = View.VISIBLE
            giftboxImageView.visibility = View.GONE
            searchImageView.visibility = View.GONE
            contactImageView.visibility = View.VISIBLE
            contactImageView.setOnClickListener {
                CommonUtil.sendEmail(context)
            }
        }
    }

    override fun initTabItems() {
        tabItems = listOf(getString(R.string.str_notice), getString(R.string.str_faq))
    }
}