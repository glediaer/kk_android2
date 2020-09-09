package com.krosskomics.login.activity

import android.content.Intent
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.util.CODE
import kotlinx.android.synthetic.main.activity_login_intro.*

class LoginIntroActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_login_intro
    }

    override fun initModel() {
    }

    override fun initLayout() {
        initToolbar()
        initMainView()
    }

    override fun requestServer() {
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_login))
    }

    private fun initMainView() {
        signupButton.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java).apply {
            putExtra("pageType", CODE.SIGNUP_MODE)
        }) }
        loginButton.setOnClickListener { startActivity(Intent(context, LoginActivity::class.java).apply {
            putExtra("pageType", CODE.LOGIN_MODE)
        }) }

        inviteTextView.setOnClickListener {
            // 앱 다운로드 url
            shareUrl(CODE.APP_DOWNLOAD_URL)
        }
    }
}