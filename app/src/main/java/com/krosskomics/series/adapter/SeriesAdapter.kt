package com.krosskomics.series.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.krosskomics.R
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.ranking.activity.RankingActivity

class SeriesAdapter(private val items: ArrayList<*>, private val layoutRes: Int, private val context: Context) :
    RecyclerViewBaseAdapter(items) {

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_B, VIEW_TYPE_C
    }

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewBaseAdapter.BaseItemHolder {
        return when(viewType) {
            VIEW_TYPE.VIEW_TYPE_A.ordinal ->
                BaseItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ranking_first, parent, false))
            VIEW_TYPE.VIEW_TYPE_C.ordinal ->
                BaseItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ranking_detail, parent, false))
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
        if (context is RankingActivity) {
            if (position == 0) {
                return VIEW_TYPE.VIEW_TYPE_A.ordinal
            }
        } else {
            if (position < 3) {
                return VIEW_TYPE.VIEW_TYPE_C.ordinal
            }
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