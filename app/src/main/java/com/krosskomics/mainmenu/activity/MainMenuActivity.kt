package com.krosskomics.mainmenu.activity

import android.content.Intent
import android.view.View
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.mainmenu.fragment.GenreFragment
import com.krosskomics.mainmenu.fragment.OnGoingFragment
import com.krosskomics.mainmenu.fragment.RankingFragment
import com.krosskomics.mainmenu.fragment.WaitFreeFragment
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.search.activity.SearchActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.view_mainmenu_tab.*

class MainMenuActivity : RecyclerViewBaseActivity() {
    private val TAG = "MainMenuActivity"

    private var tabButtonItems: List<View>? = null

    override fun requestServer() {}

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_ongoing
        return R.layout.activity_main_menu
    }

    override fun initTracker() {}

    override fun initLayout() {
        super.initLayout()
        replaceFragmentTabIndex()
    }

    override fun initModel() {
        viewModel.tabIndex = intent.getIntExtra("tabIndex", 1)
        tabButtonItems = listOf(onGoingButton, waitButton, rankingButton, genreButton)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.searchImageView -> startActivity(Intent(context, SearchActivity::class.java))
            R.id.giftboxImageView -> {
                if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                    intent = Intent(context, LibraryActivity::class.java)
                    startActivity(intent)
                } else {
                    goLoginAlert(context)
                }
            }
            // tabview
            R.id.homeButton -> finish()
            R.id.onGoingButton -> {
                viewModel.tabIndex = 1
                replaceFragmentTabIndex()
            }
            R.id.waitButton -> {
                viewModel.tabIndex = 2
                replaceFragmentTabIndex()
            }
            R.id.rankingButton -> {
                viewModel.tabIndex = 3
                replaceFragmentTabIndex()
            }
            R.id.genreButton -> {
                viewModel.tabIndex = 4
                replaceFragmentTabIndex()
            }
        }
    }

    fun replaceFragmentTabIndex() {
        when (viewModel.tabIndex) {
            1 -> {
                replaceFragment(OnGoingFragment(), R.id.fragment_container)
                mainMenuTabView.setBackgroundResource(R.drawable.kk_gradient_tabmenu_ongoing_rect)
                ongoingTextView.visibility = View.VISIBLE
                waitTextView.visibility = View.GONE
                rankingTextView.visibility = View.GONE
                genreTextView.visibility = View.GONE
            }
            2 -> {
                replaceFragment(WaitFreeFragment(), R.id.fragment_container)
                mainMenuTabView.setBackgroundResource(R.drawable.kk_gradient_tabmenu_waitfree_rect)
                ongoingTextView.visibility = View.GONE
                waitTextView.visibility = View.VISIBLE
                rankingTextView.visibility = View.GONE
                genreTextView.visibility = View.GONE
            }
            3 -> {
                replaceFragment(RankingFragment(), R.id.fragment_container)
                mainMenuTabView.setBackgroundResource(R.drawable.kk_gradient_tabmenu_ranking_rect)
                ongoingTextView.visibility = View.GONE
                waitTextView.visibility = View.GONE
                rankingTextView.visibility = View.VISIBLE
                genreTextView.visibility = View.GONE
            }
            4 -> {
                replaceFragment(GenreFragment(), R.id.fragment_container)
                mainMenuTabView.setBackgroundResource(R.drawable.kk_gradient_tabmenu_genre_rect)
                ongoingTextView.visibility = View.GONE
                waitTextView.visibility = View.GONE
                rankingTextView.visibility = View.GONE
                genreTextView.visibility = View.VISIBLE
            }
        }
        resetTabButtonItem()
    }

    private fun resetTabButtonItem() {
        tabButtonItems?.forEachIndexed { index, relativeLayout ->
            relativeLayout.isSelected = index + 1 == viewModel.tabIndex
        }
    }
}