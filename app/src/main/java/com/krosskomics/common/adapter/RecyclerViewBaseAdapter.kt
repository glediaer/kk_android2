package com.krosskomics.common.adapter

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krosskomics.R
import com.krosskomics.common.data.*
import com.krosskomics.common.holder.BaseItemViewHolder
import com.krosskomics.mainmenu.activity.RankingActivity
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.viewer.activity.ViewerActivity
import kotlinx.android.synthetic.main.item_coin.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_comment_report.view.*
import kotlinx.android.synthetic.main.item_download_ep.view.*
import kotlinx.android.synthetic.main.item_more.view.*
import kotlinx.android.synthetic.main.item_more.view.deleteView
import kotlinx.android.synthetic.main.item_gift.view.*
import kotlinx.android.synthetic.main.item_mynews.view.newsTextView
import kotlinx.android.synthetic.main.item_mynews.view.remainTimeTextView
import kotlinx.android.synthetic.main.item_notice.view.*
import kotlinx.android.synthetic.main.item_ongoing.view.genreTextView
import kotlinx.android.synthetic.main.item_ongoing.view.mainImageView
import kotlinx.android.synthetic.main.item_ongoing.view.titleTextView
import kotlinx.android.synthetic.main.item_ongoing.view.writerTextView
import kotlinx.android.synthetic.main.item_ranking.view.*
import kotlinx.android.synthetic.main.item_ranking_a.view.*
import kotlinx.android.synthetic.main.item_search_recent.view.*
import kotlinx.android.synthetic.main.item_series.view.*
import kotlinx.android.synthetic.main.item_series.view.img_ep_title
import kotlinx.android.synthetic.main.item_series.view.txt_ep_title
import kotlinx.android.synthetic.main.item_series_grid.view.*
import kotlinx.android.synthetic.main.view_content_like.view.*
import kotlinx.android.synthetic.main.view_content_tag_right.view.*
import kotlinx.android.synthetic.main.view_dim.view.*
import kotlinx.android.synthetic.main.view_ep_free_tag.view.*
import kotlinx.android.synthetic.main.view_ep_lock_tag.view.*
import kotlinx.android.synthetic.main.view_ep_purchase_select.view.*
import kotlinx.android.synthetic.main.view_new_up_tag.view.*
import kotlinx.android.synthetic.main.view_remain_tag.view.*
import kotlinx.android.synthetic.main.view_series_wop_tag.view.*
import kotlinx.android.synthetic.main.view_ticket.view.*

