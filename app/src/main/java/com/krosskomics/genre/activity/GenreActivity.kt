package com.krosskomics.genre.activity

import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity

class GenreActivity : RecyclerViewBaseActivity() {
    private val TAG = "GenreActivity"

//    private val viewModel: GenreViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return GenreViewModel(application) as T
//            }
//        }).get(GenreViewModel::class.java)
//    }
    override fun getLayoutId(): Int {
        return R.layout.activity_genre
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_genre))
    }

    override fun initLayout() {
        viewModel.tabIndex = 4
        super.initLayout()
    }
}