package com.krosskomics.ranking.activity

import android.content.Intent
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
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
}