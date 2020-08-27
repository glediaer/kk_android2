package com.krosskomics.common.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.krosskomics.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
//        initLayoutInflate()
        initModel()
        initLayout()
        requestServer()
    }

    // fragment 추가
    fun addFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }

    abstract fun getLayoutId(): Int
//    abstract fun initLayoutInflate()
    abstract fun initModel()
    abstract fun initLayout()
    abstract fun requestServer()
}