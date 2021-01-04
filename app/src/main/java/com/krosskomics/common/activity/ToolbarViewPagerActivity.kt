package com.krosskomics.common.activity

import com.google.android.material.tabs.TabLayoutMediator
import com.krosskomics.R
import com.krosskomics.common.adapter.CommonPagerAdapter
import kotlinx.android.synthetic.main.activity_genre_detail.*

abstract class ToolbarViewPagerActivity : ToolbarTitleActivity() {
    private val TAG = "GenreDetailActivity"

    var adapterType = 0     // 0: 장르상세, 1: 라이브러리, 3: notice
    lateinit var tabItems: List<String>
    open var tabIndex = 0

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_genre_detail
    }

    override fun initLayout() {
        super.initLayout()
        initTabItems()
        initViewPager()
        initTabPosition()
    }

    protected open fun initViewPager() {
        viewPager.adapter = CommonPagerAdapter(
            this,
            tabItems.size,
            adapterType
        )
        TabLayoutMediator(tabLayout, viewPager){ tab, position->
            tab.text = tabItems[position]
        }.attach()
    }

    fun initTabPosition() {
        viewPager.currentItem = tabIndex
    }

    abstract fun initTabItems()
}