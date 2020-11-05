package com.krosskomics.notice.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.notice.viewmodel.NoticeViewModel

class NoticeFragment : RecyclerViewBaseFragment() {

    override val viewModel: NoticeViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return NoticeViewModel(requireContext()) as T
            }
        }).get(NoticeViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_mynews
        return R.layout.fragment_notice
    }

    override fun requestServer() {
        viewModel.requestMain()
    }
}