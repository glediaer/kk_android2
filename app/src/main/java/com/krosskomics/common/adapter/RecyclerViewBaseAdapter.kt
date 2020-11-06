package com.krosskomics.common.adapter

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.*
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.ranking.activity.RankingDetailActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.viewer.activity.ViewerActivity
import kotlinx.android.synthetic.main.item_coin.view.*
import kotlinx.android.synthetic.main.item_download_ep.view.*
import kotlinx.android.synthetic.main.item_more.view.*
import kotlinx.android.synthetic.main.item_more.view.deleteView
import kotlinx.android.synthetic.main.item_gift.view.*
import kotlinx.android.synthetic.main.item_mynews.view.*
import kotlinx.android.synthetic.main.item_mynews.view.newsTextView
import kotlinx.android.synthetic.main.item_mynews.view.remainTimeTextView
import kotlinx.android.synthetic.main.item_notice.view.*
import kotlinx.android.synthetic.main.item_ongoing.view.genreTextView
import kotlinx.android.synthetic.main.item_ongoing.view.mainImageView
import kotlinx.android.synthetic.main.item_ongoing.view.titleTextView
import kotlinx.android.synthetic.main.item_ongoing.view.writerTextView
import kotlinx.android.synthetic.main.item_ranking.view.*
import kotlinx.android.synthetic.main.item_ranking_detail.view.*
import kotlinx.android.synthetic.main.item_series.view.*
import kotlinx.android.synthetic.main.item_series.view.img_ep_title
import kotlinx.android.synthetic.main.item_series.view.txt_ep_title
import kotlinx.android.synthetic.main.item_series_grid.view.*
import kotlinx.android.synthetic.main.view_content_like.view.*
import kotlinx.android.synthetic.main.view_content_tag_right.view.*
import kotlinx.android.synthetic.main.view_dim.view.*
import kotlinx.android.synthetic.main.view_ticket.view.*

open class RecyclerViewBaseAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerView.Adapter<RecyclerViewBaseAdapter.BaseItemHolder>() {

    private var onClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteItemClickListener? = null
    private var onDownloadClickListener: OnDownloadClickListener? = null
    private var onDownloadCancelClickListener: OnDownloadCancelClickListener? = null
    private var onSubscribeClickListener: OnSubscribeClickListener? = null

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

    fun setOnDownloadCancelClickListener(onItemClickListener: OnDownloadCancelClickListener) {
        this.onDownloadCancelClickListener = onItemClickListener
    }

    fun setOnSubscribeClickListener(onItemClickListener: OnSubscribeClickListener) {
        this.onSubscribeClickListener = onItemClickListener
    }

