package com.krosskomics.settings.activity

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import kotlinx.android.synthetic.main.activity_change_pw.*

class ChangePwActivity : ToolbarTitleActivity() {
    private val TAG = "ChangePwActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_change_pw
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
        newPwEditText.addTextChangedListener(object : TextWatcher {
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
                confirmButton.isEnabled = (s.length > 6 && verifyPwEditText.text.toString().length > 5)
            }
        })

        verifyPwEditText.addTextChangedListener(object : TextWatcher {
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
                confirmButton.isEnabled = (s.length > 6 && newPwEditText.text.toString().length > 5)
            }
        })
        hideImageView.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                newPwEditText.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                newPwEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
        confirmButton.setOnClickListener {
            // 비밀번호 변경 api 호충
        }
    }
}