package com.krosskomics.home.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.home.fragment.MainFragment
import com.krosskomics.home.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.android.synthetic.main.view_toolbar.toolbar

class MainActivity : BaseActivity(), Observer<Any> {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel() as T
            }
        }).get(MainViewModel::class.java)
    }
    lateinit var drawerToggle: ActionBarDrawerToggle

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initModel() {
        viewModel.updateActivityCount.observe(this, this)
        viewModel.updateActivityCount2.observe(this, this)
    }

    override fun initLayout() {
        initHeaderView()
    }

    override fun requestServer() {

    }

    override fun onChanged(t: Any?) {

    }

    override fun onBackPressed() {
        if(dl_main_drawer_root.isDrawerOpen(GravityCompat.START)) {
            dl_main_drawer_root.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initHeaderView() {
        initToolbar()
        initDrawerView()
        initRecyclerView()
    }

    private fun initRecyclerView() {
//        recyclerView.adapter
    }

    private fun initDrawerView() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            dl_main_drawer_root,
            toolbar,
            R.string.common_open_on_phone,
            R.string.str_close
        )
        dl_main_drawer_root.addDrawerListener(drawerToggle)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher)
        }
    }
}
