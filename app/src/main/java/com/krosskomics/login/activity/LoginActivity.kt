package com.krosskomics.login.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.data.DataAge
import com.krosskomics.common.data.DataLoginGenre
import com.krosskomics.common.data.DataLogin
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.Login
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.login.adapter.*
import com.krosskomics.login.viewmodel.LoginViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.emailCheck
import com.krosskomics.util.CommonUtil.setAppsFlyerEvent
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil.setRetrofitServer
import com.krosskomics.webview.WebViewActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.view_forgot_password_bottomsheet.view.*
import kotlinx.android.synthetic.main.view_login_bottomsheet.view.*
import kotlinx.android.synthetic.main.view_network_error.view.*
import kotlinx.android.synthetic.main.view_signup_info_age.view.*
import kotlinx.android.synthetic.main.view_signup_info_bottomsheet.view.*
import kotlinx.android.synthetic.main.view_signup_info_gender.view.*
import kotlinx.android.synthetic.main.view_signup_info_genre.view.*
import kotlinx.android.synthetic.main.view_signup_info_language.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class LoginActivity : BaseActivity(), View.OnClickListener, Observer<Any> {
    private val TAG = "LoginActivity"
    lateinit var dialogView: View
    lateinit var bottomSheetDialog: BottomSheetDialog

    // facebook
    lateinit var callbackManager: CallbackManager

    // 구글로그인
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LoginViewModel(application) as T
            }
        }).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        overridePendingTransition(R.anim.slide_up, R.anim.stay)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(window) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                // set an slide transition
                enterTransition = Slide(Gravity.BOTTOM)
                exitTransition = Slide(Gravity.TOP)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (::callbackManager.isInitialized) callbackManager.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id)
                handleGoogleSignIn(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                if (BuildConfig.DEBUG) Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun handleGoogleSignIn(account: GoogleSignInAccount?) {
        account?.let {
            viewModel.repository.apply {
                loginType = CODE.LOGIN_TYPE_GOOGLE
                snsToken = it.idToken
                id = it.id
                oprofile = it.email

                if (viewModel.repository.pageType == CODE.LOGIN_MODE) {
                    requestSNSLogin()
                } else {
                    // 회원 정보 요청
                    bottomSheetDialog.dismiss()
                    showBottomSheet(R.layout.view_signup_info_bottomsheet)
                }
            }
        }
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

    override fun requestServer() {}

    override fun initTracker() {
        setTracker(getString(R.string.str_login))
    }

    override fun initErrorView() {
        errorView.refreshButton.setOnClickListener {
            requestServer()
        }
    }

    override fun onChanged(t: Any?) {
        if (t == null) {
            checkNetworkConnection(context, t, errorView)
            return
        }
        if (t is Login) {
            if (viewModel.repository.pageType == CODE.LOGIN_MODE) {
                when (t.retcode) {
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
                            val eventValue: MutableMap<String, Any?> =
                                HashMap()
                            setAppsFlyerEvent(this, "af_login", eventValue)
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
                            || CODE.LOGIN_TYPE_GOOGLE == viewModel.repository.loginType
                        ) {
                            KJKomicsApp.LOGIN_DATA = DataLogin()
                            viewModel.repository.pageType = CODE.SIGNUP_MODE
                            // 회원 정보 요청
                            bottomSheetDialog.dismiss()
                            showBottomSheet(R.layout.view_signup_info_bottomsheet)
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
                    }
                }
            } else {
                when (t.retcode) {
                    CODE.SUCCESS -> {
                        viewModel.repository.pageType = CODE.SIGNUP_MODE
                        requestLogin()
                        var gaLogMethod = ""
                        if (viewModel.repository.loginType == CODE.LOGIN_TYPE_KROSS) {
                            gaLogMethod = "email"
                        } else {
                            if (viewModel.repository.loginType == CODE.LOGIN_TYPE_FACEBOOK) {
                                gaLogMethod = "facebook"
                            } else if (viewModel.repository.loginType == CODE.LOGIN_TYPE_GOOGLE) {
                                gaLogMethod = "google"
                            }
                        }
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, gaLogMethod)
                        FirebaseAnalytics.getInstance(context)
                            .logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                        val eventValue: MutableMap<String, Any?> =
                            HashMap()
                        eventValue["af_registration_method"] = gaLogMethod
                        setAppsFlyerEvent(
                            context,
                            "af_complete_registration",
                            eventValue
                        )
                    }
                    else -> {
                        if (!t.msg.isNullOrEmpty()) {
                            showToast(t.msg, this@LoginActivity)
                        }
                    }
                }
            }
        } else if (t is Default) {
            when (t.retcode) {
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

    private fun requestLogin() {
        viewModel.requestLogin()
    }

    private fun requestSNSLogin() {
        viewModel.requestSNSLogin()
    }

    private fun requestSignUp() {
        viewModel.requestSignUp()
    }

    private fun initMainView() {
        showBottomSheet(R.layout.view_login_bottomsheet)
    }

    private fun showBottomSheet(view: Int) {
        if (::dialogView.isInitialized) {
            (dialogView.parent as ViewGroup).removeView(dialogView)
        }

        dialogView = layoutInflater.inflate(view, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()

        when (view) {
            R.layout.view_login_bottomsheet -> {
                dialogView.apply {
                    setLoginViewType()

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
                            if (viewModel.repository.pageType == CODE.LOGIN_MODE) {
                                goLoginButton.isEnabled =
                                    !(passwordEditTextView.text.toString().trim().isEmpty() &&
                                            passwordEditTextView.text.toString()
                                                .trim().length < 6 ||
                                            !emailCheck(s.toString()))
                            } else {
                                goNextButton.isEnabled =
                                    !(passwordEditTextView.text.toString().trim().isEmpty() &&
                                            passwordEditTextView.text.toString()
                                                .trim().length < 6 ||
                                            !emailCheck(emailEditTextView.text.toString().trim()))
                                            && termsImageView.isSelected
                            }
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
                            if (viewModel.repository.pageType == CODE.LOGIN_MODE) {
                                goLoginButton.isEnabled =
                                    !(passwordEditTextView.text.toString().trim().isEmpty() &&
                                            passwordEditTextView.text.toString()
                                                .trim().length < 6 ||
                                            !emailCheck(emailEditTextView.text.toString().trim()))
                            } else {
                                goNextButton.isEnabled =
                                    !(passwordEditTextView.text.toString().trim().isEmpty() &&
                                            passwordEditTextView.text.toString()
                                                .trim().length < 6 ||
                                            !emailCheck(emailEditTextView.text.toString().trim()))
                                            && termsImageView.isSelected
                            }
                        }
                    })

                    forgotPwTextView.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        showBottomSheet(R.layout.view_forgot_password_bottomsheet)
                    }

                    facebookView.setOnClickListener {
                        // facebook login
                        callbackManager = CallbackManager.Factory.create();
                        requestFacebookLogin()
                    }

                    googleView.setOnClickListener {
                        // google login
                        requestGoogleLogin()
                    }

                    hideImageView.setOnClickListener {
                        it.isSelected = !it.isSelected
                        if (it.isSelected) {
                            passwordEditTextView.inputType = InputType.TYPE_CLASS_TEXT
                        } else {
                            passwordEditTextView.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                    }
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
                            sendLinkButton.isEnabled = emailCheck(s.toString())
                        }
                    })
                    sendLinkButton.setOnClickListener {
                        // 비밀번호 찾기 요청
                        viewModel.repository.id = forgotEmailEditTextView.text.toString().trim()
                        viewModel.requestFindPassword()
                    }
                }
            }
            R.layout.view_signup_info_bottomsheet -> {
                KJKomicsApp.LOGIN_DATA = DataLogin()
                setNickNameView()
                dialogView.apply {
                    nextImageView.setOnClickListener {
                        when (viewModel.repository.signOutInfoStep) {
                            1 -> {
                                KJKomicsApp.LOGIN_DATA?.nickname =
                                    infoEmailEditTextView.text.toString()
                                viewModel.repository.signOutInfoStep = 2
                                nextImageView.isEnabled = false
                                setGenderView()
                            }
                            2 -> {
                                KJKomicsApp.LOGIN_DATA?.gender =
                                    if (genderView.maleView.isSelected) {
                                        genderView.maleView.tag.toString()
                                    } else {
                                        genderView.femaleView.tag.toString()
                                    }
                                viewModel.repository.signOutInfoStep = 3
                                setAgeView()
                            }
                            3 -> {
                                KJKomicsApp.LOGIN_DATA?.age = 30
                                viewModel.repository.signOutInfoStep = 4
                                nextImageView.isEnabled = false
                                setGenreView()
                            }
                            4 -> {
                                var genreList = KJKomicsApp.LOGIN_DATA?.genres.toString()
                                genreList = genreList.trim { it <= ' ' }.replace(" ", "")
                                genreList = genreList.substring(1, genreList.length - 1)
                                KJKomicsApp.LOGIN_DATA?.genreString = genreList
                                viewModel.repository.signOutInfoStep = 5
                                setLanguageView()
                            }
                            5 -> {
                                write(
                                    context,
                                    CODE.CURRENT_LANGUAGE,
                                    viewModel.repository.language
                                );
                                CommonUtil.setLocale(
                                    context,
                                    CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en")
                                )

                                val intent = Intent(CODE.LB_JOIN)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                                bottomSheetDialog.dismiss()

                                requestSignUp()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestGoogleLogin() {
        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_account_webclient_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
    }

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun requestFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("public_profile", "email")
        )
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    val request =
                        GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken()
                        ) { resultObject, response ->
                            try {
                                loginResult?.let {
                                    if (BuildConfig.DEBUG) {
                                        Log.e(
                                            TAG,
                                            "loginResult.getAccessToken() : " + it.accessToken.token
                                        )
                                    }
                                    viewModel.repository.loginType = CODE.LOGIN_TYPE_FACEBOOK
                                    viewModel.repository.snsToken = it.accessToken.token
                                    viewModel.repository.id = resultObject.getString("id")
                                    if (!resultObject.isNull("email")) {
                                        viewModel.repository.fbEmail =
                                            resultObject.getString("email")
                                    }
                                    viewModel.repository.fbName = resultObject.getString("name")
                                    if (viewModel.repository.fbEmail.isEmpty()) {
                                        viewModel.repository.oprofile = viewModel.repository.fbName
                                    } else {
                                        viewModel.repository.oprofile = viewModel.repository.fbEmail
                                    }
                                    if (viewModel.repository.pageType == CODE.LOGIN_MODE) {
                                        requestSNSLogin()
                                    } else {
                                        // 회원 정보 요청
                                        bottomSheetDialog.dismiss()
                                        showBottomSheet(R.layout.view_signup_info_bottomsheet)
                                    }
                                }
                            } catch (e: Exception) {
                                // TODO Auto-generated catch block
                                e.printStackTrace()
                            }
                        }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {}

                override fun onError(exception: FacebookException) {
                    showToast(getString(R.string.msg_fail_facebook), this@LoginActivity)
                }
            })
    }

    private fun setLanguageView() {
        dialogView.run {
            infoTextView.text =
                "(${viewModel.repository.signOutInfoStep}/5) ${getString(R.string.str_sign_info_step_noti)}"
            progressBar.progress = 5
            infoTitleTextView.text = getString(R.string.str_language)

            genreView.visibility = View.GONE
            languageView.apply {
                visibility = View.VISIBLE
                languageRecyclerView.layoutManager = LinearLayoutManager(context)
                KJKomicsApp.INIT_SET.lang_list?.let {
                    languageRecyclerView.adapter = InfoLanguageAdapter(it)
                    (languageRecyclerView.adapter as InfoLanguageAdapter).setOnItemClickListener(
                        object :
                            InfoLanguageAdapter.OnItemClickListener {
                            override fun onItemClick(item: Any?, position: Int) {
                                it.forEachIndexed { index, item ->
                                    if (index == position) {
                                        item.isSelect = true
                                        viewModel.repository.language = item.lang
                                    } else {
                                        item.isSelect = false
                                    }
                                }
                                (languageRecyclerView.adapter as InfoLanguageAdapter).notifyDataSetChanged()
                            }
                        })
                }
            }
        }
    }

    private fun setGenreView() {
        dialogView.run {
            infoTextView.text =
                "(${viewModel.repository.signOutInfoStep}/5) ${getString(R.string.str_sign_info_step_noti)}"
            progressBar.progress = 4
            infoTitleTextView.text = getString(R.string.str_genre_title)

            ageView.visibility = View.GONE

            genreView.apply {
                visibility = View.VISIBLE
                recyclerView.layoutManager = GridLayoutManager(context, 3)
                if (KJKomicsApp.LOGIN_DATA?.genres == null) {
                    KJKomicsApp.LOGIN_DATA?.genres = arrayListOf()
                }
                dialogView.nextImageView.isEnabled = KJKomicsApp.LOGIN_DATA?.genres?.size!! >= 3
                KJKomicsApp.INIT_SET.loginGenre_img_list?.let {
                    recyclerView.adapter = InfoGenreAdapter(it)
                    recyclerView.addItemDecoration(GenreDecoration(context))
                    (recyclerView.adapter as InfoGenreAdapter).setOnItemClickListener(object :
                        InfoGenreAdapter.OnItemClickListener {
                        override fun onItemClick(item: Any?) {
                            if (item is DataLoginGenre) {
                                if (KJKomicsApp.LOGIN_DATA?.genres == null) {
                                    KJKomicsApp.LOGIN_DATA?.genres = arrayListOf()
                                }

                                item.isSelect = !item.isSelect

                                if (item.isSelect) {
                                    if (KJKomicsApp.LOGIN_DATA?.genres?.size!! >= 3) return
                                    KJKomicsApp.LOGIN_DATA?.genres?.add(item.genre.toString())
                                } else {
                                    KJKomicsApp.LOGIN_DATA?.genres?.remove(item.genre.toString())
                                }
                                (recyclerView.adapter as InfoGenreAdapter).notifyDataSetChanged()
                                dialogView.nextImageView.isEnabled =
                                    KJKomicsApp.LOGIN_DATA?.genres?.size!! >= 3
                            }
                        }
                    })
                }
            }
        }
    }

    private fun setAgeView() {
        dialogView.run {
            infoTextView.text =
                "(${viewModel.repository.signOutInfoStep}/5) ${getString(R.string.str_sign_info_step_noti)}"
            progressBar.progress = 3
            infoTitleTextView.text = getString(R.string.str_age)

            genderView.visibility = View.GONE
            KJKomicsApp.INIT_SET.age_list?.let {
                ageView.apply {
                    visibility = View.VISIBLE
                    ageRecyclerView.layoutManager = GridLayoutManager(context, 2)
                    ageRecyclerView.addItemDecoration(AgeDecoration(context))
                    ageRecyclerView.adapter = InfoAgeAdapter(it)
                    (ageRecyclerView.adapter as InfoAgeAdapter).setOnItemClickListener(object :
                        InfoAgeAdapter.OnItemClickListener {
                        override fun onItemClick(item: Any?) {
                            if (item is DataAge) {
                                KJKomicsApp.LOGIN_DATA?.age = item.age
                                it.forEach { ageData ->
                                    ageData.isSelect = false
                                }
                                item.isSelect = !item.isSelect

                                (ageRecyclerView.adapter as InfoAgeAdapter).notifyDataSetChanged()
                                dialogView.nextImageView.isEnabled = true
                            }
                        }
                    })
                }
            }
        }
    }

    private fun setGenderView() {
        dialogView.run {
            infoTextView.text =
                "(${viewModel.repository.signOutInfoStep}/5) ${getString(R.string.str_sign_info_step_noti)}"
            progressBar.progress = 2
            infoTitleTextView.text = getString(R.string.str_gender)

            infoEmailEditTextView.visibility = View.GONE
            genderView.apply {
                visibility = View.VISIBLE
                maleView.setOnClickListener {
                    if (!it.isSelected) {
                        it.isSelected = true
                        femaleView.isSelected = false
                        it.mailTextView.setTypeface(null, Typeface.BOLD)
                        femaleView.femailTextView.setTypeface(null, Typeface.NORMAL)
                    }
                    dialogView.nextImageView.isEnabled = true
                }
                femaleView.setOnClickListener {
                    if (!it.isSelected) {
                        it.isSelected = true
                        maleView.isSelected = false
                        it.femailTextView.setTypeface(null, Typeface.BOLD)
                        maleView.mailTextView.setTypeface(null, Typeface.NORMAL)
                    }
                    dialogView.nextImageView.isEnabled = true
                }
            }
        }
    }

    private fun setNickNameView() {
        dialogView.apply {
            nextImageView.isEnabled = false
            infoTextView.text =
                "(${viewModel.repository.signOutInfoStep}/5) ${getString(R.string.str_sign_info_step_noti)}"
            progressBar.progress = 1
            infoEmailEditTextView.addTextChangedListener(object : TextWatcher {
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
                    nextImageView.isEnabled = !emailCheck(s.toString()) && s.length >= 6
                }
            })
        }
    }

    private fun setLoginViewType() {
        // login, signup 구분
        dialogView.apply {
            if (viewModel.repository.pageType == CODE.LOGIN_MODE) {
                signupTextView.isSelected = false
                loginTextView.isSelected = true
                signupTextView.setTypeface(null, Typeface.NORMAL)
                loginTextView.setTypeface(null, Typeface.BOLD)
                signupTextView.setOnClickListener {
                    viewModel.repository.pageType = CODE.SIGNUP_MODE
                    setLoginViewType()
                }

                welcomeTextView.text = getString(R.string.str_welcome_back)

                termsView.visibility = View.GONE
                termsView.isSelected = false

                goLoginButton.visibility = View.VISIBLE
                goNextButton.visibility = View.GONE
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
                    requestLogin()
                }

                forgotPwTextView.visibility = View.VISIBLE

                orViewtypeTextView.text = getString(R.string.str_or_login_with)

            } else {
                signupTextView.isSelected = true
                loginTextView.isSelected = false
                signupTextView.setTypeface(null, Typeface.BOLD)
                loginTextView.setTypeface(null, Typeface.NORMAL)
                loginTextView.setOnClickListener {
                    viewModel.repository.pageType = CODE.LOGIN_MODE
                    setLoginViewType()
                }

                welcomeTextView.text = getString(R.string.str_lets_started)

                termsView.visibility = View.VISIBLE
                termsView.isSelected = true
                termsView.setOnClickListener {
                    it.isSelected = !it.isSelected
                    goNextButton.isEnabled =
                        !("" == passwordEditTextView.text.toString().trim() ||
                                !emailCheck(emailEditTextView.text.toString().trim()))
                                && termsImageView.isSelected
                }
                termsTextView.setOnClickListener {
                    val intent =
                        Intent(this@LoginActivity, WebViewActivity::class.java).apply {
                            putExtra("title", termsTextView.text.toString())
                            putExtra(
                                "url", KJKomicsApp.getWebUrl().toString() + "terms/terms"
                            )
                        }
                    startActivity(intent)
//                    bottomSheetDialog.dismiss()
                }
                privacyTextView.setOnClickListener {
                    val intent =
                        Intent(this@LoginActivity, WebViewActivity::class.java).apply {
                            putExtra("title", privacyTextView.text.toString())
                            putExtra(
                                "url", KJKomicsApp.getWebUrl().toString() + "terms/privacy"
                            )
                        }
                    startActivity(intent)
//                    bottomSheetDialog.dismiss()
                }

                goLoginButton.visibility = View.GONE
                goNextButton.visibility = View.VISIBLE
                goNextButton.setOnClickListener {
                    // 개인정보 요청
                    // 회원가입 요청
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
                    bottomSheetDialog.dismiss()
                    showBottomSheet(R.layout.view_signup_info_bottomsheet)
                }

                forgotPwTextView.visibility = View.GONE

                orViewtypeTextView.text = getString(R.string.str_or_signup_with)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }
}