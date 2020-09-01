package com.krosskomics.common.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.login.activity.LoginActivity
import kotlinx.android.synthetic.main.view_toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@BaseActivity
        setContentView(getLayoutId())
        initModel()
        initLayout()
        requestServer()
        initTracker()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    // fragment 추가
//    fun addFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//
//        fragmentTransaction.add(R.id.fragment_container, fragment)
//        fragmentTransaction.commitAllowingStateLoss()
//    }

    abstract fun getLayoutId(): Int
    abstract fun initModel()
    abstract fun initLayout()
    abstract fun requestServer()
    abstract fun initTracker()

    fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.icon_back)
        }
    }

    open fun goLoginAlert(context: Context?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    open fun getCurrentItem(recyclerView: RecyclerView): Int {
        return (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }
}