package com.krosskomics.notice.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.book.activity.BookActivity
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.More
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ongoing.adapter.OnGoingAdapter
import com.krosskomics.ongoing.viewmodel.OnGoingViewModel
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.util.CommonUtil
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_content.toolbar
import kotlinx.android.synthetic.main.view_action_item.view.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.android.synthetic.main.view_toolbar.view.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_topbutton.*

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