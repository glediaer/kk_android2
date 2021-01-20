package com.krosskomics.genre.fragment

import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.genre.activity.GenreDetailActivity

class GenreDetailFragment() : RecyclerViewBaseFragment() {
    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_more
        return R.layout.fragment_genre_detail
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