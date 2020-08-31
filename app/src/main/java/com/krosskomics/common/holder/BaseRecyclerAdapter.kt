package com.krosskomics.common.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R

open class BaseRecyclerAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<BaseItemViewHolderImpl>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseItemViewHolderImpl {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_banner, parent, false)
        return BaseItemViewHolderImpl(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseItemViewHolderImpl, position: Int) {
        holder.setData(items[position])
    }
}