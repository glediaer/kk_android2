package com.krosskomics.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.ranking.activity.RankingActivity
import com.krosskomics.ranking.activity.RankingDetailActivity
import com.krosskomics.util.CommonUtil
import kotlinx.android.synthetic.main.item_genre_detail.view.*
import kotlinx.android.synthetic.main.item_home_banner.view.mainImageView
import kotlinx.android.synthetic.main.item_ongoing.view.*
import kotlinx.android.synthetic.main.item_ongoing.view.genreTextView
import kotlinx.android.synthetic.main.item_ongoing.view.titleTextView
import kotlinx.android.synthetic.main.item_ongoing.view.writerTextView
import kotlinx.android.synthetic.main.item_ranking.view.*
import kotlinx.android.synthetic.main.item_ranking.view.rankingTextView
import kotlinx.android.synthetic.main.item_ranking_detail.view.*
import kotlinx.android.synthetic.main.view_content_tag_right.view.*

open class RecyclerViewBaseAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<RecyclerViewBaseAdapter.BaseItemHolder>() {

    private var onClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ongoing, parent, false)
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
            if (item is DataBook) {
                itemView.apply {
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

                    setOnClickListener {
                        onClickListener?.onItemClick(item)
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