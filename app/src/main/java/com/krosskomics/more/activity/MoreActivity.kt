package com.krosskomics.more.activity

import android.content.Intent
import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.util.CommonUtil
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.activity_ongoing.*
import kotlinx.android.synthetic.main.view_ongoing_date.*
import kotlinx.android.synthetic.main.view_topbutton.*

class MoreActivity : RecyclerViewBaseActivity() {
    private val TAG = "MoreActivity"

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_contents
        return R.layout.activity_more
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_more))
    }
}