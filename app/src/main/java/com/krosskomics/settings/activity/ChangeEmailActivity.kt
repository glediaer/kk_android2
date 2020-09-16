package com.krosskomics.settings.activity

import android.text.Editable
import android.text.TextWatcher
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_change_email.*

class ChangeEmailActivity : ToolbarTitleActivity() {
    private val TAG = "ChangeEmailActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_change_email
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_account))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_account)
        super.initLayout()

        initView()
    }

    private fun initView() {
        newEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                confirmButton.isEnabled = !CommonUtil.emailCheck(s.toString()) && s.length >= 6
            }
        })
        confirmButton.setOnClickListener {
            // 이메일 변경 api 호충
        }
    }
}