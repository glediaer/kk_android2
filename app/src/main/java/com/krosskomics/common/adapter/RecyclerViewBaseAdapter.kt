package com.krosskomics.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataCoin
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.ranking.activity.RankingDetailActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.read
import kotlinx.android.synthetic.main.item_coin.view.*
import kotlinx.android.synthetic.main.item_genre_detail.view.*
import kotlinx.android.synthetic.main.item_home_banner.view.mainImageView
import kotlinx.android.synthetic.main.item_ongoing.view.genreTextView
import kotlinx.android.synthetic.main.item_ongoing.view.titleTextView
import kotlinx.android.synthetic.main.item_ongoing.view.writerTextView
import kotlinx.android.synthetic.main.item_ranking.view.*
import kotlinx.android.synthetic.main.item_ranking_detail.view.*
import kotlinx.android.synthetic.main.item_series.view.*
import kotlinx.android.synthetic.main.item_series.view.img_ep_title
import kotlinx.android.synthetic.main.item_series.view.txt_ep_title
import kotlinx.android.synthetic.main.item_series_grid.view.*
import kotlinx.android.synthetic.main.view_content_tag_right.view.*
import kotlinx.android.synthetic.main.view_dim.view.*

open class RecyclerViewBaseAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerView.Adapter<RecyclerViewBaseAdapter.BaseItemHolder>() {

    private var onClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteItemClickListener? = null
    private var onDownloadClickListener: OnDownloadClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return BaseItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseItemHolder, position: Int) {
        holder.setData(items[position], position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    fun setOnDelteItemClickListener(onItemClickListener: OnDeleteItemClickListener) {
        this.onDeleteClickListener = onItemClickListener
    }

    fun setOnDownloadClickListener(onItemClickListener: OnDownloadClickListener) {
        this.onDownloadClickListener = onItemClickListener
    }

    inner class BaseItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                setOnClickListener {
                    onClickListener?.onItemClick(item)
                }
                if (item is DataBook) {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .into(mainImageView)

                    titleTextView?.text = item.title
                    writerTextView?.text = item.writer1
                    genreTextView?.text = item.genre1
                    likeCountTextView?.text = item.like_cnt

                    // ranking
                    if (context is RankingDetailActivity) {
                        when(position) {
                            0 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_1)
                            1 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_2)
                            2 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_3)
                        }
                    } else {
                        rankingTextView?.text = (position + 1).toString()
                    }

                    // genre
                    subscribeImageView?.setOnClickListener {
                        subscribeImageView.isSelected = !it.isSelected
                    }

                    // library
                    if (item.isCheckVisible) {
                        deleteView?.visibility = View.VISIBLE
                        deleteView?.isEnabled = item.isChecked
                        deleteView.setOnClickListener {
                            onDeleteClickListener?.onItemClick(item)
                        }
                    } else {
                        deleteView?.visibility = View.GONE
                    }
                } else if (item is DataEpisode) {
                    img_ep_title.controller = CommonUtil.getDraweeController(context, item.image,
                        200, 200)
                    txt_ep_title.text = item.ep_title
                    txt_update?.text = item.ep_show_date
                    txt_showDate?.text = item.show_str
                    val viewModel = (itemView.context as SeriesActivity).viewModel
                    if ("1" == item.isunlocked) {
                        dimView?.visibility = View.GONE
                        ticketImageView?.visibility = View.GONE
                        if (viewModel.listViewType == 1) {
                            if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
                                downloadImageView?.isSelected = "0" != item.isdownload
                                downloadImageView?.visibility = View.VISIBLE
                                downloadImageView?.setOnClickListener(View.OnClickListener {
                                    onDownloadClickListener?.onItemClick(item, position)
//                                if (isSelectDownload || "1" == itemData.isdownload) {
//                                    return@OnClickListener
//                                }
//                                mDownloadEpEid = itemData.eid
//                                mDownloadEpShowdate = itemData.ep_show_date
//                                mDownloadEpTitle = itemData.ep_title
//                                mSelectedDownloadIndex = position
//                                isSelectDownload = true
//                                requestImageUrl(itemData.eid)
                                })
                                if (item.download_progress > 0) {
                                    downloadImageView?.visibility = View.GONE
//                                holder.circleView.setMax(itemData.download_max)
//                                holder.circleView.setVisibility(View.VISIBLE)
//                                holder.circleView.setProgress(itemData.download_progress)
//                                holder.circleView.setOnClickListener(View.OnClickListener {
//                                    if (downLoadAsyncTask != null && !downLoadAsyncTask.isCancelled()) {
//                                        mIsDownloadException = true
//                                        downLoadAsyncTask.cancel(true)
//                                    }
//                                })
                                } else {
//                                holder.circleView.setVisibility(View.GONE)
                                    downloadImageView?.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        dimView?.visibility = View.VISIBLE
                        // 티켓
                        if (itemView.context is SeriesActivity) {
                            if (viewModel.item.rticket > 0) {
                                if (item.ep_seq < viewModel.item.except_ep_seq) {
                                    ticketImageView?.visibility = View.VISIBLE
                                    ticketImageView?.isSelected = false
                                } else {
                                    ticketImageView?.visibility = View.GONE
                                }
                            } else {
                                if (viewModel.item.sticket > 0) {
                                    if (item.ep_seq < viewModel.item.except_ep_seq) {
                                        ticketImageView?.visibility = View.VISIBLE
                                        ticketImageView?.isSelected = true
                                    } else {
                                        ticketImageView?.visibility = View.GONE
                                    }
                                } else {
                                    ticketImageView?.visibility = View.GONE
                                }
                            }
                        }
                    }
                } else if (item is String) { // viewer
                    img_ep_title.controller = CommonUtil.getDraweeController(context, item,
                        200, 200)
                } else if (item is DataCoin) {
                    tv_coin.text = item.product_name
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }

    interface OnDeleteItemClickListener {
        fun onItemClick(item: Any)
    }

    interface OnDownloadClickListener {
        fun onItemClick(item: Any, position: Int)
    }
}