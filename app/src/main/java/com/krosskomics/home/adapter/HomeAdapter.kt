package com.krosskomics.home.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.more.activity.MoreActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.webview.WebViewActivity

class HomeAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    lateinit var context: Context

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_B, VIEW_TYPE_C, VIEW_TYPE_D, VIEW_TYPE_E, VIEW_TYPE_LINE, VIEW_TYPE_FOOTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        context = parent.context
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_content_a, parent, false)
        when (viewType) {
            VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content_a, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_B.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content_b, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_C.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content_a, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_D.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content_d, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_E.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_content_e, parent, false)
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
//                "type1" -> return VIEW_TYPE.VIEW_TYPE_E.ordinal
                "type2" -> return VIEW_TYPE.VIEW_TYPE_B.ordinal
                "type3" -> return VIEW_TYPE.VIEW_TYPE_C.ordinal
                "type4" -> return VIEW_TYPE.VIEW_TYPE_D.ordinal
                "type4" -> return VIEW_TYPE.VIEW_TYPE_E.ordinal
                "type9" -> return VIEW_TYPE.VIEW_TYPE_LINE.ordinal
                "type8" -> return VIEW_TYPE.VIEW_TYPE_FOOTER.ordinal
            }
        }
        return VIEW_TYPE.VIEW_TYPE_A.ordinal
    }

    private fun setATypeView(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type_a, null) as LinearLayout

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                val genreTextView = view.findViewById<TextView>(R.id.tv_genre)
                val likeCountTextView = view.findViewById<TextView>(R.id.tv_like_count)

                titleTextView.text = item.title
                genreTextView.text = item.genre1
                likeCountTextView.text = item.like_cnt

                mainImageView.controller =
                    CommonUtil.getDraweeController(context, item.image, 200, 200)

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

    private fun setCTypeView(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type_c, null) as LinearLayout

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)

                val likeCountTextView = view.findViewById<TextView>(R.id.tv_like_count)
                val writerTextView = view.findViewById<TextView>(R.id.tv_writer)

                titleTextView.text = item.title
                likeCountTextView?.text = item.like_cnt

                mainImageView.controller =
                    CommonUtil.getDraweeController(context, item.image, 200, 200)

                if (item.writer1.isNullOrEmpty()) {
                    writerTextView.visibility = View.GONE
                } else {
                    writerTextView.text = item.writer1
                    writerTextView.visibility = View.VISIBLE
                }

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
    private fun setDTypeView(list: ArrayList<DataBook>?, layEvent: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        try {
            if (null != list && 0 < list.size) {
                for (i in list.indices) {
                    val item = list[i]
                    val view = inflater.inflate(R.layout.item_main_type_d, null) as LinearLayout
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

    private fun setBTypeView(items: ArrayList<DataBook>?, recyclerView: RecyclerView) {
        items?.let {
            val layoutManager = GridLayoutManager(context, 4)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0, 1 -> 2
                        else -> 1
                    }
                }
            }
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = HomeETypeAdapter(it, R.layout.item_main_type_b_1, context)
            (recyclerView.adapter as HomeETypeAdapter).setOnItemClickListener(object : HomeETypeAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is DataBook) {
                        val intent = Intent(context, SeriesActivity::class.java).apply {
                            putExtra("sid", item.sid)
                            putExtra("title", item.title)
                        }
                        context.startActivity(intent)
                    }
                }
            })
        }
    }

    /**
     * 이벤트
     *
     * @param list
     * @param layEvent
     */
    private fun setETypeView(list: ArrayList<DataBook>?, layEvent: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        try {
            if (null != list && 0 < list.size) {
                for (i in list.indices) {
                    val item = list[i]
                    val view = inflater.inflate(R.layout.item_main_type_e, null) as LinearLayout
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
        val moreButton = itemView.findViewById<ImageView>(R.id.moreImageView)
        val contentLayout = itemView.findViewById<LinearLayout>(R.id.lay_user_update)
        val eventLayout = itemView.findViewById<LinearLayout>(R.id.lay_event)
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerView)

        override fun setData(item: Any?, position: Int) {
            if (item is DataMainContents) {
                titleTextView?.text = item.layout_title
                if ("1" == item.show_more) {
                    moreButton?.visibility = View.VISIBLE
                    moreButton?.setOnClickListener {
                        val intent = Intent(itemView.context, MoreActivity::class.java)
                        intent.putExtra("title", item.layout_title)
                        intent.putExtra("more_param", item.more_param)
                        context.startActivity(intent)
                    }
                } else {
                    moreButton?.visibility = View.GONE
                }
                when (getItemViewType(position)) {
                    VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                        setATypeView(item.list, contentLayout)
                    }
                    VIEW_TYPE.VIEW_TYPE_B.ordinal -> {
                        setBTypeView(item.list, recyclerView)
                    }
                    VIEW_TYPE.VIEW_TYPE_C.ordinal -> {
                        setCTypeView(item.list, contentLayout)
                    }
                    VIEW_TYPE.VIEW_TYPE_D.ordinal -> {
                        setDTypeView(item.list, eventLayout)
                    }
                    VIEW_TYPE.VIEW_TYPE_E.ordinal -> {
                        setETypeView(item.list, eventLayout)
                    }
                }
            }
        }
    }
}