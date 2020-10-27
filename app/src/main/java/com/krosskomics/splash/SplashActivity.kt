package com.krosskomics.splash

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import bolts.AppLinks
import com.facebook.applinks.AppLinkData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.iid.FirebaseInstanceId
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.KJKomicsApp.Companion.LATEST_APP_VERSION_CODE
import com.krosskomics.R
import com.krosskomics.common.model.AppToken
import com.krosskomics.common.model.Cookie
import com.krosskomics.common.model.Login
import com.krosskomics.common.model.Version
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.getNetworkInfo
import com.krosskomics.util.CommonUtil.moveAppMarket
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil
import com.krosskomics.util.ServerUtil.setRetrofitServer
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.view_network_error_init.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SplashActivity : Activity() {
    private val TAG = "SplashActivity"
    private lateinit var context: Context
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this@SplashActivity
        if (getNetworkInfo(context) == null) {
            KJKomicsApp.IS_ENTER_OFFLINE = true
            networkErrorView.visibility = View.VISIBLE
            refreshButton.setOnClickListener {
                if (getNetworkInfo(context) == null) {
                    return@setOnClickListener
                }
                networkErrorView.visibility = View.GONE
                initProcess()
            }
            goDownloadEpButton.setOnClickListener {
                startActivity(Intent(context, LibraryActivity::class.java))
                finish()
            }
            return
        }
        val bundle = intent.extras
        if (bundle != null) {
            KJKomicsApp.ATYPE = bundle.getString("atype")
            KJKomicsApp.SID = bundle.getString("sid")
        }
        // 외부 인텐트 데이터
        val data = intent.data
        //https://krosskomics.com?sid=3445&lang=en
        //https://krosskomics.com?rid=455667
//        data = Uri.parse("https://krosskomics.com?rid=455667");
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "KJKomicsApp.ATYPE : " + KJKomicsApp.ATYPE)
            Log.i(TAG, "data uri : $data")
        }
        if (data != null) {
            KJKomicsApp.DEEPLINK_CNO = data.getQueryParameter("sid")
            KJKomicsApp.DEEPLINK_RID = data.getQueryParameter("rid") ?: ""
        }

        initProcess()
    }

    private fun initProcess() {
        initPreference()
        checkFCMToken()
        setDynamicLink()
        setFacebookLink()
        if ("0" == read(context, CODE.IS_RUN_FIRST_APP, "0")) {
            write(context, CODE.IS_RUN_FIRST_APP, "1")
            if (TextUtils.isEmpty(KJKomicsApp.DEEPLINK_DATA)) {
                requestCookieServer()
            }
        }
    }

    private fun setFacebookLink() {
        val targetUrl =
            AppLinks.getTargetUrlFromInboundIntent(this, intent)
        if (targetUrl != null) {
            KJKomicsApp.DEEPLINK_DATA = targetUrl.toString()
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "App Link Target URL: $targetUrl")
            }
        }
    }

    private fun setDynamicLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    KJKomicsApp.DEEPLINK_DATA = deepLink.toString()
                }
                Log.e(TAG, "deepLink : $deepLink")
            }
            .addOnFailureListener(
                this
            ) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
    }

    private fun initPreference() {
        setRetrofitServer(context)
        uid = read(context, CODE.LOCAL_Android_Id, "")
        if ("" == read(context, CODE.LOCAL_RECIEVE_PUSH, "")) {
            write(context, CODE.LOCAL_RECIEVE_PUSH, "1")
        }
        if ("" == read(context, CODE.CURRENT_LANGUAGE, "")) {
            write(context, CODE.CURRENT_LANGUAGE, "en")
        }
        if (TextUtils.isEmpty(uid)) {  // 유저 식별자
            uid = Settings.System.getString(
                context!!.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            if (TextUtils.isEmpty(uid) || "9774d56d682e549c" == uid) {
                val uuid = UUID.randomUUID()
                uid = uuid.toString().replace("-", "")
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "uid : $uid")
            }
            write(context, CODE.LOCAL_Android_Id, uid)
            setRetrofitServer(context)
        }
        if ("" == read(context, CODE.LOCAL_REF_SOURCE, "")) {
            write(context, CODE.LOCAL_REF_SOURCE, CODE.REF_SOURCE)
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "CODE.REF_SOURCE : " + CODE.REF_SOURCE)
        }
        requestAppVersion()
    }

    private fun checkFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                write(this@SplashActivity, CODE.LOCAL_token, token)
                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
