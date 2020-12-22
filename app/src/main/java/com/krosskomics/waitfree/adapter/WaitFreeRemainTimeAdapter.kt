package com.krosskomics.waitfree.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.holder.BaseItemViewHolder

class WaitFreeRemainTimeAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<WaitFreeRemainTimeAdapter.WaitFreeTimeViewHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaitFreeTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_waitfree_remain_time, parent, false)

        return WaitFreeTimeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: WaitFreeTimeViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    inner class WaitFreeTimeViewHolder(itemView: View) : BaseItemViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.tv_title)
        val moreButton = itemView.findViewById<ImageView>(R.id.moreImageView)
        val contentLayout = itemView.findViewById<LinearLayout>(R.id.lay_user_update)

        override fun setData(item: Any?, position: Int) {
            if (item is DataMainContents) {
                itemView.apply {
                    setOnClickListener {
                        onClickListener?.onItemClick(item, position)
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?, position: Int)
    }
}