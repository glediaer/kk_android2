package com.krosskomics.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.more.activity.MoreActivity

class HomeETypeAdapter(private val items: ArrayList<*>, private val layoutRes: Int, private val context: Context) :
    RecyclerView.Adapter<HomeETypeAdapter.HomeETypeViewHolder>() {

    enum class VIEW_TYPE {
        VIEW_TYPE_A, VIEW_TYPE_B
    }

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeETypeAdapter.HomeETypeViewHolder {
        return when(viewType) {
            VIEW_TYPE.VIEW_TYPE_A.ordinal ->
                HomeETypeViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
            VIEW_TYPE.VIEW_TYPE_B.ordinal ->
                HomeETypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_type_b_2, parent, false))
            else ->
                HomeETypeViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeETypeViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 1 -> VIEW_TYPE.VIEW_TYPE_A.ordinal
            else -> VIEW_TYPE.VIEW_TYPE_B.ordinal
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }

    inner class HomeETypeViewHolder(itemView: View) : BaseItemViewHolder(itemView) {
        val mainImageView = itemView.findViewById<ImageView>(R.id.mainImageView)
        val dimView = itemView.findViewById<View>(R.id.dimView)

        override fun setData(item: Any?, position: Int) {
            if (item is DataBook) {
                itemView.setOnClickListener {
                    onClickListener?.onItemClick(item)
                }
                mainImageView?.let {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .into(it)
                }
                if (items.size > 5 && position == 5) {
                    dimView?.visibility = View.VISIBLE
                    dimView.setOnClickListener {
                        val intent = Intent(context, MoreActivity::class.java).apply {
                            putExtra("sid", item.sid)
                            putExtra("title", item.title)
                        }
                        context.startActivity(intent)
                    }
                } else {
                    dimView?.visibility = View.GONE
                }
            }
        }
    }
}