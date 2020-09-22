package com.krosskomics.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataCoin
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.ranking.activity.RankingDetailActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.item_coin.view.*
import kotlinx.android.synthetic.main.item_genre_detail.view.*
import kotlinx.android.synthetic.main.item_home_banner.view.mainImageView
import kotlinx.android.synthetic.main.item_ongoing.view.*
import kotlinx.android.synthetic.main.item_ongoing.view.genreTextView
import kotlinx.android.synthetic.main.item_ongoing.view.titleTextView
import kotlinx.android.synthetic.main.item_ongoing.view.writerTextView
import kotlinx.android.synthetic.main.item_ranking.view.*
import kotlinx.android.synthetic.main.item_ranking_detail.view.*
import kotlinx.android.synthetic.main.item_series.view.*
import kotlinx.android.synthetic.main.view_content_tag_right.view.*

open class RecyclerViewBaseAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerView.Adapter<RecyclerViewBaseAdapter.BaseItemHolder>() {

    private var onClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return BaseItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    fun setOnDelteItemClickListener(onItemClickListener: OnDeleteItemClickListener) {
        this.onDeleteClickListener = onItemClickListener
    }

    inner class BaseItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                setOnClickListener {
                    onClickListener?.onItemClick(item)
                }
                if (item is DataBook) {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .into(mainImageView)

                    titleTextView?.text = item.title
                    writerTextView?.text = item.writer1
                    genreTextView?.text = item.genre1
                    likeCountTextView?.text = CommonUtil.likeCountFormat(itemView.context, item.like_cnt)

                    // ranking
                    if (context is RankingDetailActivity) {
                        when(position) {
                            0 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_1)
                            1 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_2)
                            2 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_3)
                        }
                    } else {
                        rankingTextView?.text = (position + 1).toString()
                    }

                    // genre
                    subscribeImageView?.setOnClickListener {
                        subscribeImageView.isSelected = !it.isSelected
                    }

                    // library
                    if (item.isCheckVisible) {
                        deleteView?.visibility = View.VISIBLE
                        deleteView?.isEnabled = item.isChecked
                        deleteView.setOnClickListener {
                            onDeleteClickListener?.onItemClick(item)
                        }
                    } else {
                        deleteView?.visibility = View.GONE
                    }
                } else if (item is DataEpisode) {
                    img_ep_title.controller = CommonUtil.getDraweeController(context, item.image,
                        200, 200)
                    txt_ep_title.text = item.ep_title
                    txt_update.text = item.ep_show_date
                    txt_showDate.text = item.show_str
                } else if (item is String) { // viewer
                    img_ep_title.controller = CommonUtil.getDraweeController(context, item,
                        200, 200)
                } else if (item is DataCoin) {
                    tv_coin.text = item.product_name
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }

    interface OnDeleteItemClickListener {
        fun onItemClick(item: Any)
    }
}