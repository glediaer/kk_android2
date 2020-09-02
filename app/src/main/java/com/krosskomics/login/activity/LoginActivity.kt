package com.krosskomics.login.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.data.DataLogin
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.Login
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.login.viewmodel.LoginViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.emailCheck
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil.setRetrofitServer
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.view_forgot_password_bottomsheet.view.*
import kotlinx.android.synthetic.main.view_login_bottomsheet.view.*
import kotlinx.android.synthetic.main.view_signup_bottomsheet.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginActivity : BaseActivity(), View.OnClickListener, Observer<Any> {
    lateinit var dialogView: View
    lateinit var bottomSheetDialog: BottomSheetDialog
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LoginViewModel(application) as T
            }
        }).get(LoginViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initModel() {
        viewModel.repository.pageType = intent?.getStringExtra("pageType").toString()
        viewModel.getLoginResponseLiveData().observe(this, this)
        viewModel.getFindPasswordResponseLiveData().observe(this, this)
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

    override fun onChanged(t: Any?) {
        if (t == null) {
            checkNetworkConnection(context, t, errorView)
            return
        }
        if (t is Login) {
            when(t.retcode) {
                CODE.SUCCESS, "104" -> {
                    t.user?.let { user ->
                        //내부 저장소에 정보를 기록
                        write(context, CODE.LOCAL_id, viewModel.repository.id)
                        write(context, CODE.LOCAL_loginType, viewModel.repository.loginType)
                        write(context, CODE.LOCAL_loginYn, "Y")
                        write(context, CODE.LOCAL_user_no, user.u_token)
                        write(context, CODE.LOCAL_coin, user.user_coin)
                        write(context, CODE.Local_oprofile, viewModel.repository.oprofile)
                        write(context, CODE.LOCAL_Nickname, user.nick)
                        write(context, CODE.LOCAL_email, user.email)

                        user.u_token?.let {
                            write(context, CODE.LOCAL_ENC_USER_NO, it)
                            setRetrofitServer(context)
                        }
                        KJKomicsApp.LOGIN_SEQ = user.login_seq
                        user.new_gift?.let {
                            if ("1" == it) {
                                KJKomicsApp.IS_GET_NEW_GIFT = true
                            }
                        }
                        KJKomicsApp.PROFILE_PICTURE = user.profile_picture.toString()

                        showToast(
                            getString(R.string.msg_success_login),
                            this@LoginActivity
                        )
                        //메인에 로그인되었다고 알린다.
                        val intent = Intent(CODE.LB_MAIN)
                        intent.putExtra("message", CODE.MSG_NAV_REFRESH)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

                        FirebaseAnalytics.getInstance(context)
                            .logEvent(FirebaseAnalytics.Event.LOGIN, Bundle())

                        // 메인으로 이동
                        runBlocking {
                            launch {
                                delay(400)
                                startActivity(Intent(context, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
                "102" -> {
                    // 소셜로그인으로 로그인 추가 데이터 입력 처리
                    //                // 추가 데이터 수집
                    if (CODE.LOGIN_TYPE_FACEBOOK == viewModel.repository.loginType
                        || CODE.LOGIN_TYPE_GOOGLE == viewModel.repository.loginType) {
                        KJKomicsApp.LOGIN_DATA = DataLogin()
                        val intent =
//                            Intent(this@LoginActivity, SelectGenderActivity::class.java)
                        startActivity(intent)
                    } else {
                        if (!t.msg.isNullOrEmpty()) {
                            showToast(t.msg, this@LoginActivity)
                        }
                    }
                }
                else -> {
                    if (!t.msg.isNullOrEmpty()) {
                        showToast(t.msg, this@LoginActivity)
                    }
                    runBlocking {
                        launch {
                            delay(400)
                            startActivity(Intent(context, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        } else if (t is Default) {
            when(t.retcode) {
                CODE.SUCCESS -> {
                    dialogView.apply {
                        defaultView.visibility = View.GONE
                        successView.visibility = View.VISIBLE
                        succesEmailTextView.text = "[ ${forgotEmailEditTextView.text} ]"

                        successBackImageView.setOnClickListener { bottomSheetDialog.dismiss() }
                        okButton.setOnClickListener { bottomSheetDialog.dismiss() }
                    }
                }
                else -> {
                    if (!t.msg.isNullOrEmpty()) {
                        showToast(t.msg, this@LoginActivity)
                    }
                }
            }
        }
    }

    private fun initMainView() {
        showBottomSheet(R.layout.view_login_bottomsheet)
    }

    private fun showBottomSheet(view: Int) {
        dialogView = layoutInflater.inflate(view, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()

        when(view) {
            R.layout.view_login_bottomsheet -> {
                dialogView.apply {
                    emailEditTextView.addTextChangedListener(object : TextWatcher {
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
                            goLoginButton.isEnabled =
                                !("" == passwordEditTextView.text.toString() || !emailCheck(s.toString()))
                        }
                    })
                    passwordEditTextView.addTextChangedListener(object : TextWatcher {
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
                            goLoginButton.isEnabled =
                                !("" == passwordEditTextView.text.toString() ||
                                        !emailCheck(emailEditTextView.text.toString()))
                        }
                    })
                    goLoginButton.setOnClickListener {
                        // 로그인 요청
                        if (passwordEditTextView.text.length < 6) {
                            showToast(
                                getString(R.string.msg_fail_password_length),
                                this@LoginActivity
                            )
                            return@setOnClickListener
                        }
                        viewModel.repository.apply {
                            id = emailEditTextView.text.toString().trim()
                            password = passwordEditTextView.text.toString().trim()
                            loginType = CODE.LOGIN_TYPE_KROSS
                            oprofile = ""
                        }
                        viewModel.requestLogin()
                    }
                    forgotPwTextView.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        showBottomSheet(R.layout.view_forgot_password_bottomsheet)
                    }
                }
            }
            R.layout.view_signup_bottomsheet -> {
                dialogView.btn_forgot_password.setOnClickListener {
                    showBottomSheet(R.layout.view_forgot_password_bottomsheet)
                }
            }
            R.layout.view_forgot_password_bottomsheet -> {
                dialogView.apply {
                    backImageView.setOnClickListener { bottomSheetDialog.dismiss() }
                    forgotEmailEditTextView.addTextChangedListener(object : TextWatcher {
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
                            sendLinkButton.isEnabled = !emailCheck(s.toString())
                        }
                    })
                    sendLinkButton.setOnClickListener {
                        // 비밀번호 찾기 요청
                        viewModel.repository.id = forgotEmailEditTextView.text.toString().trim()
                        viewModel.requestFindPassword()
                    }
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