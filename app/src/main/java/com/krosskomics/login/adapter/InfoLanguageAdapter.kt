package com.krosskomics.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataGenre
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.data.DataLanguage
import kotlinx.android.synthetic.main.item_info_genre.view.*
import kotlinx.android.synthetic.main.item_info_language.view.*

class InfoLanguageAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<InfoLanguageAdapter.CustomItemHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_info_language, parent, false)
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
                if (it is DataLanguage) {
                    itemView.apply {
                        languageTextView.isSelected = it.isSelect
                        languageTextView.text = it.lang_text
                        languageTextView.setOnClickListener {
                            onClickListener?.onItemClick(item, position)
                        }
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?, position: Int)
    }
}