//        val FCMToken = FirebaseInstanceId.getInstance().id
//        if (FCMToken != null) {
//            write(this@SplashActivity, CODE.LOCAL_token, FCMToken)
//        }
    }

    /**
     * 구글 플레이 서비스 체크
     */
    private fun checkGoogleService() {
        if (checkPlayServices()) {
//            val intent = Intent(context, RegistrationIntentService::class.java)
//            startService(intent)
        }

//        requestAppVersion();
        loginCheck()
    }

    /**
     * 자동 로그인 체크
     */
    private fun loginCheck() {
        // 로그인 타입 체크
        if ("" != read(context, CODE.LOCAL_ENC_USER_NO, "")) {
            //로그인 시킨다.
            resetLoginData()
            requestLoginId("auto")
        } else {
            goMain()
        }
    }

    private fun resetLoginData() {
        write(context, CODE.LOCAL_loginYn, "N")
        write(context, CODE.LOCAL_user_no, "")
        write(context, CODE.LOCAL_coin, "0")
        write(context, CODE.Local_oprofile, "")
        write(context, CODE.LOCAL_login_token, "")
    }

    /**
     * 메인으로 이동
     */
    private fun goMain() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val handler = Handler()
                handler.postDelayed({
                    val intentMain: Intent
                    intentMain = Intent(this@SplashActivity, MainActivity::class.java)
                    intentMain.putExtra("atype", KJKomicsApp.ATYPE)
                    intentMain.putExtra("sid", KJKomicsApp.SID)
                    intentMain.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                    startActivity(intentMain)
                    finish()
                }, 500)
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                showToast(getString(R.string.msg_permission_denied1), context)
                finish()
            }
        }
        TedPermission(context)
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.msg_permission_denied2))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    /**
     * 각 타입별 로그인 요청
     *
     * @param loginType
     */
    private fun requestLoginId(loginType: String) {
        if (!this@SplashActivity.isFinishing) {
            val api: Call<Login> = ServerUtil.service.setAutoLogin(
                read(context, CODE.CURRENT_LANGUAGE, "en"),
                loginType,
                read(context, CODE.LOCAL_token, "")
            )
            api.enqueue(object : Callback<Login> {
                override fun onResponse(
                    @NonNull call: Call<Login>,
                    @NonNull response: Response<Login>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val loginData: Login? = response.body()
                            loginData?.let {
                                val retcode: String = loginData.retcode ?: "00"
                                if ("00".equals(retcode, ignoreCase = true) || "104".equals(
                                        retcode,
                                        ignoreCase = true
                                    )
                                ) {
                                    //내부 저장소에 정보를 기록
//                                CommonUtil.write(context, CODE.LOCAL_loginType, loginType);
                                    write(context, CODE.LOCAL_loginYn, "Y")
                                    write(context, CODE.LOCAL_user_no, loginData.user?.u_token)
                                    write(context, CODE.LOCAL_coin, loginData.user?.user_coin)
                                    write(context, CODE.LOCAL_Nickname, loginData.user?.nick)
                                    write(context, CODE.LOCAL_email, loginData.user?.email)
                                    if (!TextUtils.isEmpty(loginData.user?.u_token)) {
                                        write(
                                            context,
                                            CODE.LOCAL_ENC_USER_NO,
                                            loginData.user?.u_token
                                        )
                                        setRetrofitServer(context)
                                    }
                                    KJKomicsApp.LOGIN_SEQ = loginData.user?.login_seq!!
                                    if (null != loginData.user?.new_gift) {
                                        if ("1" == loginData.user?.new_gift) {
                                            KJKomicsApp.IS_GET_NEW_GIFT = true
                                        }
                                    }
                                    KJKomicsApp.PROFILE_PICTURE = loginData.user?.profile_picture.toString()
                                    goMain()
                                } else {
                                    write(context, CODE.LOCAL_ENC_USER_NO, "")
                                }
                            }
                        } else {
                            showToast(context.getString(R.string.msg_fail_login), context)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    @NonNull call: Call<Login?>,
                    @NonNull t: Throwable
                ) {
                    showToast(context.getString(R.string.msg_error_server), context)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
//        AppEventsLogger.activateApp(application)
    }

    override fun onPause() {
        super.onPause()
//        AppEventsLogger.deactivateApp(application)
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show()
            } else {
                showToast(
                    getString(R.string.msg_push_not_support_device),
                    this@SplashActivity
                )
                finish()
            }
            return false
        }
        return true
    }

    /**
     * 버전 정보 요청
     */
    private fun requestAppVersion() {
        if (!this@SplashActivity.isFinishing) {
            val version: Call<Version> = ServerUtil.service.getVersion
            version.enqueue(object : Callback<Version> {
                override fun onResponse(
                    @NonNull call: Call<Version>,
                    @NonNull response: Response<Version>
                ) {
                    try {
                        if (response.isSuccessful) {
                            requestAppVersionResult(response.body())
                        } else {
                            showToast(
                                context.getString(R.string.msg_fail_dataloading),
                                context
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    @NonNull call: Call<Version>,
                    @NonNull t: Throwable
                ) {
                    t.printStackTrace()
                    showToast(context.getString(R.string.msg_error_server), context)
                }
            })
        }
    }

    /**
     * 버전 정보 결과 처리
     * @param data
     */
    private fun requestAppVersionResult(data: Version?) {
        //action F: 강제 업데이트
        //action M: 서비스 점검
        //action U: 업데이트 유도
        try {
            data?.let {
//                if ("F" == it.action) {       // 강제 업데이트
//                    if (it.app_version > getVersionCode(context)) {
//                        showUpdateAlert(it)
//                        return
//                    }
//                } else if ("M" == it.action) {    // 서비스 점검
//                    showMaintenanceAlert(it)
//                    return
//                } else if ("U" == data.action) {    // 업데이트 유도
//                    if (it.app_version > getVersionCode(context)) {
//                        showUpdateAlert(it)
//                        return
//                    }
//                }
                LATEST_APP_VERSION_CODE = it.app_version
                requestAppToken()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestAppToken() {
        if (!this@SplashActivity.isFinishing) {
            val api: Call<AppToken> =
                ServerUtil.service.getCheckApp(read(context, CODE.LOCAL_Android_Id, ""))
            api?.enqueue(object : Callback<AppToken> {
                override fun onResponse(
                    @NonNull call: Call<AppToken>,
                    @NonNull response: Response<AppToken>
                ) {
                    try {
                        if (response.isSuccessful) {
                            if (!TextUtils.isEmpty(response.body()?.app_token) &&
                                "0" != response.body()?.app_token
                            ) {
                                if (BuildConfig.DEBUG) {
                                    Log.e(
                                        TAG,
                                        "LOCAL_Android_Id : " + read(
                                            context,
                                            CODE.LOCAL_Android_Id,
                                            ""
                                        )
                                    )
                                    Log.e(
                                        TAG,
                                        "app_token : " + response.body()?.app_token
                                    )
                                }
                                KJKomicsApp.APPTOKEN = response.body()?.app_token ?: "0"
                                setRetrofitServer(context)
                                KJKomicsApp.APPTOKEN_RECIEVE_TIME =
                                    System.currentTimeMillis()
                                checkGoogleService()
                            }
                            if (BuildConfig.DEBUG) {
                                Log.e(
                                    TAG,
                                    "KJKomicsApp.APPTOKEN : " + KJKomicsApp.APPTOKEN
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    @NonNull call: Call<AppToken>,
                    @NonNull t: Throwable
                ) {
                    try {
                        Log.e(TAG, "onFailure : " + t.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private fun requestCookieServer() {
        if (!this@SplashActivity.isFinishing) {
            val getMain =
                ServerUtil.service.getCookieData("deep_link", Build.MODEL)
            getMain.enqueue(object : Callback<Cookie> {
                override fun onResponse(
                    @NonNull call: Call<Cookie>,
                    @NonNull response: Response<Cookie>
                ) {
                    try {
                        if (response.isSuccessful) {
                            KJKomicsApp.DEEPLINK_CNO = response.body()!!.sid
                            KJKomicsApp.DEEPLINK_RID = response.body()?.rid ?: ""
                            if (BuildConfig.DEBUG) {
                                Log.e(
                                    TAG,
                                    "response.body().sid : " + response.body()!!.sid
                                )
                                Log.e(
                                    TAG,
                                    "response.body().rid : " + response.body()!!.rid
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    @NonNull call: Call<Cookie>,
                    @NonNull t: Throwable
                ) {
                    try {
                        Log.e(TAG, "onFailure : " + t.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private fun initDialog(view: View): Dialog {
        val dialog = Dialog(this@SplashActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        val params: ViewGroup.LayoutParams = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as WindowManager.LayoutParams
        dialog.show()
        return dialog
    }

    /**
     * 버전 팝업
     */
    private fun showUpdateAlert(data: Version) {
        try {
            val innerView =
                layoutInflater.inflate(R.layout.dialog_update, null)
            val dialog = initDialog(innerView)
            val tvMsg1 = innerView.findViewById<TextView>(R.id.tv_msg1)
            val tvMsg2 = innerView.findViewById<TextView>(R.id.tv_msg2)
            val tvMsg3 = innerView.findViewById<TextView>(R.id.tv_msg3)
            val btnConfirm =
                innerView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel =
                innerView.findViewById<Button>(R.id.btn_cancel)
            tvMsg1.text = data.msg1
            tvMsg2.text = data.msg2
            tvMsg3.text = data.msg3
            btnCancel.setOnClickListener {
                if ("U" == data.action) {
                    dialog.dismiss()
                    loginCheck()
                } else if ("F" == data.action) {
                    dialog.dismiss()
                    moveTaskToBack(true)
                    finish()
                    Process.killProcess(Process.myPid())
                }
            }
            btnConfirm.setOnClickListener {
                dialog.dismiss()
                moveAppMarket(context!!, CODE.MARKET_URL)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 점검 팝업
     */
    private fun showMaintenanceAlert(data: Version) {
        try {
            val innerView: View =
                layoutInflater.inflate(R.layout.dialog_maintenance, null)
            val dialog = initDialog(innerView)
            val tvMsg1 = innerView.findViewById<TextView>(R.id.tv_msg1)
            val tvMsg2 = innerView.findViewById<TextView>(R.id.tv_msg2)
            val tvMsg3 = innerView.findViewById<TextView>(R.id.tv_msg3)
            val btnConfirm =
                innerView.findViewById<Button>(R.id.btn_confirm)
            tvMsg1.text = data.msg1
            tvMsg2.text = data.msg2
            tvMsg3.text = data.msg3
            btnConfirm.setOnClickListener {
                dialog.dismiss()
                moveTaskToBack(true)
                finish()
                Process.killProcess(Process.myPid())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}