package com.krosskomics.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.holder.BaseItemViewHolder

class HomeAdapter(private val items: ArrayList<*>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_banner, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.setData(items[position])
    }

    inner class HomeViewHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?) {

        }
    }
}