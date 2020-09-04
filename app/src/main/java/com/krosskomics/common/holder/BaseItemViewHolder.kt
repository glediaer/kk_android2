package com.krosskomics.common.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class BaseItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * ItemViewHolder implementation
     */
//    abstract fun ItemViewHolder(itemView: View?)

    /**
     * Binds the data to the item. <br></br>
     * Called within [RecyclerView.Adapter.onBindViewHolder].
     *
     * @param item The data set to fill the view items.
     */
    abstract fun setData(item: Any?, position: Int)
}