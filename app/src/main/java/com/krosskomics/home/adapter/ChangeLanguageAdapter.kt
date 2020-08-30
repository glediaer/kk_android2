package com.krosskomics.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.data.DataLanguage

class ChangeLanguageAdapter(val items: MutableList<DataLanguage>) :
    RecyclerView.Adapter<ChangeLanguageAdapter.MainViewHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_change_language, null)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        items.let {
            holder.languageTextView.text = it[position].lang_text
            holder.languageTextView.setOnClickListener { view ->
                this.onClickListener?.onItemClick(view, position)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val languageTextView = itemView.findViewById<TextView>(R.id.tv_language)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}