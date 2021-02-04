package com.krosskomics.notice.fragment

import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment

class FAQFragment : RecyclerViewBaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_faq
    }

//    override fun requestServer() {
//        viewModel.requestMain()
//    }
//
//    override fun initLayout() {
//        super.initLayout()
//        initTagRecyclerView()
//    }

//    private fun initTagRecyclerView() {
//        val items = arrayListOf(
//            DataTag("Account", null, true),
//            DataTag("Keys", null, false),
//            DataTag("Contents", null, false),
//            DataTag("Library", null, false),
//            DataTag("Troubleshooting", null, false)
//        )
//
//        tagRecyclerView?.apply {
//            visibility = View.VISIBLE
//            adapter = FaqTagAdapter(items, R.layout.item_faq_tag)
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            (adapter as FaqTagAdapter).setOnItemClickListener(object : FaqTagAdapter.OnItemClickListener {
//                override fun onItemClick(item: Any?, position: Int) {
//                    if (item is DataTag) {
//                        viewModel.tag = item.tag_title ?: ""
//                        viewModel.isRefresh = true
//                        requestServer()
//                        resetTagItem(items, position)
//                        adapter?.notifyDataSetChanged()
//                    }
//                }
//            })
//        }
//    }
//
//    fun resetTagItem(items: ArrayList<DataTag>, position: Int) {
//        items.forEachIndexed { index, item ->
//            item.isSelect = index == position
//        }
//    }
}