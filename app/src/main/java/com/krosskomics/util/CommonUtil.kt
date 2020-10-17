package com.krosskomics.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.facebook.AccessToken
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.login.LoginManager
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.login.activity.LoginActivity
import com.krosskomics.login.activity.LoginIntroActivity
import com.krosskomics.series.activity.SeriesActivity
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

object CommonUtil {
    private const val TAG = "CommonUtil"

    var EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "^(([0-9a-zA-Z]((\\.(?!\\.))|[-!#\\$%&'\\*\\+/=\\?\\^`\\{\\}\\|~\\w])*)(?<=[0-9a-zA-Z])@)"
                + "(([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,6})$"
    )
    var PHONE_PATTERN =
        Pattern.compile("^01([0|1|6|7|8|9]?)?([0-9]{3,4})?([0-9]{4})$")
    var TEL_PATTERN =
        Pattern.compile("^(0(2|3[1-3]|4[1-4]|5[1-5]|6[1-4]))(\\d{3,4})(\\d{4})$")
    var HANGUL_PATTERN = Pattern.compile("^[ㄱ-ㅣ가-힣]*$")

    //내부저장소 읽기
    fun write(
        context: Context?,
        key: String?,
        value: String?
    ) {
        try {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sp.edit()
            editor.putString(key, value)
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //내부저장소 읽기
    fun read(
        context: Context?,
        key: String?,
        defaultValue: String?
    ): String? {
        try {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getString(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    //토스트
    fun showToast(msg: String?, context: Context?) {
        if (msg == null) return
        if (context == null) return
        //		SuperToast.create(context, msg, SuperToast.Duration.SHORT).show();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(msg: String?, context: Context?) {
        if (msg == null) return
        if (context == null) return
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    //3글짜 요일문자를 숫자로 바꿈
    fun DayOfWeekToNum(dayWeek: String): Int {
        var res = Calendar.SUNDAY
        if (dayWeek.equals("SUN", ignoreCase = true)) res = Calendar.SUNDAY
        if (dayWeek.equals("MON", ignoreCase = true)) res = Calendar.MONDAY
        if (dayWeek.equals("TUE", ignoreCase = true)) res = Calendar.TUESDAY
        if (dayWeek.equals("WED", ignoreCase = true)) res = Calendar.WEDNESDAY
        if (dayWeek.equals("THU", ignoreCase = true)) res = Calendar.THURSDAY
        if (dayWeek.equals("FRI", ignoreCase = true)) res = Calendar.FRIDAY
        if (dayWeek.equals("SAT", ignoreCase = true)) res = Calendar.SATURDAY
        return res
    }

    fun emailCheck(email: String?): Boolean {
//		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
        val regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val p = Pattern.compile(regex)
        val m = p.matcher(email)
        return m.matches()
    }

    /**
     * 숫자에 천단위마다 콤마 넣기
     *
     * @param num
     * @return String
     */
    fun toNumFormat(num: Int): String? {
        val df = DecimalFormat("#,###")
        return df.format(num.toLong())
    }

    /**
     * 기기 가로 길이 구하기
     *
     * @param context
     * @return
     */
    fun getDeviceWidth(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.widthPixels
    }

    /**
     * 기기 세로 길이 구하기
     *
     * @param context
     * @return
     */
    fun getDeviceHeight(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.heightPixels
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun getAppVersion(context: Context): String? {
        var version = ""
        var i: PackageInfo? = null
        try {
            i = context.packageManager.getPackageInfo(context.packageName, 0)
            version = i.versionName
        } catch (e1: PackageManager.NameNotFoundException) {
            e1.printStackTrace()
        }
        return version
    }

    fun getVersionCode(context: Context): Int {
        var pinfo: PackageInfo? = null
        var versionNumber = 0
        try {
            pinfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionNumber = pinfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionNumber
    }

    fun logout(context: Context?) {
        try {
            if (CODE.LOGIN_TYPE_FACEBOOK.equals(
                    read(
                        context,
                        CODE.LOCAL_loginType,
                        ""
                    )
                )
            ) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    //페이스북 세션 종료
                    LoginManager.getInstance().logOut()
                }
            }
            if (CODE.LOGIN_TYPE_GOOGLE.equals(read(context, CODE.LOCAL_loginType, ""))) {
//                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestIdToken(context?.getString(R.string.google_account_webclient_id))
//                    .requestEmail()
//                    .build()
//                // [END config_signin]
//
//                googleSignInClient = GoogleSignIn.getClient(this, gso)
                // Google sign out
//                googleSignInClient.signOut().addOnCompleteListener(this) {
//                    updateUI(null)
//                }
                // Google revoke access
//                googleSignInClient.revokeAccess().addOnCompleteListener(this) {
//                    updateUI(null)
//                }
            }
            //로그인때 기록한 정보를 제거한다.
            if (!CODE.LOGIN_TYPE_KROSS.equals(read(context, CODE.LOCAL_loginType, ""))) {
                write(context, CODE.LOCAL_id, "")
            }
            write(context, CODE.LOCAL_loginType, "")
            write(context, CODE.LOCAL_loginYn, "N")
            write(context, CODE.LOCAL_user_no, "")
            write(context, CODE.LOCAL_email, "")
            write(context, CODE.LOCAL_coin, "0")
            write(context, CODE.Local_oprofile, "")
            write(context, CODE.LOCAL_login_token, "")
            write(context, CODE.LOCAL_ENC_USER_NO, "")
            ServerUtil.resetRetrofitServer(context)
            KJKomicsApp.IS_GET_NEW_GIFT = false
            KJKomicsApp.PROFILE_PICTURE = ""

            KJKomicsApp.LOGIN_SEQ = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getWifiLinkSpeed(context: Context): Int {
        val linkSpeed = 0
        try {
            val wifiManager =
                context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            return info.linkSpeed
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return linkSpeed
    }

    /**
     * 국가 코드 얻어 오기
     *
     * @param context
     * @return
     */
    fun getCountrylso(context: Context): String? {
        val tm =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.simCountryIso
    }

    // 언어 설정 메소드
    fun setLocale(context: Context, charicter: String?) {
        val locale = Locale(charicter)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources
            .updateConfiguration(config, context.resources.displayMetrics)
    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter
            ?: // pre-condition
            return
        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(
            listView.width,
            View.MeasureSpec.AT_MOST
        )
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
        listView.isFocusable = false
    }

    fun downKeyboard(context: Context, editText: EditText) {
        val mInputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mInputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun getNetworkInfo(context: Context): NetworkInfo? {
        // network check
        val connectivityManager: ConnectivityManager
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

    fun dpToPx(context: Context?, dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(context: Context?, px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun setOverflowButtonColor(activity: Activity, color: Int) {
        val overflowDescription =
            activity.getString(R.string.abc_action_menu_overflow_description)
        val decorView = activity.window.decorView as ViewGroup
        val viewTreeObserver = decorView.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val outViews =
                ArrayList<View>()
            decorView.findViewsWithText(
                outViews,
                overflowDescription,
                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
            )
            if (outViews.isEmpty()) {
                return@OnGlobalLayoutListener
            }
            val overflow: AppCompatImageView = outViews[0] as AppCompatImageView
            overflow.setColorFilter(color)
        })
    }

    fun getTopActivity(context: Context): String? {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = manager.getRunningTasks(1)
        val cn = info[0].topActivity
        return cn!!.shortClassName.substring(1)
    }

    /**
     * 앱의 인스톨 여부 확인 후 인스톨안되면 설치
     *
     * @param packageName
     * @return
     */
    fun isInstallAppAndGoInstall(
        context: Context,
        packageName: String
    ): Boolean {
        var isInstall = false
        try {
            val startLink =
                context.packageManager.getLaunchIntentForPackage(packageName)
            if (startLink == null) {
                isInstall = false
                Toast.makeText(
                    context,
                    context.getString(R.string.msg_not_install_app),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=$packageName")
                context.startActivity(intent)
            } else {
                isInstall = true
            }
        } catch (e: Exception) {
            isInstall = false
        }
        return isInstall
    }

    /**
     * 앱의 인스톨 여부 확인
     *
     * @param packageName
     * @return
     */
    fun isInstallApp(
        context: Context,
        packageName: String?
    ): Boolean {
        var isInstall = false
        isInstall = try {
            val startLink =
                context.packageManager.getLaunchIntentForPackage(packageName!!)
            if (startLink == null) {
                false
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
        return isInstall
    }

    /**
     * 클립보드에 주소 복사 기능
     *
     * @param context
     * @param link
     */
    fun setClipBoardLink(context: Context, link: String?) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", link)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, context.getString(R.string.msg_copied), Toast.LENGTH_SHORT).show()
    }

    /**
     * 앱 공유
     *
     * @param context
     * @param subject
     * @param message
     * @param packageName
     */
    fun shareLink(
        context: Context,
        subject: String?,
        message: String?,
        packageName: String?
    ) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, subject) // 페이스북은 무시하는 정보이지만 트위터나 Mail 등등에서는 필요한 항목
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.setPackage(packageName)
        //Text Share시 image를 기준으로 공유가 되며, 불가능한 경우도 있음.
        context.startActivity(intent)
    }

    /**
     * 웹 공유
     *
     * @param activity
     * @param url
     */
    fun shareLinkWeb(activity: Activity, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        //Text Share시 image를 기준으로 공유가 되며, 불가능한 경우도 있음.
        activity.startActivity(intent)
    }

    /**
     * 앱 실행여부 체크
     *
     * @param context
     * @return
     */
    fun isAppRunning(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos =
            activityManager.runningAppProcesses
        for (i in processInfos.indices) {
            if (processInfos[i].processName == context.packageName) {
                return true
            }
        }
        return false
    }

    fun getRunningClass(context: Context): String? {
        var topActivity = ""
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info: List<RunningTaskInfo>?
        info = activityManager.getRunningTasks(1)
        if (info != null && info.size > 0) {
            val runningTaskInfo = info[0]
            if (runningTaskInfo != null) {
                topActivity = runningTaskInfo.topActivity!!.className
            }
        }
        return topActivity
    }

    /**
     * 시스템 UI 숨김 처리
     *
     * @param context
     * @param mDecorView
     * @param toolbar
     * @param showAppbar
     * @param layBottom
     * @param isSea
     */
    @SuppressLint("InlinedApi")
    fun hideSystemUI(
        context: Context,
        mDecorView: View,
        toolbar: Toolbar,
        showAppbar: LinearLayout,
        layBottom: LinearLayout,
        isSea: Boolean
    ) {

        showAppbar.setBackgroundColor(ContextCompat.getColor(context, R.color.trans))
        mDecorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navigation bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        showAppbar.setPadding(0, 0, 0, 0)
        toolbar.visibility = View.GONE
        layBottom.visibility = View.GONE
    }

    /**
     * 뷰어 UI 숨김 처리
     *
     * @param context
     * @param toolbar
     * @param showAppbar
     * @param layBottom
     */
    fun hideViewerUI(
        context: Context,
        toolbar: Toolbar,
        showAppbar: LinearLayout,
        layBottom: LinearLayout
    ) {
        showAppbar.setBackgroundColor(ContextCompat.getColor(context, R.color.trans))
        toolbar.visibility = View.GONE
        layBottom.visibility = View.GONE
    }

    /**
     * 뷰어 UI 노출
     *
     * @param context
     * @param toolbar
     * @param showAppbar
     * @param layBottom
     */
    fun showViewerUI(
        context: Context?,
        toolbar: Toolbar,
        showAppbar: LinearLayout?,
        layBottom: LinearLayout
    ) {
        toolbar.visibility = View.VISIBLE
        layBottom.visibility = View.VISIBLE
    }

    /**
     * 하단 네비게이션 바 높이
     *
     * @param context
     * @return
     */
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 하단 네비게이션 바 유무
     *
     * @return
     */
    fun hasSoftNavigationBar(): Boolean {
        val hasBackKey =
            KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        val hasHomeKey =
            KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
        return !(hasBackKey && hasHomeKey)
    }

    /**
     * 상단 스테이터스바 높이
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun showNetworkAlertDialog(context: Context) {
        if (!(context as Activity).isFinishing) {
            try {
                var msg = ""
                var positiveStr = ""
                msg = "Anda belum login~" + "Silakan login terlebih dahulu!"
                positiveStr = "OK"
                val builder =
                    AlertDialog.Builder(context)
                builder.setMessage(msg)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which -> dialog.dismiss() }.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun goLoginAlert(context: Context) {
        val intent = Intent(context, LoginIntroActivity::class.java)
        intent.putExtra("pageType", "login")
        context.startActivity(intent)
    }

    fun moveSignUp(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("pageType", "signup")
        context.startActivity(intent)
    }

    fun setGlobalFont(
        context: Context,
        view: View?
    ) {
        if (view != null) {
            if (view is ViewGroup) {
                val vg = view
                val len = vg.childCount
                for (i in 0 until len) {
                    val v = vg.getChildAt(i)
                    if (v is TextView) {
                        v.typeface = Typeface.createFromAsset(
                            context.assets, "Comfortaa-Regular.ttf"
                        )
                    }
                    setGlobalFont(context, v)
                }
            }
        } else {
        }
    }

    fun moveBrowserChrome(context: Context, url: String?) {
        try {
            val chromePackageName = "com.android.chrome"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (isInstallApp(context, chromePackageName)) {
                intent.setPackage("com.android.chrome")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun moveAppMarket(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun showErrorView(erroView: View) {
        erroView.visibility = View.VISIBLE
        erroView.setOnClickListener(null)
    }

    fun convertUno(uno: String?): String? {
        return uno?.replace("/", "slash")
    }

    /**
     * 랜덤한 문자열을 원하는 길이만큼 반환합니다.
     *
     * @param length 문자열 길이
     * @return 랜덤문자열
     */
    fun getRandomString(length: Int): String? {
        val buffer = StringBuffer()
        val random = Random()
        val chars =
            "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",")
                .toTypedArray()
        for (i in 0 until length) {
            buffer.append(chars[random.nextInt(chars.size)])
        }
        return buffer.toString()
    }

    fun getDraweeController(
        context: Context?,
        url: String?,
        width: Int,
        height: Int
    ): DraweeController? {
        val requestBuilder =
            ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
        if (getDeviceWidth(context!!) < 720) {
            requestBuilder.resizeOptions = ResizeOptions(width, height)
        }
        requestBuilder.setRequestPriority(Priority.HIGH).isProgressiveRenderingEnabled = true
        val request = requestBuilder.build()
        return Fresco.newDraweeControllerBuilder()
            .setUri(Uri.parse(url))
            .setImageRequest(request)
            .build()
    }

//    fun likeCountFormat(context: Context, likeCount: String?): String{
//        if (likeCount == null) return ""
//        likeCount.let {
//            if (it.toInt() <= 999) return it else return context.getString(R.string.str_like_count_max)
//        }
//    }

    /**
     * 푸시타입별 액션 설정
     * @param context
     * @param pushType
     * @param cno
     */
    fun setPushAction(context: Context?, pushType: String?, cno: String?): Intent {
        var intent = Intent(context, MainActivity::class.java)
        when (pushType) {
            "M" -> {
                intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            "H" -> {
                intent = Intent(context, SeriesActivity::class.java)
                val b = Bundle()
                b.putString("cid", cno)
                intent.putExtras(b)
            }
        }
        return intent
    }

    fun getDayWeek(): Int {
        val calendar = Calendar.getInstance()
        // 1: 일요일, 7: 토요일
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun sendEmail(context: Context) {
        Intent(Intent.ACTION_SEND).apply {
            type = "plain/Text"
            putExtra(Intent.EXTRA_EMAIL, context.getString(R.string.str_kk_email))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.str_faq))
            putExtra(Intent.EXTRA_TEXT, "앱 버전 (AppVersion):" + CommonUtil.getAppVersion(context) + "\n기기명 (Device):\n안드로이드 OS (Android OS):\n내용 (Content)")
            type = "message/rfc822"

            context.startActivity(this)
        }
    }
}