package com.krosskomics.home.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.facebook.drawee.view.SimpleDraweeView
import com.krosskomics.R
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.webview.WebViewActivity

class HomeAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    lateinit var context: Context

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_B, VIEW_TYPE_C, VIEW_TYPE_D, VIEW_TYPE_LINE, VIEW_TYPE_FOOTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        context = parent.context
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_content, parent, false)
        when (viewType) {
            VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_B.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_C.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_D.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content_event, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_LINE.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_underbar, parent, false)
            }
        }
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is DataMainContents) {
            when (item.layout_type) {
                "type1" -> return VIEW_TYPE.VIEW_TYPE_A.ordinal
                "type2" -> return VIEW_TYPE.VIEW_TYPE_B.ordinal
                "type3" -> return VIEW_TYPE.VIEW_TYPE_C.ordinal
                "type4" -> return VIEW_TYPE.VIEW_TYPE_D.ordinal
                "type9" -> return VIEW_TYPE.VIEW_TYPE_LINE.ordinal
                "type8" -> return VIEW_TYPE.VIEW_TYPE_FOOTER.ordinal
            }
        }
        return VIEW_TYPE.VIEW_TYPE_A.ordinal
    }

    private fun setType1View(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type1, null) as LinearLayout

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                val genreTextView = view.findViewById<TextView>(R.id.tv_genre)
                val likeCountTextView = view.findViewById<TextView>(R.id.tv_like_count)

                titleTextView.text = item.title
                genreTextView.text = item.genre1
                likeCountTextView.text = item.like_cnt

                mainImageView.setController(CommonUtil.getDraweeController(context, item.image, 200, 200))

//                if (item.isupdate == "1") {
//                    upImaegeView.setVisibility(View.VISIBLE)
//                } else {
//                    upImaegeView.setVisibility(View.GONE)
//                }
//                if (item.isnew.equals("1")) {
//                    newImaegView.setVisibility(View.VISIBLE)
//                } else {
//                    newImaegView.setVisibility(View.GONE)
//                }

                layout.addView(view)

                view.setOnClickListener {
                    val intent = Intent(context, SeriesActivity::class.java)
                    val b = Bundle()
                    b.putString("sid", item.sid)
                    b.putString("title", item.title)
                    intent.putExtras(b)
                    context.startActivity(intent)
                }
            }
        }
    }

    private fun setType2View(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type2, null) as LinearLayout

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                val genreTextView = view.findViewById<TextView>(R.id.tv_genre)

                val likeCountTextView = view.findViewById<TextView>(R.id.tv_like_count)

                titleTextView.text = item.title
                genreTextView.text = item.genre1
                likeCountTextView.text = item.like_cnt

                mainImageView.setController(CommonUtil.getDraweeController(context, item.image, 200, 200))

                if (item.isupdate == "1") {
//                    upImaegeView.setVisibility(View.VISIBLE)
                } else {
//                    upImaegeView.setVisibility(View.GONE)
                }
//                if (item.isnew.equals("1")) {
//                    newImaegView.setVisibility(View.VISIBLE)
//                } else {
//                    newImaegView.setVisibility(View.GONE)
//                }

                layout.addView(view)

                view.setOnClickListener {
                    val intent = Intent(context, SeriesActivity::class.java)
                    val b = Bundle()
                    b.putString("sid", item.sid)
                    b.putString("title", item.title)
                    intent.putExtras(b)
                    context.startActivity(intent)
                }
            }
        }
    }

    private fun setType3View(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type3, null)

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)

                titleTextView.text = item.title

                mainImageView.controller =
                    CommonUtil.getDraweeController(context, item.image, 200, 200)

                if (item.isupdate == "1") {
//                    upImaegeView.setVisibility(View.VISIBLE)
                } else {
//                    upImaegeView.setVisibility(View.GONE)
                }
//                if (item.isnew.equals("1")) {
//                    newImaegView.setVisibility(View.VISIBLE)
//                } else {
//                    newImaegView.setVisibility(View.GONE)
//                }

                layout.addView(view)

                view.setOnClickListener {
                    val intent = Intent(context, SeriesActivity::class.java)
                    val b = Bundle()
                    b.putString("sid", item.sid)
                    b.putString("title", item.title)
                    intent.putExtras(b)
                    context.startActivity(intent)
                }
            }
        }
    }

    /**
     * 이벤트
     *
     * @param list
     * @param layEvent
     */
    private fun setEventView(list: ArrayList<DataBook>?, layEvent: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        try {
            if (null != list && 0 < list.size) {
                for (i in list.indices) {
                    val item = list[i]
                    val view = inflater.inflate(R.layout.item_main_type4, null) as LinearLayout
                    val ivEvent = view.findViewById<SimpleDraweeView>(R.id.iv_event)
                    ivEvent.controller = CommonUtil.getDraweeController(context, item.image, 200, 200)

                    layEvent.addView(view)

                    view.setOnClickListener {
                        // M:메인, H:작품홈, C:충전(인앱), W:웹뷰, B:브라우저, N:없슴
                        val intent: Intent
                        when (item.atype) {
                            "M" -> {
                            }
                            "H" -> {
                                intent = Intent(context, SeriesActivity::class.java)
                                val b = Bundle()
                                b.putString("sid", item.sid)
                                b.putString("title", item.title)
                                intent.putExtras(b)
                                context.startActivity(intent)
//                                val activity = BookActivity.activity
//                                activity?.finish()
                            }
                            "C" -> if (CommonUtil.read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                                intent = Intent(context, CoinActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                (context as MainActivity).goLoginAlert(context)
                            }
                            "W" -> if ("" != item.link) {
                                intent = Intent(context, WebViewActivity::class.java)
                                intent.putExtra("title", item.subject)
                                intent.putExtra("url", item.link)
                                context.startActivity(intent)
                            }
                            "B" -> if ("" != item.link) {
                                CommonUtil.moveBrowserChrome(context, item.link)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class HomeViewHolder(itemView: View) : BaseItemViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.tv_title)
        val moreButton = itemView.findViewById<Button>(R.id.btn_more)
        val contentLayout = itemView.findViewById<LinearLayout>(R.id.lay_user_update)
        val rankingPager = itemView.findViewById<ViewPager>(R.id.rank_pager)
        val eventLayout = itemView.findViewById<LinearLayout>(R.id.lay_event)

        override fun setData(item: Any?, position: Int) {
            if (item is DataMainContents) {
                titleTextView?.text = item.layout_title
                if ("1".equals(item.show_more)) {
                    moreButton?.visibility = View.VISIBLE
                    moreButton?.setOnClickListener {
//                        val intent = Intent(itemView.context, MoreActivity::class.java)
//                        intent.putExtra("more_param", item.more_param)
//                        context.startActivity(intent)
                    }
                } else {
                    moreButton?.visibility = View.GONE
                }
                when (getItemViewType(position)) {
                    VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                        setType1View(item.list, contentLayout)
                    }
                    VIEW_TYPE.VIEW_TYPE_B.ordinal -> {
                        setType2View(item.list, contentLayout)
                    }
                    VIEW_TYPE.VIEW_TYPE_C.ordinal -> {
                        setType3View(item.list, contentLayout)
                    }
                    VIEW_TYPE.VIEW_TYPE_D.ordinal -> {
                        setEventView(item.list, eventLayout)
                    }
                }
            }
        }
    }
}