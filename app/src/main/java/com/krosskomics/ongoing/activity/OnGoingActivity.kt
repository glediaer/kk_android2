package com.krosskomics.ongoing.activity

import android.content.Intent
import android.view.View
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.view_ongoing_date.*

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

    override fun initLayout() {
        super.initLayout()
        initDateView()
    }

    private fun initDateView() {
        monView.setOnClickListener {  }
        tueView.setOnClickListener {  }
        wedView.setOnClickListener {  }
        thuView.setOnClickListener {  }
        friView.setOnClickListener {  }
        satView.setOnClickListener {  }
        sunView.setOnClickListener {  }
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
            R.id.genreButton -> {
                startActivity(Intent(context, GenreActivity::class.java))
                finish()
            }
        }
    }
}