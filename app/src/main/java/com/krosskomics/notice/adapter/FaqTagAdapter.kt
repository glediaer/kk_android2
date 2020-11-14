package com.krosskomics.notice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.common.data.DataTag
import com.krosskomics.common.holder.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_search_tag.view.*

class FaqTagAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerView.Adapter<FaqTagAdapter.ViewerViewHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    inner class ViewerViewHolder(itemView: View) : BaseItemViewHolder(itemView) {

        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                setOnClickListener {
                    onClickListener?.onItemClick(item, position)
                }
                if (item is DataTag) {
                    tagTextView.text = item.tag_title
                    isSelected = item.isSelect
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?, position: Int)
    }
}