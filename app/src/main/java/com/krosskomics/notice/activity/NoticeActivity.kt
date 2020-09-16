package com.krosskomics.notice.activity

import android.content.Intent
import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_main_content.toolbar
import kotlinx.android.synthetic.main.view_action_item.view.*
import kotlinx.android.synthetic.main.view_toolbar.view.*

class NoticeActivity : ToolbarViewPagerActivity() {
    private val TAG = "NoticeActivity"

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

    override fun initToolbar() {
        super.initToolbar()

        toolbar.actionItem.apply {
            visibility = View.VISIBLE
            giftboxImageView.visibility = View.GONE
            searchImageView.visibility = View.GONE
            contactImageView.visibility = View.VISIBLE
            contactImageView.setOnClickListener {
                Intent(Intent.ACTION_SEND).apply {
                    type = "plain/Text"
                    putExtra(Intent.EXTRA_EMAIL, getString(R.string.str_kk_email))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.str_faq))
                    putExtra(Intent.EXTRA_TEXT, "앱 버전 (AppVersion):" + CommonUtil.getAppVersion(context) + "\n기기명 (Device):\n안드로이드 OS (Android OS):\n내용 (Content)")
                    type = "message/rfc822"

                    startActivity(this)
                }
            }
        }
    }

    override fun initTabItems() {
        tabItems = listOf(getString(R.string.str_notice), getString(R.string.str_faq))
    }
}