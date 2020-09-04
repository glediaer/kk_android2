package com.krosskomics.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.home.adapter.HomeBannerAdapter.*
import kotlinx.android.synthetic.main.item_home_banner.view.*

class HomeBannerAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<HomeBannerItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_banner, parent, false)
        return HomeBannerItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeBannerItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    inner class HomeBannerItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            if (item is DataBanner) {
                Glide.with(itemView.context)
                    .load(item.image)
                    .into(itemView.mainImageView)
            }
        }
    }
}