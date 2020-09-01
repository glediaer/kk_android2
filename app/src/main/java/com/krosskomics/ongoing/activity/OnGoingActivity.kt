package com.krosskomics.ongoing.activity

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
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.More
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ongoing.adapter.OnGoingAdapter
import com.krosskomics.ongoing.viewmodel.OnGoingViewModel
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.view_main_tab.*
import kotlinx.android.synthetic.main.view_topbutton.*

class OnGoingActivity : RecyclerViewBaseActivity() {
    private val TAG = "OnGoingActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_ongoing
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_ongoing))
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // tabview
            R.id.homeButton -> finish()
            R.id.onGoingButton -> startActivity(Intent(context, OnGoingActivity::class.java))
            R.id.waitButton -> {
                startActivity(Intent(context, WaitFreeActivity::class.java))
                finish()
            }
            R.id.rankingButton -> {
                startActivity(Intent(context, RankingActivity::class.java))
                finish()
            }
            R.id.jenreButton -> {
                startActivity(Intent(context, GenreActivity::class.java))
                finish()
            }
        }
    }
}