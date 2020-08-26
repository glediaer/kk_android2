package com.krosskomics.home.activity

import android.os.Bundle
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.home.fragment.MainFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFragment(MainFragment())
    }
}
