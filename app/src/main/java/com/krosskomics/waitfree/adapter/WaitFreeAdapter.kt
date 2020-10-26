package com.krosskomics.waitfree.adapter

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
import kotlinx.android.synthetic.main.view_content_tag_right.view.*

class WaitFreeAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<WaitFreeAdapter.CustomItemHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ongoing, parent, false)
        return CustomItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    inner class CustomItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            if (item is DataBook) {
                itemView.apply {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .into(mainImageView)

                    titleTextView.text = item.title
                    writerTextView.text = item.writer1
                    genreTextView.text = item.genre1
                    likeCountTextView.text = item.like_cnt

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