package com.krosskomics.home.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.data.DataBanner
import com.krosskomics.login.activity.LoginActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.getDeviceWidth
import com.krosskomics.util.CommonUtil.moveBrowserChrome
import com.krosskomics.util.CommonUtil.moveSignUp
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.webview.WebViewActivity
import java.util.*

class MainBannerPagerAdapter(val context: Context?,
                             val items: ArrayList<DataBanner>?,
                             val isMaxValue: Boolean) : PagerAdapter() {
    private val TAG = "MainBannerPagerAdapter"

    override fun getCount(): Int {
        return if (isMaxValue) {
            Int.MAX_VALUE
        } else {
            items!!.size
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_home_banner, container, false)
        val iv_main: SimpleDraweeView = view.findViewById(R.id.iv_main)
        if (null != items && 0 < items!!.size) {
            val pos = position % items!!.size
            val item: DataBanner = items!![pos]
            val params = iv_main.layoutParams

            iv_main.controller = CommonUtil.getDraweeController(
                context,
                item.image,
                getDeviceWidth(context!!),
                params.height
            )
            iv_main.setOnClickListener { // M:메인, H:작품홈, C:충전(인앱), W:웹뷰, B:브라우저, N:없슴
                val intent: Intent
                when (item.atype) {
                    "M" -> {
                    }
                    "H" -> {
                        intent = Intent(context, SeriesActivity::class.java)
                        val b = Bundle()
                        b.putString("cid", item.sid)
                        b.putString("title", item.subject)
                        intent.putExtras(b)
                        context.startActivity(intent)
                    }
                    "C" -> if (read(context, CODE.LOCAL_loginYn, "N")
                            .equals("Y", ignoreCase = true)
                    ) {
                        intent = Intent(context, CoinActivity::class.java)
                        context!!.startActivity(intent)
                    } else {
                        goLoginAlert(context)
                    }
                    "W" -> if ("" != item.link) {
                        intent = Intent(context, WebViewActivity::class.java)
                        intent.putExtra("title", item.subject)
                        intent.putExtra("url", item.link)
                        context.startActivity(intent)
                    }
                    "B" -> if ("" != item.link) {
                        moveBrowserChrome(context!!, item.link)
                    }
                    "S" -> if (!read(context, CODE.LOCAL_loginYn, "N")
                            .equals("Y", ignoreCase = true)
                    ) {
                        moveSignUp(context!!)
                    }
                }
            }
            container.addView(view, 0)
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    fun goLoginAlert(context: Context?) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("pageType", "login")
        context!!.startActivity(intent)
    }
}