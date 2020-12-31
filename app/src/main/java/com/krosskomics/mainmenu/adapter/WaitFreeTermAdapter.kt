package com.krosskomics.mainmenu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.data.DataMainContents
import com.krosskomics.common.data.DataWaitFreeTerm
import com.krosskomics.common.holder.BaseItemViewHolder

class WaitFreeTermAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<WaitFreeTermAdapter.WaitFreeTimeViewHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaitFreeTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_waitfree_term, parent, false)

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
        val titleTextView = itemView.findViewById<TextView>(R.id.waitFreeTermTextView)

        override fun setData(item: Any?, position: Int) {
            if (item is DataWaitFreeTerm) {
                itemView.apply {
                    setOnClickListener {
                        onClickListener?.onItemClick(item, position)
                    }
                    titleTextView.text = item.dp_wop_term_num + "\n" + item.dp_wop_term_text
                    isSelected = item.isSelect
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?, position: Int)
    }
}