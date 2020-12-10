package com.krosskomics.home.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.data.DataBanner
import com.krosskomics.home.activity.HomePopupActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.goLoginAlert
import com.krosskomics.util.CommonUtil.moveBrowserChrome
import com.krosskomics.util.CommonUtil.moveSignUp
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.webview.WebViewActivity

class EventPagerAdapter(val eventList: ArrayList<DataBanner>) : PagerAdapter() {
    override fun getCount(): Int {
        return eventList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(container.context).inflate(
            R.layout.dialog_main_popup,
            null
        )
        if (0 < eventList.size) {
            val item: DataBanner = eventList.get(position)
            val ivMain: SimpleDraweeView = view.findViewById(R.id.iv_main)
            ivMain.setImageURI(item.image)
            ivMain.setOnClickListener { // M:메인, H:작품홈, C:충전(인앱), W:웹뷰, B:브라우저, N:없슴
                CommonUtil.setBannerAction(container.context, item)
                (container.context as HomePopupActivity).finish()
            }
            container.addView(view)
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}