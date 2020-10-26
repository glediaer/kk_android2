package com.krosskomics.notice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter

class NoticeAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerViewBaseAdapter(items, layoutRes) {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewBaseAdapter.BaseItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return BaseItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerViewBaseAdapter.BaseItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

//    inner class CustomItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
//        override fun setData(item: Any?) {
//            if (item is DataBook) {
//                itemView.apply {
//                    Glide.with(itemView.context)
//                        .load(item.image)
//                        .into(mainImageView)
//
//                    titleTextView.text = item.title
//                    writerTextView.text = item.writer1
//                    genreTextView.text = item.genre1
//                    likeCountTextView.text = item.like_cnt
//
//                    setOnClickListener {
//                        onClickListener?.onItemClick(item)
//                    }
//                }
//            }
//        }
//    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}