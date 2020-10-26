package com.krosskomics.ranking.activity

import android.content.Intent
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.ranking.adapter.RankingAdapter
import com.krosskomics.series.activity.SeriesActivity
import kotlinx.android.synthetic.main.activity_main_content.*

class RankingDetailActivity : ToolbarTitleActivity() {
    private val TAG = "RankingDetailActivity"

//    private val viewModel: RankingViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return RankingViewModel(application) as T
//            }
//        }).get(RankingViewModel::class.java)
//    }

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

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = RankingAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
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