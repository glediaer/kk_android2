package com.krosskomics.settings.activity

import android.text.Editable
import android.text.TextWatcher
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_change_nickname.*

class ChangeNickNameActivity : ToolbarTitleActivity() {
    private val TAG = "ChangeNickNameActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_change_nickname
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
        currentNickNameEditText.setText(CommonUtil.read(context, CODE.LOCAL_Nickname, "").toString())
        newNickNameEditText.addTextChangedListener(object : TextWatcher {
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
                nicknameLengthTv.text = "${s.length} / 20"
                confirmButton.isEnabled = s.length in 1..20
            }
        })
        nicknameLengthTv.text = "0 / 20"
        confirmButton.setOnClickListener {
            // 닉네임 변경 api 호충
        }
    }
}