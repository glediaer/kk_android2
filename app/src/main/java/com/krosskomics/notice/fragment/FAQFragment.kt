package com.krosskomics.notice.fragment

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.krosskomics.R
import com.krosskomics.common.data.DataTag
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.notice.adapter.FaqTagAdapter
import com.krosskomics.notice.viewmodel.NoticeViewModel
import kotlinx.android.synthetic.main.fragment_faq.*

class FAQFragment : RecyclerViewBaseFragment() {

    override val viewModel: NoticeViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return NoticeViewModel(requireContext()) as T
            }
        }).get(NoticeViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_faq
        return R.layout.fragment_faq
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun initLayout() {
        super.initLayout()
        initTagRecyclerView()
    }

    private fun initTagRecyclerView() {
        val items = arrayListOf(
            DataTag("Account", null, true),
            DataTag("Keys", null, false),
            DataTag("Contents", null, false),
            DataTag("Library", null, false),
            DataTag("Troubleshooting", null, false)
        )

        tagRecyclerView?.apply {
            visibility = View.VISIBLE
            adapter = FaqTagAdapter(items, R.layout.item_faq_tag)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            (adapter as FaqTagAdapter).setOnItemClickListener(object : FaqTagAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is DataTag) {
                        viewModel.tag = item.tag_title ?: ""
                        viewModel.isRefresh = true
                        requestServer()
                        resetTagItem(items, position)
                        adapter?.notifyDataSetChanged()
                    }
                }
            })
        }
    }

    fun resetTagItem(items: ArrayList<DataTag>, position: Int) {
        items.forEachIndexed { index, item ->
            item.isSelect = index == position
        }
    }
}