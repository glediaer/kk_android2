package com.krosskomics.search.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.samples.zoomable.DoubleTapGestureListener
import com.facebook.samples.zoomable.ZoomableDraweeView
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBanner
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.getDeviceWidth
import com.krosskomics.util.CommonUtil.goLoginAlert
import com.krosskomics.util.CommonUtil.moveBrowserChrome
import com.krosskomics.util.CommonUtil.moveSignUp
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.webview.WebViewActivity
import kotlinx.android.synthetic.main.item_footer_viewer.view.*
import kotlinx.android.synthetic.main.item_search_tag.view.*
import kotlinx.android.synthetic.main.item_viewer.view.*
import kotlinx.android.synthetic.main.item_viewer_comic.view.*

class SearchTagAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerView.Adapter<SearchTagAdapter.ViewerViewHolder>() {

    private var onClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    inner class ViewerViewHolder(itemView: View) : BaseItemViewHolder(itemView) {

        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                setOnClickListener {
                    onClickListener?.onItemClick(item)
                }
                if (item is String) {
                    tagTextView.text = item
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}