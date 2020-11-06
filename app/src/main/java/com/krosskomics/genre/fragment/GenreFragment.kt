package com.krosskomics.genre.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.genre.viewmodel.GenreViewModel

class GenreFragment : RecyclerViewBaseFragment() {

    override val viewModel: GenreViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GenreViewModel(requireContext()) as T
            }
        }).get(GenreViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_more
        return R.layout.fragment_genre
    }
}