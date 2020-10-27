package com.krosskomics.settings.activity

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.model.Default
import com.krosskomics.util.CODE
import com.krosskomics.util.CODE.PW_MIN_LENGTH
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.activity_change_pw.*
import retrofit2.Call
import retrofit2.Response

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
                confirmButton.isEnabled = (s.length >= PW_MIN_LENGTH && verifyPwEditText.text.toString().length >= PW_MIN_LENGTH &&
                        currentPwEditText.text.toString().length >= PW_MIN_LENGTH)
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
                confirmButton.isEnabled = (s.length >= PW_MIN_LENGTH && newPwEditText.text.toString().length >= PW_MIN_LENGTH &&
                        currentPwEditText.text.toString().length >= PW_MIN_LENGTH)
            }
        })

        currentPwEditText.addTextChangedListener(object : TextWatcher {
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
                confirmButton.isEnabled = (s.length >= PW_MIN_LENGTH && newPwEditText.text.toString().length >= PW_MIN_LENGTH &&
                        verifyPwEditText.text.toString().length >= PW_MIN_LENGTH)
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
            requestChangePw(verifyPwEditText.text.toString(), currentPwEditText.text.toString())
        }
    }

    private fun requestChangePw(password: String, currentPassword: String) {
        showProgress(context)
        val api = ServerUtil.service.setPassword(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "change_pass", password, currentPassword)
        api.enqueue(object : retrofit2.Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            finish()
                        } else {
                            if ("" != item.msg) {
                                CommonUtil.showToast(item.msg, context)
                            }
                        }
                        hideProgress()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    hideProgress()
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                hideProgress()
                try {
                    checkNetworkConnection(context, null, errorView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}