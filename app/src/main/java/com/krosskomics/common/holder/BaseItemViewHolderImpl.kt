package com.krosskomics.common.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.common.data.BaseItem

open class BaseItemViewHolderImpl(itemView: View) : BaseItemViewHolder(itemView) {
    override fun setData(item: Any?) {

    }
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
}