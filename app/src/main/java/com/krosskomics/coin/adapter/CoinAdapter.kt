package com.krosskomics.coin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.krosskomics.R
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter

class CoinAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerViewBaseAdapter(items, layoutRes) {

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_B
    }

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewBaseAdapter.BaseItemHolder {
        return when(viewType) {
            VIEW_TYPE.VIEW_TYPE_A.ordinal ->
                BaseItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_coin_first, parent, false))
            else ->
                BaseItemHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerViewBaseAdapter.BaseItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return VIEW_TYPE.VIEW_TYPE_A.ordinal
        }
        return VIEW_TYPE.VIEW_TYPE_B.ordinal
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}