package com.krosskomics.settings.activity

import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.krosskomics.KJKomicsApp.Companion.LATEST_APP_VERSION_CODE
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.model.Default
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.logout
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil
import com.krosskomics.util.ServerUtil.service
import kotlinx.android.synthetic.main.activity_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        initMainView()
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

    override fun initMainView() {
        pushNotiSwitch.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                requestPushNoti("S")
            } else {
                requestPushNoti("C")
            }
        }
        subscribeSwitch.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                requestUpdatePushNoti("S")
            } else {
                requestUpdatePushNoti("C")
            }
        }
    }

    /**
     * 푸시수신 설정
     */
    private fun requestPushNoti(action: String) {
        val api: Call<Default> = ServerUtil.service.setNotiSelector(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "push_notify", "", action, read(context, CODE.LOCAL_token, ""),
            read(context, CODE.LOCAL_Android_Id, "")
        )
        api.enqueue(object : Callback<Default> {
            override fun onResponse(
                call: Call<Default>,
                response: Response<Default>
            ) {
                try {
                    if (response.isSuccessful) {
                        val item: Default? = response.body()
                        if ("00" == item?.retcode) {
                            if ("C" == action) {
//                                actBinding.btnSwPush.setSelected(false)
                                write(context, CODE.LOCAL_RECIEVE_PUSH, "0")
                            } else if ("203" == item.retcode) {
                                goLoginAlert(context)
                            } else {
//                                actBinding.btnSwPush.setSelected(true)
                                write(context, CODE.LOCAL_RECIEVE_PUSH, "1")
                            }
                        } else {
                            if ("" != item?.msg) {
                                showToast(item?.msg, context)
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                    checkNetworkConnection(context, t, errorView)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 업데이트 푸시수신 설정
     */
    private fun requestUpdatePushNoti(action: String) {
        val api = service.setNotiSelector(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "push_update", "", action, read(context, CODE.LOCAL_token, ""), ""
        )
        api.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            if ("C" == action) {
//                                actBinding.btnNewEpisodePush.setSelected(false)
                                write(context, CODE.LOCAL_RECIEVE_UPDATE_PUSH, "0")
                            } else if ("203" == item.retcode) {
                                goLoginAlert(context)
                            } else {
//                                actBinding.btnNewEpisodePush.setSelected(true)
                                write(context, CODE.LOCAL_RECIEVE_UPDATE_PUSH, "1")
                            }
                        } else {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                    checkNetworkConnection(context, t, errorView)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun requestDeleteAccount() {
        val api = service.setDeleteUser(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "delete_account"
        )
        api.enqueue(object : Callback<Default> {
            override fun onResponse(
                call: Call<Default>,
                response: Response<Default>
            ) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                            logout(context)
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        } else if ("203" == item.retcode) {
                            goLoginAlert(context)
                        } else {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                    checkNetworkConnection(context, t, errorView)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
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
            R.id.deleteAccountView -> {
                requestDeleteAccount()
            }
        }
    }
}