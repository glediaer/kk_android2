package com.krosskomics.common.activity

import com.google.android.material.tabs.TabLayoutMediator
import com.krosskomics.R
import com.krosskomics.genre.adapter.GenrePagerAdapter
import kotlinx.android.synthetic.main.activity_genre_detail.*

abstract class ToolbarViewPagerActivity : ToolbarTitleActivity() {
    private val TAG = "GenreDetailActivity"


//    private val viewModel: RankingViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return RankingViewModel(application) as T
//            }
//        }).get(RankingViewModel::class.java)
//    }

    var adapterType = 0     // 0: 장르상세, 1: 라이브러리
    lateinit var tabItems: List<String>

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_genre_detail
    }

    override fun initLayout() {
        super.initLayout()
        initTabItems()
        initViewPager()
    }

    protected fun initViewPager() {
        viewPager.adapter = GenrePagerAdapter(this, tabItems.size, adapterType)
        TabLayoutMediator(tabLayout, viewPager){ tab, position->
            tab.text = tabItems[position]
        }.attach()
    }

    abstract fun initTabItems()
}