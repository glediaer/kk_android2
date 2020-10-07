package com.krosskomics.library.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.library.viewmodel.DownloadViewModel
import kotlinx.android.synthetic.main.view_toolbar_black.*

class DownloadViewerActivity : ToolbarTitleActivity() {
    private val TAG = "DownloadViewerActivity"

    public override val viewModel: DownloadViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DownloadViewModel(application) as T
            }
        }).get(DownloadViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_download_ep
        return R.layout.activity_download_ep
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_downloaded))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_downloaded)
        super.initLayout()
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
    }
}