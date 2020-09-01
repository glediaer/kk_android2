package com.krosskomics.ranking.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ongoing.activity.OnGoingActivity
import com.krosskomics.ranking.viewmodel.RankingViewModel
import com.krosskomics.waitfree.activity.WaitFreeActivity

class RankingActivity : RecyclerViewBaseActivity() {
    private val TAG = "RankingActivity"

//    private val viewModel: RankingViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return RankingViewModel(application) as T
//            }
//        }).get(RankingViewModel::class.java)
//    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ranking
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_top))
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // tabview
            R.id.homeButton -> finish()
            R.id.onGoingButton -> {
                startActivity(Intent(context, OnGoingActivity::class.java))
                finish()
            }
            R.id.waitButton -> {
                startActivity(Intent(context, WaitFreeActivity::class.java))
                finish()
            }
            R.id.rankingButton -> {
                startActivity(Intent(context, RankingActivity::class.java))
            }
            R.id.jenreButton -> {
                startActivity(Intent(context, GenreActivity::class.java))
                finish()
            }
        }
    }
}