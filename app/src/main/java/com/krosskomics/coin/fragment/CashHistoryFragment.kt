package com.krosskomics.coin.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.coin.viewmodel.CashHistoryViewModel
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.genre.activity.GenreDetailActivity
import kotlinx.android.synthetic.main.fragment_genre.recyclerView

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

    override fun initModel() {
        arguments?.let {
            viewModel.listType = it.getString("listType").toString()
        }
        super.initModel()
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