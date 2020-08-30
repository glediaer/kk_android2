package com.krosskomics.login.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_login
import kotlinx.android.synthetic.main.activity_login.btn_signup
import kotlinx.android.synthetic.main.view_signup_bottomsheet.*
import kotlinx.android.synthetic.main.view_signup_bottomsheet.view.*

class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_login
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
    }

    private fun initMainView() {
        btn_signup.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    private fun showBottomSheet(view: Int) {
        val dialogView = layoutInflater.inflate(view, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        dialog.show()

        when(view) {
            R.layout.view_signup_bottomsheet -> {
                dialogView.btn_forgot_password.setOnClickListener {
                    showBottomSheet(R.layout.view_forgot_password_bottomsheet)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_signup -> {
                showBottomSheet(R.layout.view_signup_bottomsheet)
            }
        }
    }
}