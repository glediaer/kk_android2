package com.krosskomics.home.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.BaseActivity
import com.krosskomics.common.data.DataBanner
import com.krosskomics.home.adapter.EventPagerAdapter
import kotlinx.android.synthetic.main.activity_event_popup.*
import java.util.*

class HomePopupActivity : BaseActivity() {
    private val TAG = "HomePopupActivity"

    private val eventList = ArrayList<DataBanner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.5f // 투명도 0 ~ 1

        window.attributes = layoutParams

        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_event_popup
    }

    override fun initModel() {
        try {
            if (null != KJKomicsApp.INIT_SET.banner_list && 0 < KJKomicsApp.INIT_SET.banner_list!!.size) {
                for (i in KJKomicsApp.INIT_SET.banner_list!!.indices) {
                    val data: DataBanner = KJKomicsApp.INIT_SET.banner_list!![i]
                    if (TextUtils.isEmpty(data.image)) {
                        break
                    }
                    eventList.add(data)
                }
                when (eventList.size) {
                    0 -> {
                        finish()
                    }
                    1 -> {
                        progressBar.visibility = View.GONE
                    }
                    else -> {
                        progressBar.visibility = View.VISIBLE
                        progressBar.progress = eventList.size
                    }
                }
            } else {
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initLayout() {
        setPager()
    }

    override fun requestServer() {}

    override fun initTracker() {}

    override fun initErrorView() {}

    private fun setPager() {
        pager.adapter = EventPagerAdapter(eventList)
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }
}