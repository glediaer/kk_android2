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

class GenreFragment : RecyclerViewBaseFragment() {

    override val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainMenuViewModel(requireContext()) as T
            }
        }).get(MainMenuViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_genre
    }

    override fun initLayout() {
        viewModel.tabIndex = 4
        viewModel.param1 = "genre"
        super.initLayout()
    }
}