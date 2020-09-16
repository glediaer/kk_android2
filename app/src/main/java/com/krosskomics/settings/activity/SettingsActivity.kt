package com.krosskomics.settings.activity

import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.krosskomics.KJKomicsApp.Companion.LATEST_APP_VERSION_CODE
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : ToolbarTitleActivity() {
    private val TAG = "SettingsActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_settings
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_settings))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_settings)
        super.initLayout()

        setLoginSignView()
        setAppVersionView()
    }

    private fun setLoginSignView() {
        changeNicknameView.setOnClickListener(this)
        changeEmailView.setOnClickListener(this)
        changePwView.setOnClickListener(this)
        deleteAccountView.setOnClickListener(this)
        if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {

        } else {
            accountLoginView.visibility = View.GONE
            notificationView.visibility = View.GONE
            notificationUnderbar.visibility = View.GONE
            loginSignupView.setOnClickListener(this)
        }
    }

    private fun setAppVersionView() {
        var spannableString = SpannableString(CommonUtil.getAppVersion(context))
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        latestVerTextView.text = spannableString

        spannableString = SpannableString(CommonUtil.getAppVersion(context))
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        currentVerTextView.text = spannableString

        if (LATEST_APP_VERSION_CODE > CommonUtil.getVersionCode(context)) {
            latestVerTextView.setOnClickListener {
                // 팝업띄움
                showUpdateAlert()
            }
        }
    }

    /**
     * 버전 팝업
     */
    private fun showUpdateAlert() {
        try {
            val innerView =
                layoutInflater.inflate(R.layout.dialog_app_version, null)
            val dialog = initDialog(innerView)
            val tvMsg1 = innerView.findViewById<TextView>(R.id.currentVerTextView)
            val tvMsg2 = innerView.findViewById<TextView>(R.id.latestVerTextView)
            val btnConfirm =
                innerView.findViewById<TextView>(R.id.downloadTextView)

            tvMsg1.text = CommonUtil.getAppVersion(context)
            tvMsg2.text = CommonUtil.getAppVersion(context)

            btnConfirm.setOnClickListener {
                dialog.dismiss()
                CommonUtil.moveAppMarket(context, CODE.MARKET_URL)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.changeNicknameView -> startActivity(Intent(context, ChangeNickNameActivity::class.java))
            R.id.changeEmailView -> {
                if (CommonUtil.read(context, CODE.LOCAL_loginType, "") == CODE.LOGIN_TYPE_KROSS) {
                    startActivity(Intent(context, ChangeEmailActivity::class.java))
                } else {
                    CommonUtil.showToast(getString(R.string.msg_not_available_sns), context)
                }
            }
            R.id.changePwView -> {
                if (CommonUtil.read(context, CODE.LOCAL_loginType, "") == CODE.LOGIN_TYPE_KROSS) {
                    startActivity(Intent(context, ChangePwActivity::class.java))
                } else {
                    CommonUtil.showToast(getString(R.string.msg_not_available_sns), context)
                }
            }
        }
    }
}