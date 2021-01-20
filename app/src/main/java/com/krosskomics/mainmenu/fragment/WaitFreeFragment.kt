package com.krosskomics.mainmenu.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.data.DataWaitFreeTerm
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.genre.activity.GenreDetailActivity
import com.krosskomics.mainmenu.adapter.WaitFreeTermAdapter
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel
import kotlinx.android.synthetic.main.fragment_waitfree.*

class WaitFreeFragment : RecyclerViewBaseFragment() {

    override val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainMenuViewModel(requireContext()) as T
            }
        }).get(MainMenuViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_waitfree
        return R.layout.fragment_waitfree
    }

    override fun initLayout() {
        viewModel.tabIndex = 2
        viewModel.param1 = "waitorpay"
        super.initLayout()
    }

    override fun initModel() {
        arguments?.let {
            viewModel.listType = it.getString("listType").toString()
            val position = it.getInt("position")
            viewModel.param2 = (context as GenreDetailActivity).lazyTabItems[position].p_genre
        }
        super.initModel()
    }
}