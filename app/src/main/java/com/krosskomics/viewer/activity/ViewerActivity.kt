package com.krosskomics.viewer.activity

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Episode
import com.krosskomics.viewer.adapter.ViewerAdapter
import com.krosskomics.viewer.viewmodel.ViewerViewModel
import kotlinx.android.synthetic.main.activity_main_content.*
import java.util.*

class ViewerActivity : ToolbarTitleActivity() {
    private val TAG = "ViewerActivity"

    override val viewModel: ViewerViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ViewerViewModel(application) as T
            }
        }).get(ViewerViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_viewer
        return R.layout.activity_viewer
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_viewer))
    }

    override fun initModel() {
        intent?.apply {
            toolbarTitleString = extras?.getString("title").toString()
            viewModel.item.eid = extras?.getString("eid").toString()
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

    override fun setMainContentView(body: Any) {
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        if (body is Episode) {
            body.episode?.ep_contents?.let {
                makeList(it, true)
//                viewModel.items.addAll(it)
//                recyclerView?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = ViewerAdapter(viewModel.items, recyclerViewItemLayoutId, context)
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

    private fun makeList(urls: String, isReload: Boolean) {
        val arr_url = urls.split(",".toRegex()).toTypedArray()
            viewModel.items.addAll(
                Arrays.asList(
                    *arr_url
                )
            )
        recyclerView?.adapter?.notifyDataSetChanged()
    }
}