package com.krosskomics.waitfree.activity

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.genre.activity.GenreActivity
import com.krosskomics.ongoing.activity.OnGoingActivity
import com.krosskomics.ranking.activity.RankingActivity
import kotlinx.android.synthetic.main.activity_waitfree.*

class WaitFreeActivity : RecyclerViewBaseActivity() {
    private val TAG = "WaitFreeActivity"

//    private val viewModel: WaitViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return WaitViewModel(application) as T
//            }
//        }).get(WaitViewModel::class.java)
//    }
    lateinit var dateViewItems: ArrayList<TextView>

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_waitfree
        return R.layout.activity_waitfree
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_waitforfree))
    }

    override fun initLayout() {
        viewModel.tabIndex = 2
        super.initLayout()
        initDateView()
    }

    private fun initDateView() {
        dateViewItems = arrayListOf(oneDateTextView, twoDateTextView, threeDateTextView,
            fourDateTextView, fiveDateTextView)
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