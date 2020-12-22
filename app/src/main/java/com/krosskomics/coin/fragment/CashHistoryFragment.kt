package com.krosskomics.coin.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.viewmodel.CashHistoryViewModel
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.User
import com.krosskomics.library.activity.DownloadEpActivity
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.library.viewmodel.LibraryViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.FileUtils
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import kotlinx.android.synthetic.main.view_mytoon_category.*
import kotlinx.android.synthetic.main.view_mytoon_filter.*
import kotlinx.android.synthetic.main.view_network_state.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CashHistoryFragment : RecyclerViewBaseFragment() {

    override val viewModel: CashHistoryViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CashHistoryViewModel(requireContext()) as T
            }
        }).get(CashHistoryViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_cash_history
        return R.layout.fragment_cash_history
    }

    override fun initLayout() {
        initMainView()
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                recyclerViewItemLayoutId
            )
    }
}