package com.krosskomics.waitfree.activity

import android.content.Intent
import android.view.View
import com.google.android.gms.analytics.HitBuilders
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ongoing.activity.OnGoingActivity
import com.krosskomics.ranking.activity.RankingActivity

class WaitFreeActivity : RecyclerViewBaseActivity() {
    private val TAG = "WaitFreeActivity"

//    private val viewModel: WaitViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return WaitViewModel(application) as T
//            }
//        }).get(WaitViewModel::class.java)
//    }

    override fun getLayoutId(): Int {
        return R.layout.activity_waitfree
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_waitforfree))
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