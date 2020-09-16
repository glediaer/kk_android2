package com.krosskomics.genre.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.facebook.drawee.view.SimpleDraweeView
import com.krosskomics.R
import com.krosskomics.book.activity.BookActivity
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.genre.activity.GenreDetailActivity
import com.krosskomics.home.activity.MainActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.webview.WebViewActivity

class GenreAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<GenreAdapter.RankingViewHolder>() {

    lateinit var context: Context

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_B, VIEW_TYPE_C, VIEW_TYPE_D, VIEW_TYPE_LINE, VIEW_TYPE_FOOTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        context = parent.context
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_content, parent, false)
        when (viewType) {
            VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_genre_content, parent, false)
            }
            VIEW_TYPE.VIEW_TYPE_LINE.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_underbar, parent, false)
            }
        }
        return RankingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is DataMainContents) {
            when (item.layout_type) {
                "type1" -> return VIEW_TYPE.VIEW_TYPE_A.ordinal
                "type9" -> return VIEW_TYPE.VIEW_TYPE_LINE.ordinal
            }
        }
        return VIEW_TYPE.VIEW_TYPE_A.ordinal
    }

    private fun setType1View(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type2, null) as LinearLayout

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                val writerTextView = view.findViewById<TextView>(R.id.tv_writer)
                val likeCountTextView = view.findViewById<TextView>(R.id.tv_like_count)

                titleTextView.setText(item.title)
                writerTextView.setText(item.writer1)
                likeCountTextView.setText(item.like_cnt)

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
                    val intent = Intent(context, BookActivity::class.java)
                    val b = Bundle()
                    b.putString("sid", item.sid)
                    b.putString("title", item.title)
                    intent.putExtras(b)
                    context.startActivity(intent)
                }
            }
        }
    }

    inner class RankingViewHolder(itemView: View) : BaseItemViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.tv_title)
        val moreButton = itemView.findViewById<ImageView>(R.id.moreImageView)
        val contentLayout = itemView.findViewById<LinearLayout>(R.id.lay_user_update)

        override fun setData(item: Any?, position: Int) {
            if (item is DataMainContents) {
                titleTextView?.text = item.layout_title
                if ("1".equals(item.show_more)) {
                    moreButton?.visibility = View.VISIBLE
                    moreButton?.setOnClickListener {
                        val intent = Intent(itemView.context, GenreDetailActivity::class.java)
                        intent.putExtra("more_param", item.more_param)
                        context.startActivity(intent)
                    }
                } else {
                    moreButton?.visibility = View.GONE
                }
                when (getItemViewType(position)) {
                    VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                        setType1View(item.list, contentLayout)
                    }
                }
            }
        }
    }
}