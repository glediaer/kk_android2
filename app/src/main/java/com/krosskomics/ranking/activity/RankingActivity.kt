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
import kotlinx.android.synthetic.main.view_title_section_ranking.*

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
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_ranking
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_top))
    }

    override fun initLayout() {
        viewModel.tabIndex = 3
        super.initLayout()

        moreImageView.setOnClickListener {
            startActivity(Intent(context, RankingDetailActivity::class.java))
        }
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
            R.id.genreButton -> {
                startActivity(Intent(context, GenreActivity::class.java))
                finish()
            }
        }
    }
}