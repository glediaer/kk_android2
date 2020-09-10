package com.krosskomics.genre.activity

import android.content.Intent
import com.google.android.material.tabs.TabLayoutMediator
import com.krosskomics.R
import com.krosskomics.book.activity.BookActivity
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.genre.adapter.GenrePagerAdapter
import com.krosskomics.ranking.adapter.RankingAdapter
import kotlinx.android.synthetic.main.activity_genre_detail.*
import kotlinx.android.synthetic.main.activity_main_content.recyclerView

class GenreDetailActivity : ToolbarTitleActivity() {
    private val TAG = "GenreDetailActivity"

//    private val viewModel: RankingViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return RankingViewModel(application) as T
//            }
//        }).get(RankingViewModel::class.java)
//    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ranking
        return R.layout.activity_genre_detail
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_genre))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_genre)
        super.initLayout()
        initViewPager()
    }

    private fun initViewPager() {
        val tabList = listOf("Romance", "Drama", "Action", "Horror", "Comic", "Romance", "Drama")
        viewPager.adapter = GenrePagerAdapter(this, tabList.size)
        TabLayoutMediator(tabLayout, viewPager){ tab,position->
            tab.text = tabList[position]
        }.attach()
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = RankingAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataBook) {
                    val intent = Intent(context, BookActivity::class.java).apply {
                        putExtra("sid", item.sid)
                        putExtra("title", item.title)
                    }
                    startActivity(intent)
                }
            }
        })
    }
}