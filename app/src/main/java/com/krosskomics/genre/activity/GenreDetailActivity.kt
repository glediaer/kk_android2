package com.krosskomics.genre.activity

import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import kotlinx.android.synthetic.main.activity_genre_detail.*

class GenreDetailActivity : ToolbarViewPagerActivity() {
    private val TAG = "GenreDetailActivity"

//    private val viewModel: RankingViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return RankingViewModel(application) as T
//            }
//        }).get(RankingViewModel::class.java)
//    }

    override var tabIndex: Int
        get() = super.tabIndex
        set(value) {}

    override fun getLayoutId(): Int {
        return R.layout.activity_genre_detail
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_genre))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_genre)
        adapterType = 0
        super.initLayout()
        initViewPager()
    }

    override fun initTabItems() {
        tabItems = listOf("Romance", "Drama", "Action", "Horror", "Comic", "Romance", "Drama")
    }

}