package com.krosskomics.home.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.home.adapter.HomeBannerAdapter.HomeBannerItemHolder
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.goLoginAlert
import com.krosskomics.webview.WebViewActivity
import kotlinx.android.synthetic.main.item_home_banner.view.*

class HomeBannerAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<HomeBannerItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_banner, parent, false)
        return HomeBannerItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeBannerItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    inner class HomeBannerItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                if (item is DataBanner) {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .into(itemView.mainImageView)

                    setOnClickListener {
                        // M:메인, H:작품홈, C:충전(인앱), W:웹뷰, B:브라우저, N:없슴
                        when (item.atype) {
                            "H" -> {
                                Intent(context, SeriesActivity::class.java).run {
                                    val bundle = Bundle()
                                    bundle.putString("sid", item.sid)
                                    bundle.putString("title", item.title)
                                    putExtras(bundle)
                                    itemView.context.startActivity(this)
                                }
                            }
                            "C" -> {
                                if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N") == "Y") {
                                    itemView.context.startActivity(Intent(context, CoinActivity::class.java))
                                } else {
                                    goLoginAlert(context);
                                }
                            }
                            "W" -> {
                                item.link?.let {
                                    Intent(context, WebViewActivity::class.java).run {
                                        putExtra("title", item.subject)
                                        putExtra("url", item.link)
                                        itemView.context.startActivity(this)
                                    }
                                }
                            }
                            "B" -> {
                                item.link?.let {
                                    CommonUtil.moveBrowserChrome(context, item.link)
                                }
                            }
                            "S" -> {
                                if (!CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y")) {
                                    CommonUtil.moveSignUp(context);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}