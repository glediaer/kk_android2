package com.krosskomics.webview

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.event.activity.EventActivity
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.login.activity.LoginActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.settings.activity.SettingsActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.getAppVersion
import com.krosskomics.util.CommonUtil.read
import kotlinx.android.synthetic.main.activity_webview.*
import java.util.*

class WebViewActivity : ToolbarTitleActivity() {
    private val TAG = "WebViewActivity"

    var webUrl = ""
    val GOOGLE_PLAY_STORE_PREFIX = "market://details?id="
    val INTENT_PROTOCOL_START = "intent:"
    val INTENT_PROTOCOL_INTENT = "#Intent;"
    val INTENT_PROTOCOL_END = ";end"
    val INTENT_PACKAGE_NAME = "package="

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    override fun initTracker() {
        setTracker(toolbarTitleString)
    }

    override fun initModel() {
        intent?.apply {
            toolbarTitleString = extras?.getString("title").toString()
            webUrl = extras?.getString("url").toString()
        }
    }

    override fun initMainView() {
        val webViewClientClass: WebViewClientClass =
            WebViewClientClass()
        webView.apply {

        }
        webView.addJavascriptInterface(
            AndroidBridge(),
            "kk_app"
        )
        webView.setWebChromeClient(WebViewChromeClient())
        webView.setWebViewClient(webViewClientClass)
        webView.settings.apply {
            javaScriptEnabled = true
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true
        }
        if ("" == read(context, CODE.LOCAL_ENC_USER_NO, "")) {
            webView.loadUrl(webUrl)
        } else {
            val headers: MutableMap<String, String?> =
                HashMap()
            headers["KK-APP-KEY"] = CODE.AUTH_KEY
            headers["KK-APP-DEVICEID"] = read(context, CODE.LOCAL_Android_Id, "")
            headers["KK-APP-SECRET"] = read(context, CODE.LOCAL_APP_SECRET, "")
            headers["KK-APP-VERSION"] = getAppVersion(context)
            headers["KK-APP-TOKEN"] = KJKomicsApp.APPTOKEN
            headers["KK-U-TOKEN"] = read(context, CODE.LOCAL_ENC_USER_NO, "")
            headers["lang"] = read(context, CODE.CURRENT_LANGUAGE, "en")
            headers["os-version"] = Build.VERSION.RELEASE
            headers["device"] = "google"
            webView.loadUrl(webUrl, headers)
        }
    }

    private inner class WebViewClientClass : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            webUrl = url
            try {
                if ((url.startsWith("http://") || url.startsWith("https://")) && (url.contains("market.android.com") || url.contains(
                        "m.ahnlab.com/kr/site/download"
                    ))
                ) {
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    return try {
                        startActivity(intent)
                        true
                    } catch (e: ActivityNotFoundException) {
                        false
                    }
                } else if (url.contains(GOOGLE_PLAY_STORE_PREFIX)) { // 마켓 설치 url
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else if (url.startsWith("mailto:")) {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(intent)
                } else {
                    view.loadUrl(url)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            if (!this@WebViewActivity.isFinishing) {
                val url = request.url.toString()
                webUrl = url
                try {
                    if (url.startsWith(INTENT_PROTOCOL_START)) {
                        val customUrlStartIndex: Int = INTENT_PROTOCOL_START.length
                        val customUrlEndIndex: Int =
                            url.indexOf(INTENT_PROTOCOL_INTENT)
                        return if (customUrlEndIndex < 0) {
                            false
                        } else {
                            try {
                                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                val uri =
                                    Uri.parse(intent.dataString)
                                startActivity(Intent(Intent.ACTION_VIEW, uri))
                            } catch (e: ActivityNotFoundException) {
                                Log.e(
                                    TAG,
                                    "shouldOverrideUrlLoading + ActivityNotFoundException$e"
                                )
                                val packageStartIndex: Int =
                                    url.indexOf(INTENT_PACKAGE_NAME) +INTENT_PACKAGE_NAME.length
                                val packageEndIndex: Int =
                                    url.indexOf(INTENT_PROTOCOL_END)
                                val packageName = url.substring(
                                    packageStartIndex,
                                    if (packageEndIndex < 0) url.length else packageEndIndex
                                )
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)
                                    )
                                )
                            }
                            true
                        }
                    } else if (url.contains(GOOGLE_PLAY_STORE_PREFIX)) { // 마켓 설치 url
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } else if (url.startsWith("mailto:")) {
                        val intent =
                            Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                        startActivity(intent)
                    } else {
                        view.loadUrl(url)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (!this@WebViewActivity.isFinishing) {
                showProgress(context)
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (!this@WebViewActivity.isFinishing()) {
                hideProgress()
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            if (!this@WebViewActivity.isFinishing()) {
                hideProgress()
            }
        }
    }

    private inner class AndroidBridge {
        @JavascriptInterface
        fun goAppView(arg: String) { // must be final
            Handler().post(Runnable {
                Log.e("HybridApp", "setMessage($arg)")
                val intent: Intent
                val bundle = Bundle()
                when (arg) {
                    "main" -> {
                        intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "my" -> {
                        intent = Intent(context, SettingsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "login" -> {
                        intent = Intent(context, LoginActivity::class.java)
                        bundle.putString("pageType", CODE.LOGIN_MODE)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    }
                    "signup" -> {
                        intent = Intent(context, LoginActivity::class.java)
                        bundle.putString("pageType", CODE.SIGNUP_MODE)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    }
                    "gift" -> {
                        intent = Intent(context, LibraryActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "invite" -> {
//                        intent = Intent(context, InviteActivity::class.java)
//                        startActivity(intent)
//                        finish()
                    }
                    "promo" -> {
                        intent = Intent(context, EventActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "library" -> {
                        intent = Intent(context, LibraryActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }

        @JavascriptInterface
        fun goAppView(arg: String, sid: String?) { // must be final
            Handler().post(Runnable {
                Log.e("HybridApp", "setMessage($arg)")
                val intent = Intent(context, SeriesActivity::class.java)
                when (arg) {
                    "series" -> {
                        val b = Bundle()
                        b.putString("sid", sid)
                        intent.putExtras(b)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }

        @JavascriptInterface
        fun openWebview(title: String?, url: String?) {
            Handler().post(Runnable {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("url", url)
                startActivity(intent)
            })
        }
    }

    private inner class WebViewChromeClient : WebChromeClient() {
        override fun onCreateWindow(
            view: WebView,
            dialog: Boolean,
            userGesture: Boolean,
            resultMsg: Message
        ): Boolean {
            val newWebView = WebView(context)
            val transport = resultMsg.obj as WebViewTransport
            transport.webView = newWebView
            resultMsg.sendToTarget()
            return true
        }

        override fun onJsAlert(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
//            Log.e(TAG, "message : " + message);
            return true
        }
    }
}