    inner class BaseItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                setOnClickListener {
                    onClickListener?.onItemClick(item, position)
                }
                if (item is DataBook) {
                    mainImageView?.let {
                        Glide.with(itemView.context)
                            .load(item.image)
                            .into(it)
                    }

                    titleTextView?.text = item.title
                    writerTextView?.text = item.writer1
                    genreTextView?.text = item.genre1
                    if (item.like_cnt.isNullOrEmpty()) {
                        contentLikeView?.visibility = View.GONE
                    } else {
                        contentLikeView?.visibility = View.VISIBLE
                        likeCountTextView?.text = item.like_cnt
                    }
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
                        onSubscribeClickListener?.onItemClick(item, position, subscribeImageView.isSelected)
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
                    img_ep_title?.controller = CommonUtil.getDraweeController(
                        context, item.image,
                        200, 200
                    )
                    txt_ep_title?.text = item.ep_title
                    txt_update?.text = item.ep_show_date
                    txt_showDate?.text = item.show_str
                    if (itemView.context is SeriesActivity) {
                        val viewModel = (itemView.context as SeriesActivity).viewModel

                        if ("1" == item.isunlocked) {
                            dimView?.visibility = View.GONE
                            ticketImageView?.visibility = View.GONE
                            if (viewModel.listViewType == 1) {
                                if (read(context, CODE.LOCAL_loginYn, "N").equals(
                                        "Y",
                                        ignoreCase = true
                                    )) {
                                    downloadImageView?.isSelected = "0" != item.isdownload
                                    downloadImageView?.visibility = View.VISIBLE
                                    downloadImageView?.setOnClickListener(View.OnClickListener {
                                        onDownloadClickListener?.onItemClick(item, position)
                                    })
                                    if (item.download_progress > 0) {
                                        downloadImageView?.visibility = View.GONE
//                                        circleView.max = item.download_max
                                        circleView.visibility = View.VISIBLE
                                        Log.e("Adapter", "download_progress : " + item.download_progress)
                                        circleView.progress = item.download_progress
                                        circleView.setOnClickListener(View.OnClickListener {
                                            onDownloadCancelClickListener?.onItemClick(item, position)
                                        })
                                    } else {
                                        circleView.setVisibility(View.GONE)
                                        downloadImageView?.visibility = View.VISIBLE
                                    }
                                }
                            }
                        } else {
                            dimView?.visibility = View.VISIBLE
                            // 티켓
                            if (itemView.context is SeriesActivity) {
                                if (viewModel.selectEpItem.rticket > 0) {
                                    if (item.ep_seq < viewModel.selectEpItem.except_ep_seq) {
                                        ticketImageView?.visibility = View.VISIBLE
                                        ticketImageView?.isSelected = false
                                    } else {
                                        ticketImageView?.visibility = View.GONE
                                    }
                                } else {
                                    if (viewModel.selectEpItem.sticket > 0) {
                                        if (item.ep_seq < viewModel.selectEpItem.except_ep_seq) {
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
                            // 구매
                            if (item.isCheckVisible) {
                                dimView?.visibility = View.GONE
                                selectView?.visibility = View.VISIBLE
                                if (item.isChecked) {
                                    checkImageView?.visibility = View.VISIBLE
                                } else {
                                    checkImageView?.visibility = View.GONE
                                }
                            } else {
                                dimView?.visibility = View.VISIBLE
                                selectView?.visibility = View.GONE
                            }
                        }
                    } else if (itemView.context is ViewerActivity) {
                        txt_ep_title.isSelected = item.isEpSelect
                        img_ep_title.isSelected = item.isEpSelect
                    }
                } else if (item is DataCoin) {
                    tv_coin.text = item.product_name

                    val totalCoin: Int = item.coin + item.bonus_coin
                    tv_bonus_coin.text = item.coin.toString() + " + " +
                            item.bonus_coin + " " + itemView.context.getString(R.string.str_bonus) + " = "
                    tv_total_coin.text = totalCoin.toString() + " " + itemView.context.getString(R.string.str_coins)

                    tv_won.text = item.sale_price.toString() + " " + item.currency
                    if (TextUtils.isEmpty(item.product_text)) {
                        tv_product_text.visibility = View.GONE
                    } else {
                        tv_product_text.text = item.product_text
                        tv_product_text.visibility = View.VISIBLE
                    }
                } else if (item is DataBanner) {
                    img_ep_title?.controller = CommonUtil.getDraweeController(
                        context, item.image,
                        200, 200
                    )
                    txt_ep_title?.text = item.title
                } else if (item is DataFile) {
                    if (!TextUtils.isEmpty(item.image)) {
                        img_ep_title?.setImageURI("file://" + item.image)
                    }
                    if (item.isCheckVisible) {
                        deleteView?.visibility = View.VISIBLE
                        deleteView?.isEnabled = item.isChecked
                        deleteView.setOnClickListener {
                            onDeleteClickListener?.onItemClick(item)
                        }
                    } else {
                        deleteView?.visibility = View.GONE
                    }
                    txt_ep_title?.text = item.ep_title
//                    tv_show_date?.text = item.ep_show_date
                    tv_expire_date?.text = item.expireDate
//                } else if (item is DataRecentSearch) {    // search recent
//                    titleTextView.text = item.subject
//                    deleteImageView.setOnClickListener {
//                        onDeleteClickListener?.onItemClick(item)
//                    }
                } else if (item is DataGift) {
                    mainImageView?.let {
                        Glide.with(itemView.context)
                            .load(item.image)
                            .into(it)
                    }
//                    if ("1" == item.isupdate) {
//                        holder.upImaegeView.setVisibility(View.VISIBLE)
//                    } else {
//                        holder.upImaegeView.setVisibility(View.GONE)
//                    }
//                    if ("1" == item.isnew) {
//                        holder.newImaegView.setVisibility(View.VISIBLE)
//                    } else {
//                        holder.newImaegView.setVisibility(View.GONE)
//                    }
                    titleTextView.text = item.title
                    dDayTextView.text = item.show_str
                } else if (item is DataNews) {
                    newsTextView.text = item.title
                    remainTimeTextView.text = item.writer1
                    if (item.isSelect) {
                        expandView?.visibility = View.VISIBLE
                    } else {
                        expandView?.visibility = View.GONE
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?, position: Int)
    }

    interface OnDeleteItemClickListener {
        fun onItemClick(item: Any)
    }

    interface OnDownloadClickListener {
        fun onItemClick(item: Any, position: Int)
    }

    interface OnDownloadCancelClickListener {
        fun onItemClick(item: Any, position: Int)
    }

    interface OnSubscribeClickListener {
        fun onItemClick(item: Any, position: Int, selected: Boolean)
    }
}