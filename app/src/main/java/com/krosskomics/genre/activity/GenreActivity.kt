package com.krosskomics.genre.activity

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
import com.krosskomics.genre.viewmodel.GenreViewModel
import com.krosskomics.ongoing.activity.OnGoingActivity
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.waitfree.activity.WaitFreeActivity

class GenreActivity : RecyclerViewBaseActivity() {
    private val TAG = "GenreActivity"

//    private val viewModel: GenreViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return GenreViewModel(application) as T
//            }
//        }).get(GenreViewModel::class.java)
//    }
    override fun getLayoutId(): Int {
        return R.layout.activity_genre
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_genre))
    }

    override fun initLayout() {
        viewModel.tabIndex = 4
        super.initLayout()
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
                finish()
            }
            R.id.genreButton -> {
                startActivity(Intent(context, GenreActivity::class.java))
            }
        }
    }
}