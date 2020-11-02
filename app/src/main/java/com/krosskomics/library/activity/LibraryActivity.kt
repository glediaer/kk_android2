package com.krosskomics.library.activity

import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarViewPagerActivity
import kotlinx.android.synthetic.main.activity_genre_detail.*
import kotlinx.android.synthetic.main.view_toolbar_black.*

class LibraryActivity : ToolbarViewPagerActivity() {
    private val TAG = "LibraryActivity"

    override var tabIndex = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_library
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_library))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_library)
        adapterType = 1
        super.initLayout()
    }

    override fun initModel() {
        tabIndex = intent?.getIntExtra("tabIndex", 0) ?: 0
        super.initModel()
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
    }

    override fun initTabItems() {
        tabItems = listOf(getString(R.string.str_my_comic), getString(R.string.str_gift_box))
    }
}