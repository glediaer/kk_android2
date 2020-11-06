package com.krosskomics.ranking.activity

import android.content.Intent
import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.ranking.adapter.RankingAdapter
import com.krosskomics.series.activity.SeriesActivity
import kotlinx.android.synthetic.main.activity_main_content.recyclerView
import kotlinx.android.synthetic.main.activity_ranking_detail.*
import kotlinx.android.synthetic.main.view_ranking_floating_dim.*

class RankingDetailActivity : ToolbarTitleActivity() {
    private val TAG = "RankingDetailActivity"

//    private val viewModel: RankingViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return RankingViewModel(application) as T
//            }
//        }).get(RankingViewModel::class.java)
//    }

    var currentFilterIndex = 0

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_ranking_detail
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_ranking))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_ranking)
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
                requestServer()
                filterTextView.text = getString(R.string.str_all)
            }
            maleRankingView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
                currentFilterIndex = 1
                requestServer()
                filterTextView.text = getString(R.string.str_male)
            }
            femaleRankingView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
                currentFilterIndex = 2
                requestServer()
                filterTextView.text = getString(R.string.str_female)
            }
            closeImageView.setOnClickListener {
                sortImageView.visibility = View.VISIBLE
                visibility = View.GONE
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = RankingAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?, position: Int) {
                if (item is DataBook) {
                    val intent = Intent(context, SeriesActivity::class.java).apply {
                        putExtra("sid", item.sid)
                        putExtra("title", item.title)
                    }
                    startActivity(intent)
                }
            }
        })
    }
}