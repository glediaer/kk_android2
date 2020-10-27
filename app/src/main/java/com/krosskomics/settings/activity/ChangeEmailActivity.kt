package com.krosskomics.settings.activity

import android.text.Editable
import android.text.TextWatcher
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.model.Default
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.activity_change_email.*
import retrofit2.Call
import retrofit2.Response

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
            if (newEmailEditText.text.isNullOrEmpty()) return@setOnClickListener
            // 이메일 변경 api 호충
            requestChangeProfile(newEmailEditText.text.toString())
        }
    }

    private fun requestChangeProfile(email: String) {
        val api = ServerUtil.service.setUserProfile(CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "change_email", email, CommonUtil.read(context, CODE.LOCAL_email, ""))
        api.enqueue(object : retrofit2.Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item?.retcode) {
                            CommonUtil.write(context, CODE.LOCAL_email, email)
                        }
                        if ("" != item?.msg) {
                            CommonUtil.showToast(item?.msg, context)
                        }
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                try {
                    checkNetworkConnection(context, t, errorView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}