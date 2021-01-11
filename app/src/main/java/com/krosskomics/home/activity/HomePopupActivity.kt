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
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.write
import kotlinx.android.synthetic.main.activity_event_popup.*
import java.util.*

class HomePopupActivity : BaseActivity() {
    private val TAG = "HomePopupActivity"

    private val eventList = ArrayList<DataBanner>()
    private var currentPage = 1

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
                    0 -> finish()
                    1 -> indicatorView.visibility = View.GONE
                    else -> {
                        indicatorView.visibility = View.VISIBLE
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
        currentPageTextView.text = "1"
        totalPageTextView.text = "${eventList.size}"
        prevImageView.setOnClickListener {
            currentPage = currentPageTextView.text.toString().toInt()
            if (currentPage <= 1) {
                return@setOnClickListener
            }
            currentPageTextView.text = currentPage--.toString()
            pager.currentItem = currentPage - 1
        }
        nextImageView.setOnClickListener {
            currentPage = currentPageTextView.text.toString().toInt()
            if (currentPage >= eventList.size) {
                return@setOnClickListener
            }
            currentPageTextView.text = currentPage++.toString()
            pager.currentItem = currentPage - 1
        }

        dontShowTextView.setOnClickListener {
            // 하루 안보기
            // 오늘 날짜 데이터
            val curDate = Date()
            val curMillis = curDate.time

            write(context, CODE.FLOATING_BANNER_CLOSE_TIME, curMillis.toString())
            finish()
        }
        closeButton.setOnClickListener {
            finish()
        }
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