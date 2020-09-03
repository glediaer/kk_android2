package com.krosskomics.coin.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.analytics.GoogleAnalytics
import com.krosskomics.R
import com.krosskomics.coin.viewmodel.CoinViewModel
import com.krosskomics.common.activity.BaseActivity

class CoinActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "CoinActivity"

    private val viewModel: CoinViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CoinViewModel(application) as T
            }
        }).get(CoinViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        GoogleAnalytics.getInstance(this).reportActivityStart(this)
    }

    override fun onStop() {
        super.onStop()
        GoogleAnalytics.getInstance(this).reportActivityStop(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ongoing
    }

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initToolbar()
        initMainView()
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun initTracker() {
    }

    override fun onChanged(t: Any?) {
    }

    private fun initMainView() {
    }

    override fun onClick(v: View?) {
        when(v?.id) {
        }
    }
}