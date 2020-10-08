package com.krosskomics.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

class CommonRecyclerViewAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
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

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}