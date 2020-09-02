package com.krosskomics.settings.activity

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

class SettingsActivity : RecyclerViewBaseActivity() {
    private val TAG = "SettingsActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_event
    }

    override fun initTracker() {
        // Get tracker.
        val tracker = (application as KJKomicsApp).getTracker(KJKomicsApp.TrackerName.APP_TRACKER)
        tracker?.setScreenName(getString(R.string.str_event))
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun onClick(v: View?) {
        when(v?.id) {
        }
    }
}