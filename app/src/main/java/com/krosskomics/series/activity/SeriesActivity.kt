package com.krosskomics.series.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.analytics.GoogleAnalytics
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.series.viewmodel.SeriesViewModel

class SeriesActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "SeriesActivity"

    private val viewModel: SeriesViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SeriesViewModel(application) as T
            }
        }).get(SeriesViewModel::class.java)
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
        return R.layout.activity_series
    }

    override fun initModel() {
        viewModel.getInitSetResponseLiveData().observe(this, this)
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
            R.id.btn_signup -> {
            }
        }
    }
}