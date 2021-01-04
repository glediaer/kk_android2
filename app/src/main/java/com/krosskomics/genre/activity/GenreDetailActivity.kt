package com.krosskomics.genre.activity

import androidx.lifecycle.ViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import com.krosskomics.common.adapter.CommonPagerAdapter
import com.krosskomics.common.data.DataGenre
import com.krosskomics.common.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.activity_genre_detail.*

class GenreDetailActivity : ToolbarViewPagerActivity() {
    private val TAG = "GenreDetailActivity"

    internal var lazyTabItems: ArrayList<DataGenre> = arrayListOf()

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
    }

    override fun initTabItems() {}

    fun initTabItems(items: ArrayList<DataGenre>?) {
        items?.let {
            lazyTabItems = it
        }
        initViewPager()
//        initTabPosition()
    }

    override fun initModel() {
        intent?.extras?.apply {
            viewModel.listType = getString("listType").toString()
            viewModel.param2 = getString("more_param").toString()
        }
        super.initModel()
    }

    override fun initViewPager() {
        viewPager.adapter = CommonPagerAdapter(
            this,
            lazyTabItems.size,
            adapterType
        )
        TabLayoutMediator(tabLayout, viewPager){ tab, position->
            tab.text = lazyTabItems[position].dp_genre
            tab.tag = lazyTabItems[position].p_genre
        }.attach()
    }
}