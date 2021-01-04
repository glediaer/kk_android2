package com.krosskomics.mainmenu.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.krosskomics.R
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataGenre
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.genre.activity.GenreDetailActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.view_new_up_tag.view.*
import kotlinx.android.synthetic.main.view_remain_tag.view.*

class GenreAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    lateinit var context: Context

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_LINE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        context = parent.context
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_content_a, parent, false)
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
        return GenreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is DataGenre) {
            when (position % 2) {
                0 -> return VIEW_TYPE.VIEW_TYPE_A.ordinal
                1 -> return VIEW_TYPE.VIEW_TYPE_LINE.ordinal
            }
        }
        return VIEW_TYPE.VIEW_TYPE_A.ordinal
    }

    private fun setType1View(list: ArrayList<DataBook>?, layout: LinearLayout) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (null != list && 0 < list.size) {
            for (i in list.indices) {
                val item = list[i]
                val view = inflater.inflate(R.layout.item_main_type_c, null) as LinearLayout

                val mainImageView = view.findViewById<SimpleDraweeView>(R.id.iv_main)

                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                val writerTextView = view.findViewById<TextView>(R.id.tv_writer)
//                val likeCountTextView = view.findViewById<TextView>(R.id.tv_like_count)
                val contentTagView = view.findViewById<View>(R.id.remainTagView)
                val newTagImageView = view.findViewById<ImageView>(R.id.newTagImageView)
                val upTagImageView = view.findViewById<ImageView>(R.id.upTagImageView)
                val remainTagView = view.findViewById<View>(R.id.remainTagView)
                val remainTagTextView = view.findViewById<TextView>(R.id.remainTagTextView)

                titleTextView.text = item.title
                if (item.like_cnt.isNullOrEmpty()) {
                    contentTagView?.visibility = View.GONE
                } else {
                    contentTagView?.visibility = View.VISIBLE
//                    likeCountTextView.text = item.like_cnt
                }
                if (item.writer1.isNullOrEmpty()) {
                    writerTextView?.visibility = View.GONE
                } else {
                    writerTextView?.text = item.writer1
                    writerTextView?.visibility = View.VISIBLE
                }
                if (item.isnew == "1") {
                    newTagImageView?.visibility = View.VISIBLE
                    upTagImageView?.visibility = View.GONE
                } else {
                    newTagImageView?.visibility = View.GONE
                    if (item.isupdate == "1") {
                        upTagImageView?.visibility = View.VISIBLE
                    } else {
                        upTagImageView?.visibility = View.GONE
                    }
                }
                // 기다무
                if (item.iswop == "1") {
                    remainTagView.visibility = View.VISIBLE
                    remainTagView?.isSelected = false
                    remainTagTextView?.text = item.dp_wop_term
                } else {
                    if (item.dp_pub_day.isNullOrEmpty()) {
                        remainTagView.visibility = View.GONE
                    } else {
                        remainTagView?.isSelected = true
                        remainTagTextView?.text = item.dp_pub_day
                        remainTagView.visibility = View.VISIBLE
                    }
                }

                mainImageView.controller =
                    CommonUtil.getDraweeController(context, item.image, 200, 200)

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

    inner class GenreViewHolder(itemView: View) : BaseItemViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.tv_title)
        val moreButton = itemView.findViewById<ImageView>(R.id.moreImageView)
        val contentLayout = itemView.findViewById<LinearLayout>(R.id.lay_user_update)

        override fun setData(item: Any?, position: Int) {
            if (item is DataGenre) {
                titleTextView?.text = item.dp_genre
//                if ("1".equals(item.show_more)) {
                    moreButton?.visibility = View.VISIBLE
                    moreButton?.setOnClickListener {
                        val intent = Intent(itemView.context, GenreDetailActivity::class.java)
                        intent.putExtra("more_param", item.p_genre)
                        intent.putExtra("listType", "genre")
                        context.startActivity(intent)
                    }
//                } else {
//                    moreButton?.visibility = View.GONE
//                }
                when (getItemViewType(position)) {
                    VIEW_TYPE.VIEW_TYPE_A.ordinal -> {
                        setType1View(item.genre_list, contentLayout)
                    }
                }
//                setType1View(item.genre_list, contentLayout)
            }
        }
    }
}