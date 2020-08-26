package com.krosskomics.splash

import android.content.Context
import android.text.TextUtils
import androidx.annotation.NonNull
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.inteface.BaseDataCallBack
import com.krosskomics.common.model.Login
import com.krosskomics.common.model.Version
import com.krosskomics.restful.InterfaceRestful
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil
import com.krosskomics.util.ServerUtil.setRetrofitServer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashRequest(private val context: Context) {
    fun requestAppVersion(callback: BaseDataCallBack<Version?>) {
        val version: Call<Version?> = ServerUtil.service.getVersion
        version.enqueue(object : Callback<Version?> {
            override fun onResponse(
                @NonNull call: Call<Version?>,
                @NonNull response: Response<Version?>
            ) {
                try {
                    if (response.isSuccessful()) {
                        callback.onResultForData(response.body())
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
                @NonNull call: Call<Version?>,
                @NonNull t: Throwable
            ) {
                t.printStackTrace()
                showToast(context.getString(R.string.msg_error_server), context)
            }
        })
    }

    /**
     * 각 타입별 로그인 요청
     * @param loginType
     */
    fun requestLoginId(loginType: String?, callback: BaseDataCallBack<Login?>) {
        val api: Call<Login?> = ServerUtil.service!!.setAutoLogin(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            loginType,
            read(context, CODE.LOCAL_token, "")
        )
        api.enqueue(object : Callback<Login?> {
            override fun onResponse(
                @NonNull call: Call<Login?>,
                @NonNull response: Response<Login?>
            ) {
                try {
                    if (response.isSuccessful()) {
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
                                callback.onResultForData(loginData)
                            } else {
                                write(context, CODE.LOCAL_ENC_USER_NO, "")
                            }
                        }
                    } else {
                        showToast(context.getString(R.string.msg_fail_login), context)
                        callback.onResultForData(null)
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
                callback.onResultForData(null)
            }
        })
    }

}