package com.krosskomics.mainmenu.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel
import kotlinx.android.synthetic.main.activity_ranking_detail.*
import kotlinx.android.synthetic.main.view_ranking_floating_dim.*

class RankingActivity : RecyclerViewBaseActivity() {
    private val TAG = "RankingActivity"

    public override val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainMenuViewModel(
                    application
                ) as T
            }
        }).get(MainMenuViewModel::class.java)
    }

    var currentFilterIndex = 0

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_ranking
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_top))
    }

    override fun initLayout() {
        viewModel.tabIndex = 3
        viewModel.param1 = "ranking"
        super.initLayout()
    }

    override fun initMainView() {
        super.initMainView()

        sortImageView.setOnClickListener {
            sortImageView.visibility = View.GONE
            rankingDimView.visibility = View.VISIBLE
            when (currentFilterIndex) {
                0 -> {
                    allRankingView.visibility = View.GONE
                    maleRankingView.visibility = View.VISIBLE
                    femaleRankingView.visibility = View.VISIBLE
                }
                1 -> {
                    allRankingView.visibility = View.VISIBLE
                    maleRankingView.visibility = View.GONE
                    femaleRankingView.visibility = View.VISIBLE
                }
                2 -> {
                    allRankingView.visibility = View.VISIBLE
                    maleRankingView.visibility = View.VISIBLE
                    femaleRankingView.visibility = View.GONE
                }
            }
        }
        rankingDimView?.apply {
            setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                it.visibility = View.GONE
            }
            allRankingView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
                currentFilterIndex = 0
                viewModel.isRefresh = true
                viewModel.param2 = "a"
                requestServer()
                filterTextView.text = getString(R.string.str_all)
            }
            maleRankingView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
                currentFilterIndex = 1
                viewModel.isRefresh = true
                viewModel.param2 = "m"
                requestServer()
                filterTextView.text = getString(R.string.str_male)
            }
            femaleRankingView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
                currentFilterIndex = 2
                viewModel.isRefresh = true
                viewModel.param2 = "f"
                requestServer()
                filterTextView.text = getString(R.string.str_female)
            }
            closeImageView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
            }
        }
    }
}