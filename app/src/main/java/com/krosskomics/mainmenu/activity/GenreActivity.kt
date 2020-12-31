package com.krosskomics.mainmenu.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel

class GenreActivity : RecyclerViewBaseActivity() {
    private val TAG = "GenreActivity"

    public override val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainMenuViewModel(
                    application
                ) as T
            }
        }).get(MainMenuViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_genre
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_genre))
    }

    override fun initLayout() {
        viewModel.tabIndex = 4
        viewModel.param1 = "genre"
        super.initLayout()
    }
}