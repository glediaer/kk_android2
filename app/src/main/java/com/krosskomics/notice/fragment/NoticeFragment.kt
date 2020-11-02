package com.krosskomics.notice.fragment

import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment

class NoticeFragment : RecyclerViewBaseFragment() {
    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_contents
        return R.layout.fragment_notice
    }
}