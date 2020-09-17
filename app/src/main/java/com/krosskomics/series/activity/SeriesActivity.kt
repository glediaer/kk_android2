package com.krosskomics.series.activity

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.series.adapter.SeriesAdapter
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Episode
import com.krosskomics.common.model.More
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.viewmodel.SeriesViewModel
import com.krosskomics.viewer.activity.ViewerActivity
import kotlinx.android.synthetic.main.activity_main_content.*

class SeriesActivity : ToolbarTitleActivity() {
    private val TAG = "SeriesActivity"

    override val viewModel: SeriesViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SeriesViewModel(application) as T
            }
        }).get(SeriesViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_series
        return R.layout.activity_series
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_series))
    }

    override fun initModel() {
        intent?.apply {
            toolbarTitleString = extras?.getString("title").toString()
            viewModel.sid = extras?.getString("sid").toString()
        }
        super.initModel()
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun onChanged(t: Any?) {
        if (t is Episode) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = SeriesAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataEpisode) {
                    val intent = Intent(context, ViewerActivity::class.java).apply {
                        putExtra("sid", item.sid)
                        putExtra("title", item.title)
                    }
                    startActivity(intent)
                }
            }
        })
    }
}