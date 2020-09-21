package com.krosskomics.series.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Episode
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.adapter.SeriesAdapter
import com.krosskomics.series.viewmodel.SeriesViewModel
import com.krosskomics.util.CommonUtil
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
        viewModel.getCheckEpResponseLiveData().observe(this, this)
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun onChanged(t: Any?) {
        if (t is Episode) {
            when(viewModel.requestType) {
                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_A -> {
                    if ("00" == t.retcode) {
                        setMainContentView(t)
                    }
                }

                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_B -> {
                    when(t.retcode) {
                        "00" -> showEp()
                        "201" -> goLoginAlert(context)
                        "202" -> goCoinAlert(context)
                        "205" -> {
                            // 구매팝업
                            // ablestore == 1 소장구매 가능
                            // ablerent == 1 렌트 가능
                            showPurchaseRentDialog(t.episode);
                        }
                        else -> {
                            t.msg?.let {
                                CommonUtil.showToast(it, context)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showPurchaseRentDialog(episode: DataEpisode?) {

    }

    private fun showEp() {
        viewModel.item.let {
            val intent = Intent(context, ViewerActivity::class.java)
            val bundle = Bundle().apply {
                putString("title", it.ep_title)
                putString("eid", it.eid)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = SeriesAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataEpisode) {
                    viewModel.item = item
                    loadEpCheck()
                }
            }
        })
    }

    private fun loadEpCheck() {
        viewModel.requestCheckEp()
    }
}