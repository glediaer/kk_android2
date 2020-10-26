package com.krosskomics.util

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.restful.InterfaceRestful
import com.krosskomics.util.CommonUtil.getAppVersion
import com.krosskomics.util.CommonUtil.getRandomString
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.write
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ServerUtil {
    private val TAG = ServerUtil::class.java.simpleName
    lateinit var service: InterfaceRestful
    fun setRetrofitServer(context: Context?) {
        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpBuilder.addNetworkInterceptor(StethoInterceptor())
        }
        if (TextUtils.isEmpty(read(context, CODE.LOCAL_APP_SECRET, ""))) {
            val randomString = getRandomString(10)
            write(context, CODE.LOCAL_APP_SECRET, randomString)
            if (BuildConfig.DEBUG) {
                Log.e(
                    "ServerUtil",
                    "secret : " + read(context, CODE.LOCAL_APP_SECRET, "")
                )
            }
        }
        okHttpBuilder.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("KK-APP-KEY", CODE.AUTH_KEY)
                .header("KK-APP-DEVICEID", read(context, CODE.LOCAL_Android_Id, ""))
                .header("KK-APP-SECRET", read(context, CODE.LOCAL_APP_SECRET, ""))
                .header("KK-APP-VERSION", getAppVersion(context!!))
                .header("KK-APP-TOKEN", KJKomicsApp.APPTOKEN)
                .header("KK-U-TOKEN", read(context, CODE.LOCAL_ENC_USER_NO, ""))
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpBuilder.build()
        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(KJKomicsApp.getServerUrl(context))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()
        service = retrofit.create(InterfaceRestful::class.java)
    }

    fun resetRetrofitServer(context: Context?) {
        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpBuilder.addNetworkInterceptor(StethoInterceptor())
        }
        if (TextUtils.isEmpty(read(context, CODE.LOCAL_APP_SECRET, ""))) {
            val randomString = getRandomString(10)
            write(context, CODE.LOCAL_APP_SECRET, randomString)
        }
        okHttpBuilder.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("KK-APP-KEY", CODE.AUTH_KEY)
                .header("KK-APP-DEVICEID", read(context, CODE.LOCAL_Android_Id, ""))
                .header("KK-APP-SECRET", read(context, CODE.LOCAL_APP_SECRET, ""))
                .header("KK-APP-VERSION", getAppVersion(context!!))
                .header("KK-APP-TOKEN", KJKomicsApp.APPTOKEN)
                .header("KK-U-TOKEN", read(context, CODE.LOCAL_ENC_USER_NO, ""))
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpBuilder.build()
        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(KJKomicsApp.getServerUrl(context))
            .addConverterFactory(GsonConverterFactory.create()).build()
        service = retrofit.create(InterfaceRestful::class.java)
    }

//    @Synchronized
//    fun getService(context: Context?): InterfaceRestful? {
//        val curMillis = System.currentTimeMillis()
//        val diffMilliSec = curMillis - KJKomicsApp.APPTOKEN_RECIEVE_TIME
//        val diffMinute = (diffMilliSec / (1000 * 60) % 60).toInt() //분
//        if (BuildConfig.DEBUG) {
//            Log.e(
//                TAG,
//                "KJKomicsApp.APPTOKEN_RECIEVE_TIME : " + KJKomicsApp.APPTOKEN_RECIEVE_TIME
//            )
//            Log.e(TAG, "diffMilliSec: $diffMilliSec")
//            Log.e(TAG, "diffMinute: $diffMinute")
//        }
//        if (diffMinute > 30) {
//            if (BuildConfig.DEBUG) {
//                Log.e(TAG, "diffMinute > 30")
//            }
//            // 토큰 시간 초과
//            requestAppToken(context)
//        }
//        return service
//    }

//    fun requestAppToken(context: Context?) {
//        val api: Call<AppToken?>? =
//            service.getCheckApp(read(context, CODE.LOCAL_Android_Id, ""))
//        api!!.enqueue(object : Callback<AppToken> {
//            override fun onResponse(
//                @NonNull call: Call<AppToken>,
//                @NonNull response: Response<AppToken>
//            ) {
//                try {
//                    if (response.isSuccessful()) {
//                        if (!TextUtils.isEmpty(response.body().app_token) &&
//                            "0" != response.body().app_token
//                        ) {
//                            if (BuildConfig.DEBUG) {
//                                Log.e(
//                                    TAG,
//                                    "LOCAL_Android_Id : " + read(
//                                        context,
//                                        CODE.LOCAL_Android_Id,
//                                        ""
//                                    )
//                                )
//                                Log.e(
//                                    TAG,
//                                    "app_token : " + response.body().app_token
//                                )
//                            }
//                            KJKomicsApp.APPTOKEN = response.body().app_token
//                            setRetrofitServer(context)
//                            KJKomicsApp.APPTOKEN_RECIEVE_TIME = System.currentTimeMillis()
//                        }
//                        if (BuildConfig.DEBUG) {
//                            Log.e(
//                                TAG,
//                                "KJKomicsApp.APPTOKEN : " + KJKomicsApp.APPTOKEN
//                            )
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onFailure(
//                @NonNull call: Call<AppToken>,
//                @NonNull t: Throwable
//            ) {
//                try {
//                    Log.e(TAG, "onFailure : " + t.message)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        })
//    }
}