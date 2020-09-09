package com.krosskomics.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.holder.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_home_banner.view.mainImageView
import kotlinx.android.synthetic.main.item_ongoing.view.*
import kotlinx.android.synthetic.main.item_ongoing.view.genreTextView
import kotlinx.android.synthetic.main.item_ongoing.view.titleTextView
import kotlinx.android.synthetic.main.item_ranking.view.*
import kotlinx.android.synthetic.main.view_content_tag_right.view.*

open class RecyclerViewBaseAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<RecyclerViewBaseAdapter.BaseItemHolder>() {

    private var onClickListener: OnItemClickListener? = null

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
                    likeCountTextView?.text = item.like_cnt

                    // ranking
                    rankingTextView?.text = (position + 1).toString()

                    setOnClickListener {
                        onClickListener?.onItemClick(item)
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}