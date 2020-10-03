package com.krosskomics.viewer.adapter

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
import kotlinx.android.synthetic.main.item_viewer.view.*
import kotlinx.android.synthetic.main.item_viewer_comic.view.*

class ViewerAdapter(private val items: ArrayList<*>, private val layoutRes: Int, private val context: Context) :
    RecyclerView.Adapter<ViewerAdapter.ViewerViewHolder>() {

    private var onClickListener: OnItemClickListener? = null
    private var bannerItems = ArrayList<DataBanner>()
    var widthMap: HashMap<String, Float> =
        HashMap()
    var heightMap: HashMap<String, Float> =
        HashMap()

    enum class VIEW_TYPE {
        TYPE_ITEM, TYPE_FOOTER
    }

//    var viewerOrientation = 0  // 0: v, 1: h

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        when (viewType) {
            VIEW_TYPE.TYPE_ITEM.ordinal -> {
//                view = if (viewerOrientation == 0) {
                    LayoutInflater.from(parent.context)
                        .inflate(layoutRes, parent, false)
//                } else {
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.item_viewer_comic, parent, false)
//                }
            }
            VIEW_TYPE.TYPE_FOOTER.ordinal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_footer_viewer, parent, false)
            }
        }
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (this.bannerItems.isNullOrEmpty()) {
            items.size
        } else {
            items.size + 1
        }
    }

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE.TYPE_ITEM.ordinal ->
                holder.itemView.apply {
                    items[position].let { url ->
                        if (url is String) {
                            val controllerListener: ControllerListener<ImageInfo> = object :
                                BaseControllerListener<ImageInfo>() {
                                override fun onFinalImageSet(
                                    id: String,
                                    @Nullable imageInfo: ImageInfo?,
                                    @Nullable anim: Animatable?
                                ) {
                                    if (imageInfo == null) {
                                        return
                                    }
                                    if (layoutRes == R.layout.item_viewer_comic) return
                                    val qualityInfo = imageInfo.qualityInfo
                                    if (qualityInfo.isOfGoodEnoughQuality) {
                                        val heightTarget = getTargetHeight(
                                            imageInfo.width.toFloat(),
                                            imageInfo.height.toFloat(),
                                            holder.itemView,
                                            url
                                        ) as Float
                                        if (heightTarget <= 0) return
                                        heightMap[url] = heightTarget
                                        updateItemtHeight(heightTarget, holder.itemView)
                                    }
                                }

                                override fun onIntermediateImageSet(
                                    id: String,
                                    @Nullable imageInfo: ImageInfo?
                                ) {
                                }

                                override fun onFailure(
                                    id: String,
                                    throwable: Throwable
                                ) {
                                    throwable.printStackTrace()
//                                    try {
//                                        if (throwable.message!!.contains("404")) {        // 이미지가 존재하지 않음
//                                            if (url == null) {
//                                                Crashlytics.logException(
//                                                    Exception(
//                                                        "404 " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl imageUrls : " + Gson().toJson(
//                                                            imageUrls
//                                                        ) + " onFailure , message : " + throwable.message
//                                                    )
//                                                )
//                                            } else {
//                                                Crashlytics.logException(Exception("404 " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                            }
//                                        } else if (throwable.message!!.contains("unknown image format")) {    // 이미지 업로드가 잘못된것
//                                            Crashlytics.logException(Exception("unknown image format " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                        } else if (throwable.message!!.contains("403")) {    // 이미지 인증키 오류
//                                            Crashlytics.logException(Exception("403 " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                            reloadInterface.reload()
//                                        } else if (throwable.message!!.contains("503")) {    // 서버오류 또는 CDN오류
//                                            Crashlytics.logException(Exception("503 " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                        } else if (throwable.message!!.contains("img.kj.me")) {        // 사용자가 호스트를 못찾음(통신문제 가능)
//                                            Crashlytics.logException(Exception("img.kj.me " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                        } else if (throwable.message!!.contains("image.kj.com")) {    // 사용자가 호스트를 못찾음
//                                            Crashlytics.logException(Exception("image.kj.com " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                        } else if (throwable.message!!.contains("timed out")) {            // 통신 문제
//                                            Crashlytics.logException(Exception("timed out " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                        } else {
//                                            Crashlytics.logException(Exception(com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure , message : " + throwable.message))
//                                        }
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                        Crashlytics.logException(Exception("404 " + com.krosskomics.adapter.ShowListImageRecyclerAdapter.TAG + " imageurl : " + url + " onFailure Exception , message : " + throwable.message))
//                                    }
                                }
                            }
                            if (layoutRes == R.layout.item_viewer) {
                                if (heightMap.containsKey(url)) {
                                    val height: Float = heightMap[url]!!
                                    if (height > 0) {
                                        updateItemtHeight(height, holder.itemView)
                                        draweeview.setImageURI(Uri.parse(url))
                                        return
                                    }
                                }

                                val requestBuilder =
                                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                                if (getDeviceWidth(context) <= 720) {
                                    requestBuilder.resizeOptions = ResizeOptions(
                                        getDeviceWidth(
                                            context
                                        ), getDeviceWidth(context) * 3
                                    )
                                }
                                requestBuilder
                                    .setRequestPriority(Priority.HIGH).isProgressiveRenderingEnabled =
                                    true

                                val request =
                                    requestBuilder.build()

                                val controller: DraweeController =
                                    Fresco.newDraweeControllerBuilder()
                                        .setUri(Uri.parse(url))
                                        .setOldController(draweeview.controller)
                                        .setImageRequest(request)
                                        .setControllerListener(controllerListener)
                                        .setTapToRetryEnabled(true)
                                        .build()

                                draweeview.controller = controller
//                            holder.itemView.setOnClickListener { (context as ShowActivity).toggleToolBar() }
//                            (context as ShowActivity).hideProgress()
                            } else {
                                if (zoomDraweeView is ZoomableDraweeView) {
                                    (zoomDraweeView as ZoomableDraweeView).setAllowTouchInterceptionWhileZoomed(true)
                                    (zoomDraweeView as ZoomableDraweeView).setIsLongpressEnabled(false)
                                    (zoomDraweeView as ZoomableDraweeView).setTapListener(DoubleTapGestureListener(
                                        zoomDraweeView as ZoomableDraweeView
                                    ))
                                }
                                val controller: DraweeController =
                                    Fresco.newDraweeControllerBuilder()
                                        .setUri(url)
                                        .setControllerListener(controllerListener)
                                        .setCallerContext(context)
                                        .build()
                                zoomDraweeView.controller = controller
                            }
                        }
                    }
                }

            VIEW_TYPE.TYPE_FOOTER.ordinal -> {
                holder.itemView.apply {
                    bannerRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    bannerRecyclerView.adapter = ViewerBannerAdapter(bannerItems, R.layout.item_viewer_banner, context)
                    (bannerRecyclerView.adapter as ViewerBannerAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                        override fun onItemClick(item: Any?) {
                            if (item is DataBanner) {
                                setBannerAction(item)
                            }
                        }
                    })
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onClickListener = onItemClickListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionFooter(position)) {
            VIEW_TYPE.TYPE_FOOTER.ordinal
        } else VIEW_TYPE.TYPE_ITEM.ordinal
    }

    private fun isPositionFooter(position: Int): Boolean {
        return position == items.size
    }

    fun setFooterBannerData(bannerItems: ArrayList<DataBanner>) {
        this.bannerItems = bannerItems
    }


    inner class ViewerViewHolder(itemView: View) : BaseItemViewHolder(itemView) {

        override fun setData(item: Any?, position: Int) {
        }
    }

    private fun updateItemtHeight(height: Float, view: View) {
        val frameLayout = view.findViewById<FrameLayout>(R.id.fl_webtoon)
        val child = view.findViewById<View>(R.id.draweeview)
        val layoutParams =
            child.layoutParams as FrameLayout.LayoutParams
        layoutParams.height = height.toInt()
        frameLayout.updateViewLayout(child, layoutParams)
    }

    private fun getTargetHeight(
        width: Float,
        height: Float,
        view: View,
        url: String
    ): Float {
        val child = view.findViewById<View>(R.id.draweeview)
        val widthTarget: Float

        if (widthMap.containsKey(url)) {
            widthTarget = widthMap[url]?.toFloat()!!
        } else {
            widthTarget = child.measuredWidth.toFloat()
            if (widthTarget > 0) {
                widthMap[url] = widthTarget
            }
        }
        return height * (widthTarget / width)
    }

    private fun setBannerAction(item: DataBanner) {
        // M:메인, H:작품홈, C:충전(인앱), W:웹뷰, B:브라우저, N:없슴
        val intent: Intent
        when (item.atype) {
            "M" -> {
            }
            "H" -> {
                intent = Intent(context, SeriesActivity::class.java)
                val b = Bundle()
                b.putString("sid", item.sid)
                b.putString("title", item.title)
                intent.putExtras(b)
                context.startActivity(intent)
            }
            "C" -> if (read(context, CODE.LOCAL_loginYn, "N")
                    .equals("Y", ignoreCase = true)
            ) {
                intent = Intent(context, CoinActivity::class.java)
                context.startActivity(intent)
            } else {
                goLoginAlert(context)
            }
            "W" -> if ("" != item.link) {
                intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("title", item.subject)
                intent.putExtra("url", item.link)
                context.startActivity(intent)
            }
            "B" -> if ("" != item.link) {
                moveBrowserChrome(context, item.link)
            }
            "S" -> if (!read(context, CODE.LOCAL_loginYn, "N")
                    .equals("Y", ignoreCase = true)
            ) {
                moveSignUp(context)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any?)
    }
}