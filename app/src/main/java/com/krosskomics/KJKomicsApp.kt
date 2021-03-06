package com.krosskomics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.stetho.Stetho
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.data.DataLogin
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.model.InitSet
import com.krosskomics.util.CODE
import com.krosskomics.util.ServerUtil.setRetrofitServer
import java.io.File
import java.util.*

class KJKomicsApp : Application() {
    private var instance: KJKomicsApp? = null
    private val currentActivity: Activity? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        var DOWNLOAD_COUNT = 0

        // google analiytics
        private val PROPERTY_ID = "UA-75993443-2"

        var FILE_ROOT_PATH = Environment.getExternalStorageDirectory()
            .absolutePath + "/" + "krosskomics/"
        var DOWNLOAD_ROOT_PATH = FILE_ROOT_PATH + "download/"
        var DOWNLOAD_FILE_NAME = "imagefile"

        // init data
        var INIT_SET: InitSet = InitSet()

        // Charge state
        var IS_CHAGE_COMPLETED = false

        // current activity
        var DATA_EPISODE: DataEpisode = DataEpisode()

        // 선물함
        var IS_GET_NEW_GIFT = false
        var IS_CHANGE_LANGUAGE = false

        var LOGIN_DATA: DataLogin? = null

        var DEEPLINK_DATA: String? = null
        var DEEPLINK_CNO: String? = null
        var DEEPLINK_EP_NO: String? = null
        var DEEPLINK_RID = ""
        var SHARE_EP_NO: String? = null

        var autoscroll = 0

        var IS_ENTER_OFFLINE = false

        var PROFILE_PICTURE = ""

        var mGoogleApiClient: GoogleApiClient? = null

        // push data
        var ATYPE: String? = null
        var SID: String? = null

        // seq
        var APPTOKEN = "0"
        var LOGIN_SEQ: Long = 0
        var RUN_SEQ: Long = 0
        var APPTOKEN_RECIEVE_TIME: Long = 0

        fun getServerUrl(context: Context?): String? {
            return CODE.SERVER_URL
        }

        fun getWebUrl(): String? {
            return CODE.WEB_URL
        }

        var MAIN_CONTENTS: ArrayList<DataMainContents> = arrayListOf()
        var LATEST_APP_VERSION: String = ""
        var LATEST_APP_VERSION_CODE: Int = 0

        private const val AF_DEV_KEY = "YGvwqLaSWhoFCXDypHQLZ4"
    }

    enum class TrackerName {
        APP_TRACKER,  // 앱 별로 트래킹
        GLOBAL_TRACKER,  // 모든 앱을 통틀어 트래킹
        ECOMMERCE_TRACKER
        // 아마 유료 결재 트래킹 개념 같음
    }

    var mTrackers: HashMap<TrackerName, Tracker> =
        HashMap<TrackerName, Tracker>()

    fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    fun getGlobalApplicationContext(): KJKomicsApp? {
        checkNotNull(instance) { "this application does not inherit com.kakao.GlobalApplication" }
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(applicationContext)
        try {
            val diskCacheConfig: DiskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(File(FILE_ROOT_PATH, "cache"))
                .setBaseDirectoryName("cache")
                .setMaxCacheSize(200 * 1024 * 1024) //200MB
                .build()

            //fresco log
            val requestListeners: MutableSet<RequestListener> =
                HashSet<RequestListener>()
            requestListeners.add(RequestLoggingListener())
            val wm =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            val builder: ImagePipelineConfig.Builder = ImagePipelineConfig.newBuilder(this)
            builder.setMainDiskCacheConfig(diskCacheConfig)
            if (dm.widthPixels < 720) {
                builder.setDownsampleEnabled(true)
            }
            val imagePipelineConfig: ImagePipelineConfig = builder.build()
            Fresco.initialize(this, imagePipelineConfig)
            setRetrofitServer(this)
            if (BuildConfig.DEBUG) {
                Stetho.initializeWithDefaults(this)
                FacebookSdk.setIsDebugEnabled(true)
                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
            }
            setCrashlytics()
            initAppsFlyer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getTracker(trackerId: TrackerName): Tracker? {
        if (!mTrackers.containsKey(trackerId)) {
            val analytics: GoogleAnalytics = GoogleAnalytics.getInstance(this)
            val t: Tracker
            t =
                if (trackerId == TrackerName.APP_TRACKER) analytics.newTracker(PROPERTY_ID) else if (trackerId == TrackerName.GLOBAL_TRACKER) analytics.newTracker(
                    R.xml.global_tracker
                ) else analytics.newTracker(R.xml.ecommerce_tracker)
            mTrackers[trackerId] = t
        }
        return mTrackers[trackerId]
    }


    private fun setCrashlytics() {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
//        val crashlyticsKit: Crashlytics = Crashlytics.Builder()
//            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
//            .build()
//        Fabric.with(this, crashlyticsKit)
    }

    private fun initAppsFlyer() {
        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                for (attrName in conversionData.keys) {
                    Log.d(
                        "LOG_TAG",
                        "attribute: " + attrName + " = " + conversionData[attrName]
                    )
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.d("LOG_TAG", "error getting conversion data: $errorMessage")
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                for (attrName in attributionData.keys) {
                    Log.d(
                        "LOG_TAG",
                        "attribute: " + attrName + " = " + attributionData[attrName]
                    )
                }
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.d("LOG_TAG", "error onAttributionFailure : $errorMessage")
            }
        }
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this)
        AppsFlyerLib.getInstance().startTracking(this)
    }
}