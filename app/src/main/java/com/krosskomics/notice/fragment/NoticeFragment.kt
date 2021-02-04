package com.krosskomics.notice.fragment

import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment

class NoticeFragment : RecyclerViewBaseFragment() {
    private val TAG = "NoticeFragment"

    override fun getLayoutId(): Int {
        return R.layout.fragment_notice
    }
}