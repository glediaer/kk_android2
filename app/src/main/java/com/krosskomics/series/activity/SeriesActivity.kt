package com.krosskomics.series.activity

import android.content.Intent
import com.krosskomics.R
import com.krosskomics.series.adapter.SeriesAdapter
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import kotlinx.android.synthetic.main.activity_main_content.*

class SeriesActivity : ToolbarTitleActivity() {
    private val TAG = "SeriesActivity"

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_series
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_series))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_series)
        super.initLayout()
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = SeriesAdapter(viewModel.items, recyclerViewItemLayoutId, context)
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