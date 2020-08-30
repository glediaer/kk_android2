package com.krosskomics.wait.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.wait.viewmodel.WaitViewModel

class WaitActivity : BaseActivity(), Observer<Any>, View.OnClickListener {
    private val TAG = "WaitActivity"

    private val viewModel: WaitViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WaitViewModel(application) as T
            }
        }).get(WaitViewModel::class.java)
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