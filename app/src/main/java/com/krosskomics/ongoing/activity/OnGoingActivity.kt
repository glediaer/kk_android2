package com.krosskomics.ongoing.activity

import android.content.Intent
import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.waitfree.activity.WaitFreeActivity
import kotlinx.android.synthetic.main.view_ongoing_date.*

class OnGoingActivity : RecyclerViewBaseActivity() {
    private val TAG = "OnGoingActivity"

    lateinit var dateViewItems: ArrayList<View>
    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ongoing
        return R.layout.activity_ongoing
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_ongoing))
    }

    override fun initLayout() {
        viewModel.tabIndex = 1
        super.initLayout()
        initDateView()
    }

    private fun initDateView() {
        dateViewItems = arrayListOf(monView, tueView, wedView, thuView, friView, satView, sunView)
        dateViewItems.forEach { dateView ->
            dateView.setOnClickListener {
                resetDateViewItems()
                it.isSelected = true
                requestServer()
            }
        }
    }

    private fun resetDateViewItems() {
        dateViewItems.forEach { it.isSelected = false }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // tabview
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