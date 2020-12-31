package com.krosskomics.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataLoginGenre
import com.krosskomics.common.holder.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_info_genre.view.*

class InfoGenreAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<InfoGenreAdapter.CustomItemHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_info_genre, parent, false)
        return CustomItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    inner class CustomItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            item?.let {
                if (it is DataLoginGenre) {
                    itemView.apply {
                        Glide.with(itemView.context)
                            .load(it.image)
                            .into(genreImageView)

                        genreTextView.text = it.genre_text

                        isSelected = it.isSelect

                        setOnClickListener {
                            onClickListener?.onItemClick(item)
                        }
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}