package com.krosskomics.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.data.DataAge
import com.krosskomics.common.holder.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_info_age.view.*

class InfoAgeAdapter(private val items: ArrayList<*>) :
    RecyclerView.Adapter<InfoAgeAdapter.CustomItemHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_info_age, parent, false)
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
                if (it is DataAge) {
                    itemView.apply {
                        ageTextView.text = it.age_text
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