package com.krosskomics.ongoing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.krosskomics.R
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter

class OnGoingAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerViewBaseAdapter(items) {

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

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}