open class RecyclerViewBaseAdapter(private val items: ArrayList<*>, private val layoutRes: Int) :
    RecyclerView.Adapter<RecyclerViewBaseAdapter.BaseItemHolder>() {

    private var onClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnDeleteItemClickListener? = null
    private var onDownloadClickListener: OnDownloadClickListener? = null
    private var onDownloadCancelClickListener: OnDownloadCancelClickListener? = null
    private var onSubscribeClickListener: OnSubscribeClickListener? = null
    private var onCommentReportClickListener: OnCommentReportClickListener? = null

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

    fun setOnCommentReportClickListener(onItemClickListener: OnCommentReportClickListener) {
        this.onCommentReportClickListener = onItemClickListener
    }

    inner class BaseItemHolder(itemView: View) : BaseItemViewHolder(itemView) {
        override fun setData(item: Any?, position: Int) {
            itemView.apply {
                animation = AnimationUtils.loadAnimation(context, R.anim.list_item_slide_down)
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
                        tv_like_count?.text = item.like_cnt
                    }
                    if (item.isnew == "1") {
                        newTagImageView?.visibility = View.VISIBLE
                        upTagImageView?.visibility = View.GONE
                    } else {
                        newTagImageView?.visibility = View.GONE
                        if (item.isupdate == "1") {
                            upTagImageView?.visibility = View.VISIBLE
                        } else {
                            upTagImageView?.visibility = View.GONE
                        }
                    }
                    // 기다무
                    if (item.iswop == "1") {
                        remainTagView?.visibility = View.VISIBLE
                        remainTagView?.isSelected = false
                        remainTagTextView?.text = item.dp_wop_term
                    } else {
                        if (item.dp_pub_day.isNullOrEmpty()) {
                            remainTagView.visibility = View.GONE
                        } else {
                            remainTagView?.isSelected = true
                            remainTagTextView?.text = item.dp_pub_day
                            remainTagView.visibility = View.VISIBLE
                        }
                    }
                    // ranking
                    if (context is RankingActivity) {
                        when (position) {
                            0 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_1)
                            1 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_2)
                            2 -> rankingImageView?.setImageResource(R.drawable.kk_ranking_3)
                        }
                        rankingTextView?.text = (position + 1).toString()
                    }
                    // genre
                    if (item.issub.isNullOrEmpty()) {
                        subscribeImageView?.visibility = View.GONE
                    } else {
                        subscribeImageView?.visibility = View.VISIBLE
                        subscribeImageView.isSelected = item.issub != "0"
                        subscribeImageView?.setOnClickListener {
                            subscribeImageView.isSelected = !it.isSelected
                            onSubscribeClickListener?.onItemClick(
                                item,
                                position,
                                subscribeImageView.isSelected
                            )
                        }
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
//                    txt_update?.text = item.ep_show_date
//                    txt_showDate?.text = item.show_str
                    if (itemView.context is SeriesActivity) {
                        val viewModel = (itemView.context as SeriesActivity).viewModel

                        if ("1" == item.isunlocked) {
//                            dimView?.visibility = View.GONE
                            lockImageView?.visibility = View.GONE
                            ticketImageView?.visibility = View.GONE
                            if (viewModel.listViewType == 1) {  // list type
                                if (read(context, CODE.LOCAL_loginYn, "N").equals(
                                        "Y",
                                        ignoreCase = true
                                    )
                                ) {
                                    downloadImageView?.isSelected = "0" != item.isdownload
                                    downloadImageView?.visibility = View.VISIBLE
                                    downloadImageView?.setOnClickListener(View.OnClickListener {
                                        onDownloadClickListener?.onItemClick(item, position)
                                    })
                                    if (item.download_progress > 0) {
                                        downloadImageView?.visibility = View.GONE
//                                        circleView.max = item.download_max
                                        circleView.visibility = View.VISIBLE
                                        Log.e(
                                            "Adapter",
                                            "download_progress : " + item.download_progress
                                        )
                                        circleView.progress = item.download_progress
                                        circleView.setOnClickListener(View.OnClickListener {
                                            onDownloadCancelClickListener?.onItemClick(
                                                item,
                                                position
                                            )
                                        })
                                    } else {
                                        circleView.setVisibility(View.GONE)
                                        downloadImageView?.visibility = View.VISIBLE
                                    }
                                }
                            }
                        } else {
//                            dimView?.visibility = View.VISIBLE
                            lockImageView?.visibility = View.VISIBLE
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
//                                dimView?.visibility = View.GONE
                                selectView?.visibility = View.VISIBLE
                                if (item.isChecked) {
                                    checkImageView?.visibility = View.VISIBLE
                                } else {
                                    checkImageView?.visibility = View.GONE
                                }
                            } else {
//                                dimView?.visibility = View.VISIBLE
                                selectView?.visibility = View.GONE
                            }
                        }
                        when (viewModel.listViewType) {
                            0 -> {
                                txt_update?.text = item.dp_tile_txt
                                if (item.isread == "0") {
                                    epGridContentView.setBackgroundColor(Color.TRANSPARENT)
                                } else {
                                    epGridContentView.setBackgroundColor(Color.parseColor("#0c000000"))
                                }
                            }
                            1 -> {
                                txt_showDate?.text = item.dp_list_txt
                                if (item.isread == "0") {
                                    epListContentView.setBackgroundColor(Color.TRANSPARENT)
                                } else {
                                    epListContentView.setBackgroundColor(Color.parseColor("#07000000"))
                                }
                            }
                        }

                        when (item.ep_type) {   // F: 무료, W:기다리면 무료, C:유료
                            "F" -> {
                                wopImageView.visibility = View.GONE
                                freeTagImageView.visibility = View.VISIBLE
                            }
                            "W" -> {
                                wopImageView.visibility = View.VISIBLE
                                freeTagImageView.visibility = View.GONE
                            }
                            "C" -> {
                                wopImageView.visibility = View.GONE
                                freeTagImageView.visibility = View.GONE
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
                    tv_total_coin.text =
                        totalCoin.toString() + " " + itemView.context.getString(R.string.str_coins)

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
                } else if (item is DataNotice) {
                    itemView.isSelected = position == 0
                    titleTextView.text = item.title
                    dateTextView?.text = item.writer1
                    arrowImageView?.isSelected = item.isSelect
                    if (item.isSelect) {
                        expandView?.visibility = View.VISIBLE
                    } else {
                        expandView?.visibility = View.GONE
                    }
                } else if (item is DataComment) {
                    newsTextView?.text = item.title
                    remainTimeTextView?.text = item.writer1
                    reportImageView?.setOnClickListener {
                        onCommentReportClickListener?.onItemClick(item, position)
                    }
                } else if (item is DataReport) {
                    ReportTextView?.text = item.title
                    isSelected = item.isSelect
                    if (position < itemCount - 1) {
                        underLineView.visibility = View.VISIBLE
                    } else {
                        underLineView.visibility = View.GONE
                    }
                } else if (item is String) {    // recent tag
                    titleTextView?.text = item
                    deleteImageView.setOnClickListener {
                        onDeleteClickListener?.onItemClick(item)
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

    interface OnCommentReportClickListener {
        fun onItemClick(item: Any, position: Int)
    }
}