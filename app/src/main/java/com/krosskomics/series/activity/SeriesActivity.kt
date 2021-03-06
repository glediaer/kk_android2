package com.krosskomics.series.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.coin.activity.CoinActivity
import com.krosskomics.comment.activity.CommentActivity
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.Episode
import com.krosskomics.common.model.EpisodeMore
import com.krosskomics.common.model.PurchaseEpisode
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.adapter.SeriesAdapter
import com.krosskomics.series.viewmodel.SeriesViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.convertUno
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.setAppsFlyerEvent
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.FileUtils
import com.krosskomics.util.FileUtils.deleteDir
import com.krosskomics.util.FileUtils.fileToByte
import com.krosskomics.util.FileUtils.generateKey
import com.krosskomics.util.FileUtils.getBitmapFromURL
import com.krosskomics.util.FileUtils.getStream
import com.krosskomics.util.FileUtils.saveBitmapToFileCache
import com.krosskomics.util.FileUtils.writeFile
import com.krosskomics.util.FileUtils.writeFile2
import com.krosskomics.util.ServerUtil.service
import com.krosskomics.util.UtilBitmap
import com.krosskomics.viewer.activity.ViewerActivity
import com.scottyab.aescrypt.AESCrypt
import kotlinx.android.synthetic.main.activity_main_content.recyclerView
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.activity_series.mainImageView
import kotlinx.android.synthetic.main.activity_series.nestedScrollView
import kotlinx.android.synthetic.main.view_action_item.*
import kotlinx.android.synthetic.main.view_action_item.view.*
import kotlinx.android.synthetic.main.view_content_like_white.*
import kotlinx.android.synthetic.main.view_ep_purchase.*
import kotlinx.android.synthetic.main.view_ep_purchase_success.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.android.synthetic.main.view_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


class SeriesActivity : ToolbarTitleActivity() {
    private val TAG = "SeriesActivity"

    var player: SimpleExoPlayer? = null
    private var playWhenReady = false

    lateinit var downLoadAsyncTask: DownloadFileFromURL

    public override val viewModel: SeriesViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SeriesViewModel(application) as T
            }
        }).get(SeriesViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_series_grid
        return R.layout.activity_series
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_series))
    }

    override fun initToolbar() {
        super.initToolbar()
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbar.apply {
            setBackgroundColor(Color.parseColor("#33000000"))
            actionItem.visibility = View.VISIBLE
            actionItem.giftboxImageView.visibility = View.GONE
            actionItem.searchImageView.visibility = View.GONE
            actionItem.scribeImageView.visibility = View.VISIBLE
            actionItem.scribeImageView.setOnClickListener {
                if (it.isSelected) {
                    viewModel.subscribeAction = "C"
                } else {
                    viewModel.subscribeAction = "S"
                }
                it.isSelected = !it.isSelected
                // 구독 요청/해지 api request
                requestSubscribe()
            }
            actionItem.pushImageView.setOnClickListener {
                if (it.isSelected) {
                    viewModel.pushAction = "C"
                } else {
                    viewModel.pushAction = "S"
                }
                it.isSelected = !it.isSelected
                // 구독 요청/해지 api request
                requestPushNoti()
            }

            toolbarTitle.visibility = View.VISIBLE
            toolbarTitle.setTextColor(Color.WHITE)
            toolbarTitle.text = ""
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun requestSeriesEp() {
        viewModel.requestEpList()
    }

    private fun requestSubscribe() {
        val api = service.setNotiSelector(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "subscribe", viewModel.sid, viewModel.subscribeAction, "",
            read(context, CODE.LOCAL_Android_Id, "")
        )
        api.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            toolbar.actionItem.scribeImageView.isSelected =
                                "C" != viewModel.subscribeAction
                            if ("S" == viewModel.subscribeAction) {
                                pushImageView.visibility = View.VISIBLE
                                pushImageView.isSelected = viewModel.seriesItem.ispush != "0"
                                val eventValue: MutableMap<String, Any?> =
                                    HashMap()
                                eventValue["af_content"] = viewModel.seriesItem.title + " (" + read(
                                    context,
                                    CODE.CURRENT_LANGUAGE,
                                    "en"
                                ) + ")"
                                eventValue["af_content_id"] = viewModel.seriesItem.sid
                                setAppsFlyerEvent(context, "af_subscribe", eventValue)
                            } else {
                                pushImageView.visibility = View.GONE
                                pushImageView.isSelected = viewModel.seriesItem.ispush != "0"
                            }
                        } else if ("202" == item.retcode) {
                            goCoinAlert(context)
                        } else {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
//                    hideProgress()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
//                hideProgress()
                try {
//                    checkNetworkConnection(context, t, actBinding.viewError)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun requestPushNoti() {
        val api = service.setNotiSelector(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "push_subscribe", viewModel.sid, viewModel.pushAction, "",
            read(context, CODE.LOCAL_Android_Id, "")
        )
        api.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                        } else {
                            if ("" != item.msg) {
                                showToast(item.msg, context)
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun initModel() {
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        intent?.apply {
            toolbarTitleString = extras?.getString("title").toString()
            viewModel.sid = extras?.getString("sid").toString()
        }
        super.initModel()
        viewModel.getEpListResponseLiveData().observe(this, this)
        viewModel.getCheckEpResponseLiveData().observe(this, this)
        viewModel.getImageUrlResponseLiveData().observe(this, this)
    }

    private fun requestImageUrl() {
        viewModel.requestImageUrl()
    }

    private fun sendDownloadComplete() {
        val api: Call<Default> = service.sendDownloadComplete(
            read(context, CODE.CURRENT_LANGUAGE, "en"),
            "download_episode", viewModel.downloadEpEid
        )
        api.enqueue(object : Callback<Default?> {
            override fun onResponse(
                call: Call<Default?>,
                response: Response<Default?>
            ) {
                try {
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default?>, t: Throwable) {
                try {
                    t.printStackTrace()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onChanged(t: Any?) {
        if (t is Episode) {
            when (viewModel.requestType) {
                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_A -> {
                    if ("00" == t.retcode) {
                        t.list?.let {
                            viewModel.seriesItem = t.series!!
                            viewModel.arr_episode.addAll(it.list)
                            viewModel.items.addAll(it.list)
                            viewModel.page = it.page
                            viewModel.totalPage = it.tot_pages
                            setMainContentView(t)
                            setHeaderContentView(t)

                            checkViewerType()
                            getSeriesDownloadedFile()
                            // 전체구매
                            viewModel.arr_episode.forEach { item ->
                                if ("0" == item.isunlocked) {
                                    item.isChecked = true
                                    item.isCheckVisible = false
                                    item.possibility_allbuy = true
                                    viewModel.allbuy_possibility_count++
//                                mLockedEpisodeCount++
                                } else {
                                    item.isdownload = "0"
                                    for (epDownloadedEid in viewModel.seriesDownloadEpList) {
                                        if (epDownloadedEid == item.eid) {
                                            item.isdownload = "1"
                                        }
                                    }
                                }
                            }

                            val eventValue: MutableMap<String, Any?> =
                                HashMap()
                            eventValue["af_content"] =
                                viewModel.seriesItem.title.toString() + " (" + read(
                                    context,
                                    CODE.CURRENT_LANGUAGE,
                                    "en"
                                ) + ")"
                            eventValue["af_content_id"] = viewModel.seriesItem.sid
                            setAppsFlyerEvent(context, "af_content_view", eventValue)
                        }

                    }
                }

                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_C -> {
                    when (t.retcode) {
                        "00" -> showEp()
                        "201" -> goLoginAlert(context)
                        "202" -> goCoinAlert(context)
                        "205" -> {
                            // 구매팝업
                            // ablestore == 1 소장구매 가능
                            // ablerent == 1 렌트 가능
                            viewModel.selectEpItem = t.episode!!
                            showPurchaseRentDialog(t.episode)
                        }
                        else -> {
                            t.msg?.let {
                                CommonUtil.showToast(it, context)
                            }
                        }
                    }
                }

                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_D -> {
                    when (t.retcode) {
                        "00" -> goDownload(t)
                        "201" -> goLoginAlert(context)
                        "202" -> goCoinAlert(context)
                        "205" -> {
                            // 구매팝업
                            // ablestore == 1 소장구매 가능
                            // ablerent == 1 렌트 가능
                            viewModel.selectEpItem = t.episode!!
                            showPurchaseRentDialog(t.episode)
                        }
                        else -> {
                            t.msg?.let {
                                showToast(it, context)
                            }
                        }
                    }
                }
            }
        } else if (t is EpisodeMore) {
            if ("00" == t.retcode) {
                viewModel.arr_episode.addAll(t.list)
                viewModel.items.addAll(t.list)
                viewModel.page = t.page
                viewModel.totalPage = t.tot_pages
                setMainContentView(t)
                getSeriesDownloadedFile()
                // 전체구매
                viewModel.arr_episode.forEach { item ->
                    if ("0" == item.isunlocked) {
                        item.isChecked = true
                        item.isCheckVisible = false
                        item.possibility_allbuy = true
                        viewModel.allbuy_possibility_count++
//                                mLockedEpisodeCount++
                    } else {
                        item.isdownload = "0"
                        for (epDownloadedEid in viewModel.seriesDownloadEpList) {
                            if (epDownloadedEid == item.eid) {
                                item.isdownload = "1"
                            }
                        }
                    }
                }
            }
        }
        hideProgress()
    }

    private fun goDownload(t: Episode) {
        t.episode?.apply {
            if (ep_contents.isNotEmpty()) {
                viewModel.let {
                    it.arr_url = ep_contents.split(",").toTypedArray()
                    it.downloadExpire = download_expire
                    saveThumbnailFile(it.downloadEpEid)
                    KJKomicsApp.DOWNLOAD_COUNT = 0
                    it.isCompleteDownload = false

                    downLoadAsyncTask = DownloadFileFromURL()
                    downLoadAsyncTask.execute()
                }
            }
        }
    }

    private fun checkViewerType() {
        if ("1" == viewModel.seriesItem.vviewer) {  //세로스크롤만 가능
            viewModel.isVerticalView = true
        } else {
            viewModel.isVerticalView = false
            viewModel.revPager = "R" == viewModel.seriesItem.hviewer
        }
    }

    private fun getSeriesDownloadedFile() {
        // download
        viewModel.let {
            val downloadPath = (KJKomicsApp.DOWNLOAD_ROOT_PATH
                    + convertUno(read(context, CODE.LOCAL_RID, "")!!) + "/"
                    + read(context, CODE.CURRENT_LANGUAGE, "en") + "/"
                    + it.sid + "_"
                    + it.seriesItem.title + "_"
                    + java.lang.String.format(
                getString(R.string.str_writer_format),
                it.seriesItem.genre1,
                it.seriesItem.genre2,
                it.seriesItem.genre3
            ) + "_"
                    + java.lang.String.format(
                getString(R.string.str_writer_format),
                it.seriesItem.writer1,
                it.seriesItem.writer2,
                it.seriesItem.writer3
            ))
            it.seriesDonwnloadedFile = File(downloadPath)
            if (it.seriesDonwnloadedFile?.absolutePath!!.contains(it.sid)
            ) {
                if (it.seriesDonwnloadedFile?.listFiles() == null) return
                for (file in it.seriesDonwnloadedFile?.listFiles()!!) {
                    val epFileName = file.name
                    val eid = epFileName.split("_".toRegex()).toTypedArray()[0]
                    it.seriesDownloadEpList.add(eid)
                }
            }
        }
    }

    /**
     * Background Async Task to download file
     */
    inner class DownloadFileFromURL :
        AsyncTask<String?, String?, String?>() {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        override fun onPreExecute() {
            super.onPreExecute()
            viewModel.arr_pics.clear()
            viewModel.arr_episode[viewModel.selectedDownloadIndex].download_max =
                viewModel.arr_url.size
        }

        override fun onCancelled() {
            super.onCancelled()
            try {
                if (viewModel.isSelectDownload) {
                    viewModel.isSelectDownload = false
                    viewModel.arr_episode[viewModel.selectedDownloadIndex].download_progress = 0
                    recyclerView.adapter?.notifyDataSetChanged()
                    // 다운받던 에피소드 파일 삭제
                    deleteDir(viewModel.downloadPath)
                    if (viewModel.isDownloadException) {
                        viewModel.isDownloadException = false
                        if (context != null) {
                            showToast(
                                getString(R.string.msg_fail_file_download),
                                context
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Downloading file in background thread
         */
        override fun doInBackground(vararg params: String?): String? {
            try {
                viewModel.secretKeySpec =
                    generateKey(CODE.ENC_PASSWORD)
                for (i in viewModel.arr_url.indices) {
                    if (isCancelled) {
                        break
                    }
                    val imageUrl: String =
                        viewModel.arr_url[i].split("\\?__token".toRegex()).toTypedArray()[0]
                    val subString =
                        imageUrl.split("_".toRegex()).toTypedArray()[1]
                    val formatIndex =
                        subString.substring(subString.length - 7, subString.length - 4)
                    var exist = subString.substring(subString.length - 3)
                    exist = ".$exist"
                    viewModel.arr_pics.add(viewModel.arr_url[i])

                    // create file
                    viewModel.downloadPath = (KJKomicsApp.DOWNLOAD_ROOT_PATH
                            + convertUno(
                        read(
                            context,
                            CODE.LOCAL_RID,
                            ""
                        )!!
                    ) + "/"
                            + read(context, CODE.CURRENT_LANGUAGE, "en") + "/"
                            + viewModel.sid + "_"
                            + viewModel.seriesItem.title + "_"
                            + java.lang.String.format(
                        getString(R.string.str_writer_format),
                        viewModel.seriesItem.genre1,
                        viewModel.seriesItem.genre2,
                        viewModel.seriesItem.genre3
                    ) + "_"
                            + java.lang.String.format(
                        getString(R.string.str_writer_format),
                        viewModel.seriesItem.writer1,
                        viewModel.seriesItem.writer2,
                        viewModel.seriesItem.writer3
                    )
                            + "/" + viewModel.downloadEpEid + "_" + viewModel.downloadEpTitle + "_" + viewModel.downloadEpShowdate
                            + "_" + viewModel.downloadExpire + "_" + viewModel.isVerticalView + "_" + viewModel.revPager)
                    val file = File(viewModel.downloadPath)
                    // url to bitmap
                    val bitmap = getBitmapFromURL(viewModel.arr_pics[i])
                    // set ratio
                    val ratio: Float = bitmap?.height!! / (bitmap.width * 1.0f)
                    var fixRatio = String.format("%.2f", ratio)
                    fixRatio = fixRatio.replace(".", "p")
                    val fileName = "imagefile" + formatIndex + "_" + fixRatio + exist
                    val fullPath = viewModel.downloadPath + "/" + fileName
                    writeFile(
                        file.absolutePath,
                        fileName,
                        UtilBitmap.bitmapToByteArray(bitmap)
                    )
                    var imgIncode = fileToByte(
                        getStream(fullPath)!!
                    )
                    // encrypt
                    imgIncode = AESCrypt.encrypt(
                        viewModel.secretKeySpec,
                        CODE.ivBytes,
                        imgIncode
                    )
                    // bitmap to save file
                    writeFile2(
                        file.absolutePath,
                        fileName,
                        imgIncode
                    )
                    val progress = (KJKomicsApp.DOWNLOAD_COUNT.toDouble()
                        .div(viewModel.arr_url.size) * 100).toInt()
                    publishProgress("" + progress)
                }
            } catch (e: Exception) {
                e.stackTrace
                viewModel.isDownloadException = true
                cancel(true)
//                Crashlytics.logException(Exception(TAG + " " + "DownloadFileFromURL : " + "fullPath : " + fullPath + ", message : " + e.message))
            }
            return null
        }

        /**
         * Updating progress bar
         */
        override fun onProgressUpdate(vararg progress: String?) {
            // setting progress percentage
            viewModel.arr_episode[viewModel.selectedDownloadIndex].download_progress =
                progress[0]!!.toInt()
            recyclerView.adapter?.notifyDataSetChanged()
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String?) {
            // dismiss the dialog after the file was downloaded
            viewModel.let {
                it.arr_pics.clear()
                KJKomicsApp.DOWNLOAD_COUNT = 0
                showToast(getString(R.string.msg_success_file_download), context)
                sendDownloadComplete()
                it.isSelectDownload = false
                it.isCompleteDownload = true
                it.arr_episode[it.selectedDownloadIndex].isdownload = "1"
                it.arr_episode[it.selectedDownloadIndex].download_progress = 0
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun saveThumbnailFile(eid: String) {
        // save series thumbnail
        val bitmap: Bitmap = getBitmapFromURL(viewModel.seriesItem.image1)!!
        FileUtils.saveBitmapToFileCache(
            bitmap,
            KJKomicsApp.DOWNLOAD_ROOT_PATH + convertUno(
                read(
                    context,
                    CODE.LOCAL_RID,
                    ""
                )!!
            )
                    + "/thumbnail/" + viewModel.sid + "/",
            viewModel.sid + ".png"
        )

        // save ep thumbnail
        for (item in viewModel.arr_episode) {
            if (eid == item.eid) {
                val epBitmap = getBitmapFromURL(item.image)
                if (epBitmap != null) {
                    saveBitmapToFileCache(
                        epBitmap,
                        KJKomicsApp.DOWNLOAD_ROOT_PATH + convertUno(
                            read(
                                context,
                                CODE.LOCAL_RID,
                                ""
                            )!!
                        )
                                + "/thumbnail/" + viewModel.sid + "/",
                        item.eid + ".png"
                    )
                }
            }
        }
    }

    private fun setHeaderContentView(t: Episode) {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (firstContinueEpView.isShown) {
                firstContinueEpView.visibility = View.GONE
            }
            if (scrollY >= CommonUtil.dpToPx(context, 100)) {
                toolbar.setBackgroundColor(Color.parseColor("#262626"))
                if (mainBannerVideoView.isShown) {
                    setBannerView(false)
                    releasePlayer()
                }
            } else {
                toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
            if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                Log.e(TAG, "nestedScrollView 바닥 : " + scrollY)
                if (viewModel.page < viewModel.totalPage) {
                    viewModel.page++
                    requestSeriesEp()
                }
            }
        }
        t.series?.let {
            if (it.issubscribed == "0") {
                toolbar.actionItem.scribeImageView.isSelected = false
                toolbar.actionItem.pushImageView.visibility = View.GONE
            } else {
                toolbar.actionItem.scribeImageView.isSelected = true
                toolbar.actionItem.pushImageView.visibility = View.VISIBLE
                toolbar.actionItem.pushImageView.isSelected = it.ispush != "0"
            }

            // 배너 체크
            mainImageView.setImageURI(it.image)
            setBannerView(it.isVideo)
            tv_like_count.text = it.like_cnt

            // info
            // 기다무 or 연재
            if (it.iswop == "1") {
                optionView.visibility = View.VISIBLE
                wopView.isSelected = false
                optionTextView.text = it.dp_waitorpay_txt
            } else {
                if (it.dp_pub_day?.isNotEmpty() == true) {
                    optionView.visibility = View.VISIBLE
                    wopView.isSelected = true
                    optionTextView.text = it.dp_pub_day
                } else {
                    optionView.visibility = View.GONE
                }
            }

            if (it.genre1?.isNotEmpty() == true) {
                genre1TextView.text = it.genre1
                genre1TextView.visibility = View.VISIBLE
            }
            if (it.genre2?.isNotEmpty() == true) {
                genre2TextView.text = it.genre2
                genre2TextView.visibility = View.VISIBLE
            }
            if (it.genre3?.isNotEmpty() == true) {
                genre3TextView.text = it.genre3
                genre3TextView.visibility = View.VISIBLE
            }

            contentTitleTextView.text = it.title
            writerTextView.text = getString(
                R.string.str_writer_format,
                it.writer1,
                it.writer2,
                it.writer3
            )
            if (it.allow_comment == "1") {
                commentView.visibility = View.VISIBLE
                commentTextView.text = it.comment_cnt
                commentView.setOnClickListener {
                    val intent = Intent(context, CommentActivity::class.java).apply {
                        putExtra("sid", viewModel.seriesItem.sid)
                        putExtra("eid", "")
                    }
                    startActivity(intent)
                }
            }

            if (it.dp_free_txt?.isNotEmpty() == true) {
                optionFreeEpView.visibility = View.VISIBLE
                optionFreeEpTextView.text = it.dp_free_txt
            }
            if (it.dp_waitorpay_txt?.isNotEmpty() == true) {
                optionWopView.visibility = View.VISIBLE
                optionWopTextView.text = it.dp_waitorpay_txt
            }

            descTextView.text = it.long_desc
            descButton.setOnClickListener { view ->
                view.isSelected = !view.isSelected
                if (view.isSelected) {
                    descTitleTextView.text = getString(R.string.str_close)
                    descTextView.visibility = View.VISIBLE
                } else {
                    descTitleTextView.text = getString(R.string.str_description)
                    descTextView.visibility = View.GONE
                }
            }

            if (it.dp_wop_term?.isNotEmpty() == true) {
                listHeaderWaitView.visibility = View.VISIBLE
                waitTextView.text = it.dp_wop_term
                waitGuessImageView.setOnClickListener { view ->
                    // 기다무 팝업
                    showWaitFreeInfoAlert(it.dp_wop_desc)
                }
            }

            if (it.reset_wop_ratio > 0) {
                progressView.visibility = View.VISIBLE
                progressBar.progress = it.reset_wop_ratio
                progressTextView.text = it.dp_reset_wop
                progressTextView.setOnClickListener { view ->
                    showWaitFreeInfoAlert(it.dp_wop_desc)
                }
            }
            it.series_notice?.let { notice ->
                optionNoticeView.visibility = View.GONE
                optionNoticeTextView.text = notice.firstOrNull()
            }

            // permanent
            rentalCntTextView.text = getString(R.string.str_ticket_format, it.rticket)
            rentalCntTextView.isSelected = it.rticket > 0
            // ep desc
            listTypeImageView.setOnClickListener { view ->
                view.isSelected = !view.isSelected
                // 리스트뷰타입 변경
                if (recyclerView.layoutManager is GridLayoutManager) {
                    recyclerViewItemLayoutId = R.layout.item_series
                    viewModel.listViewType = 1
                } else {
                    recyclerViewItemLayoutId = R.layout.item_series_grid
                    viewModel.listViewType = 0
                }
                initRecyclerViewAdapter()
            }
            epCountTextView.text = getString(R.string.str_episodes_seq_format1, t.list?.list?.size)
            listOrderImageView.setOnClickListener { view ->
                view.isSelected = !view.isSelected
//                viewModel.items.reverse()
//                recyclerView.adapter?.notifyDataSetChanged()
                viewModel.sort = if (view.tag == "n") "f" else "n"
                viewModel.isRefresh = true
                requestSeriesEp()
            }
            if ("0" == it.read_next_ep_seq || read(context, CODE.LOCAL_loginYn, "N")
                    .equals("N", ignoreCase = true)
            ) {
                // 첫화보기
                firstContinueEpTextView.text = getString(R.string.str_first_ep)
            } else {
                // 이어보기
                firstContinueEpTextView.text = getString(R.string.str_continue)
            }
            firstContinueEpView.setOnClickListener { view ->
                // 첫회, 이어보기
                if (it.read_ep !== 0) { // 이어보기
                    if ("0" != it.read_next_ep) {
                        // 이어보기
                        viewModel.nextEp = it.read_next_ep ?: ""
                    } else {
                        // 첫화보기
                        viewModel.nextEp = it.first_ep ?: ""
                    }
                } else {    // 첫화보기
                    viewModel.nextEp = it.first_ep ?: ""
                }
                // 이어서 보여주기, 아니면 첫화보기
                t.list?.list?.forEach { item ->
                    if (item.eid == viewModel.nextEp) {
                        // 현재 선택한 회차부터 구매가능한 회차까지 계산
                        viewModel.selectBuyPosibilityCount = 0
                        calcPurchaseCurrentToLastEp(item.ep_seq)
                        viewModel.selectEpItem = item
                        loadEpCheck()
                        return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun setBannerView(isVideo: Boolean) {
        if (isVideo) {
            initializePlayer()
            mainBannerImageView.visibility = View.GONE
            mainBannerVideoView.visibility = View.VISIBLE

            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, CommonUtil.dpToPx(context, 505), 0, 0)

            infoView.layoutParams = params
        } else {
            mainBannerImageView.visibility = View.VISIBLE
            mainBannerVideoView.visibility = View.GONE

            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, CommonUtil.dpToPx(context, 170), 0, 0)

            infoView.layoutParams = params
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(context)

            //플레이어 연결
            exoPlayerView.player = player
            exoPlayerView.player.addListener(eventListener)
        }
        val sample =
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val mediaSource: MediaSource = buildMediaSource(Uri.parse(sample))

        //prepare
        player?.prepare(mediaSource, true, false)

        if (CommonUtil.checkNetworkState(context)) {    // wifi일때만 자동재생
            //start,stop
            player?.playWhenReady = true
            exoPlayImageView.visibility = View.GONE
        } else {
            exoPlayImageView.visibility = View.VISIBLE
        }
    }

    private val eventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playBackState: Int) {
            if (playBackState == Player.STATE_ENDED) {
                setBannerView(false)
                releasePlayer()
            }
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            player?.stop()
            playWhenReady = player!!.playWhenReady
            exoPlayerView.player = null
            player?.release()
            player = null
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent: String = Util.getUserAgent(context, "blackJin")
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
            .createMediaSource(uri)
    }

    private fun showPurchaseRentDialog(episode: DataEpisode?) {
        episode?.let {
            actionItem.scribeImageView.visibility = View.GONE
            actionItem.pushImageView.visibility = View.GONE
            actionItem.allCheckView.visibility = View.VISIBLE
            actionItem.allCheckView.setOnClickListener { view ->
                if (view.isSelected) {
                    view.isSelected = false
                    // 전체 취소
                    viewModel.items.forEach { item ->
                        if (item is DataEpisode) {
                            item.isCheckVisible = false
                            item.isChecked = false
                        }
                    }
                    allBuyAll(false)
                } else {
                    view.isSelected = true
                    // 전체구매
                    viewModel.items.forEach { item ->
                        if (item is DataEpisode) {
                            item.isCheckVisible = true
                            item.isChecked = true
                        }
                    }
                    allBuyAll(true)
                }
            }

            epPurchaseDialog.visibility = View.VISIBLE
            titleTextView.text = viewModel.seriesItem.title

            if (it.iswop == "0") {
                purchaseWopView.visibility = View.GONE
            } else {
                purchaseWopView.visibility = View.VISIBLE
                purchaseWaitTextView.text = it.dp_except_ep
            }

            if (it.reset_wop_ratio > 0) {
                purcaseProgressView.visibility = View.VISIBLE
                purchaseProgressBar.progress = it.reset_wop_ratio
                purchaseProgressTextView.text = it.reset_wop
            }

            myKeyTextView.text = it.user_cash
            discountRateTextView.text = it.user_bonus_cash + " %"
            totalTextView.text = "${it.ep_rent_price}"

            viewModel.epList.add(episode.eid)
            viewModel.epTitleList.add(
                viewModel.seriesItem.title.toString() + " - " + episode.ep_title + " (" + read(
                    context,
                    CODE.CURRENT_LANGUAGE,
                    "en"
                ) + ")"
            )

            viewModel.itemViewMode = 1
            viewModel.items.forEach { item ->
                if (item is DataEpisode) {
                    item.isCheckVisible = item.ep_seq == viewModel.selectEpItem.ep_seq
                    item.isChecked = item.ep_seq == viewModel.selectEpItem.ep_seq
                    calcPurchaseCurrentToLastEp(item.ep_seq)
                }
            }
            recyclerView.adapter?.notifyDataSetChanged()

            if ("1" == it.able_rent) {
                rentalButton.isEnabled = true
                rentalButton.isSelected = true
            } else {
                rentalButton.isEnabled = false
                rentalButton.isSelected = false
                if ("1" == it.able_store) {
                    purchaseButton.isEnabled = true
                    purchaseButton.isSelected = true
                } else {
                    purchaseButton.isEnabled = false
                    purchaseButton.isSelected = false
                }
            }
            rentalButton.text = it.rent_text
            purchaseButton.text = it.store_text

            allBuyCal()

            rentalButton.setOnClickListener { view ->
                view.isSelected = true
                purchaseButton.isSelected = false
                unlockButton.isEnabled = true

                allBuyCal()
            }
            purchaseButton.setOnClickListener { view ->
                view.isSelected = true
                rentalButton.isSelected = false
                unlockButton.isEnabled = true

                allBuyCal()
            }
            unlockButton.setOnClickListener {
                if ("1" == viewModel.seriesItem.allow_rent) {
                    if (viewModel.allbuyRentCoin > read(context, CODE.LOCAL_coin, "0")!!.toInt()) {
                        val intent = Intent(context, CoinActivity::class.java) //충전 페이지
                        startActivity(intent)
                    } else {
                        // rent request
                        requestEpisodeSelectPurchase("rent")
                    }
                } else {
                    if (viewModel.allbuy_coin > read(context, CODE.LOCAL_coin, "0")!!.toInt()) {
                        val intent = Intent(context, CoinActivity::class.java) //충전 페이지
                        startActivity(intent)
                    } else {
                        // purchase request
                        requestEpisodeSelectPurchase("store")
                    }
                }
                resetDefaultView()
            }
            nestedScrollView.scrollY = nestedScrollView.scrollY + CommonUtil.dpToPx(context, 200)
//            epPurchaseDialog.setOnClickListener { epPurchaseDialog.visibility = View.GONE }
        }
    }

    private fun resetDefaultView() {
        epPurchaseDialog.visibility = View.GONE
        actionItem.scribeImageView.visibility = View.VISIBLE
        actionItem.pushImageView.visibility = View.VISIBLE
        actionItem.allCheckView.visibility = View.GONE
        viewModel.arr_episode.forEach {
            it.isCheckVisible = false
            it.isChecked = false
        }
        viewModel.itemViewMode = 0
        allCheckView.isSelected = false

        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showEp() {
        viewModel.selectEpItem.let {
            val intent = Intent(context, ViewerActivity::class.java)
            val bundle = Bundle().apply {
                putString("title", it.ep_title)
                putString("eid", it.eid)
                putBoolean("isVerticalView", viewModel.isVerticalView)
                putBoolean("revPager", viewModel.revPager)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun initRecyclerViewAdapter() {
        if (viewModel.listViewType == 0) {
            recyclerView?.layoutManager = GridLayoutManager(context, 3)
            // 양쪽 패딩 20dp 추가
            recyclerView.setPadding(
                CommonUtil.dpToPx(context, 20), 0, CommonUtil.dpToPx(
                    context,
                    20
                ), 0
            )
        } else {
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView.setPadding(
                CommonUtil.dpToPx(context, 0),
                0,
                CommonUtil.dpToPx(context, 0),
                0
            )
        }
        recyclerView.adapter = SeriesAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is DataEpisode) {
                        if (viewModel.isSelectDownload) {
                            return
                        }
                        viewModel.selectEpItem = item
                        if (viewModel.itemViewMode == 0) {  // 일반모드
                            KJKomicsApp.DATA_EPISODE = item

                            // 현재 선택한 회차부터 구매가능한 회차까지 계산
                            viewModel.selectBuyPosibilityCount = 0
                            calcPurchaseCurrentToLastEp(item.ep_seq)
                            loadEpCheck()
                        } else {    // 구매모드
                            if (item.possibility_allbuy) {
                                if (item.isChecked) {
                                    item.isChecked = false
                                    item.isCheckVisible = false
                                    viewModel.epList.remove(item.eid)
                                    viewModel.epTitleList.remove(
                                        viewModel.seriesItem.title.toString() + " - " + item.ep_title + " (" + read(
                                            context,
                                            CODE.CURRENT_LANGUAGE,
                                            "en"
                                        ) + ")"
                                    )
                                } else {
                                    item.isChecked = true
                                    item.isCheckVisible = true
                                    viewModel.epList.add(item.eid)
                                    viewModel.epTitleList.add(
                                        viewModel.seriesItem.title.toString() + " - " + item.ep_title + " (" + read(
                                            context,
                                            CODE.CURRENT_LANGUAGE,
                                            "en"
                                        ) + ")"
                                    )
                                }
                                allBuyCal()
                                recyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })
            setOnDownloadClickListener(object : RecyclerViewBaseAdapter.OnDownloadClickListener {
                override fun onItemClick(item: Any, position: Int) {
                    if (item is DataEpisode) {
                        // download
                        viewModel.let {
                            if (it.isSelectDownload || "1" == item.isdownload) {
                                return
                            }
                            it.downloadEpEid = item.eid
                            it.downloadEpShowdate = item.ep_show_date
                            it.downloadEpTitle = item.ep_title
                            it.selectedDownloadIndex = position
                            it.isSelectDownload = true
                            requestImageUrl()

                            val eventName = "af_download"
                            val eventValue: MutableMap<String, Any?> =
                                HashMap()
                            eventValue["af_content"] =
                                viewModel.seriesItem.title.toString() + " (" + read(
                                    context,
                                    CODE.CURRENT_LANGUAGE,
                                    "en"
                                ) + ")"
                            eventValue["af_content_id"] = viewModel.seriesItem.sid
                            eventValue["af_episode"] =
                                viewModel.seriesItem.title.toString() + " - " + item.ep_title + " (" + read(
                                    context,
                                    CODE.CURRENT_LANGUAGE,
                                    "en"
                                ) + ")"
                            eventValue["af_episode_id"] = item.eid
                            setAppsFlyerEvent(context, eventName, eventValue)
                        }
                    }
                }
            })

            setOnDownloadCancelClickListener(object :
                RecyclerViewBaseAdapter.OnDownloadCancelClickListener {
                override fun onItemClick(item: Any, position: Int) {
                    if (::downLoadAsyncTask.isInitialized && !downLoadAsyncTask.isCancelled) {
                        viewModel.isDownloadException = true
                        downLoadAsyncTask.cancel(true)
                    }
                }
            })
        }
    }

    private fun loadEpCheck() {
        viewModel.requestCheckEp()
    }

    //에피소드 선택구매 요청
    private fun requestEpisodeSelectPurchase(unlockType: String) {
        var ep_list: String = viewModel.epList.toString()
        ep_list = ep_list.trim { it <= ' ' }.replace(" ", "")
        ep_list = ep_list.substring(1, ep_list.length - 1)

        var epTitle_list = viewModel.epTitleList.toString()
        epTitle_list = epTitle_list.replace(", ", ",")
        epTitle_list = epTitle_list.substring(1, epTitle_list.length - 1)
        val setPurchaseEpisode: Call<PurchaseEpisode> =
            service.setPurchaseSelectEpisode(
                read(context, CODE.CURRENT_LANGUAGE, "en"),
                ep_list, unlockType
            )
        setPurchaseEpisode.enqueue(object : Callback<PurchaseEpisode?> {
            override fun onResponse(
                call: Call<PurchaseEpisode?>,
                response: Response<PurchaseEpisode?>
            ) {
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if ("00" == it.retcode) {
                                var eventName = "af_unlock_rent"
                                val eventValue: MutableMap<String, Any?> =
                                    HashMap()
                                eventValue["af_content"] =
                                    viewModel.seriesItem.title.toString() + " (" + read(
                                        context,
                                        CODE.CURRENT_LANGUAGE,
                                        "en"
                                    ) + ")"
                                eventValue["af_content_id"] = viewModel.seriesItem.sid
                                eventValue["af_episode"] = epTitle_list
                                eventValue["af_episode_id"] = ep_list
                                eventValue["af_quantity"] =
                                    CommonUtil.convertEpQuantity(viewModel.allbuy_count)
                                if ("store" == unlockType) {
                                    eventName = "af_unlock_permanent"
                                    eventValue["af_price"] = viewModel.allbuy_coin
                                } else {
                                    eventName = "af_unlock_rent"
                                    eventValue["af_price"] = viewModel.allbuyRentCoin
                                }
                                setAppsFlyerEvent(context, eventName, eventValue)

                                epPurchaseDialog.visibility = View.GONE

                                initEpPurchaseSuccesDialog()

//                                    requestServer()
                                if ("" != it.user_coin) {
                                    write(context, CODE.LOCAL_coin, it.user_coin)
                                }
                            } else if ("202" == it.retcode) {
                                startActivity(Intent(context, CoinActivity::class.java))
                            } else {
                                if ("" != it.msg) {
                                    showToast(it.msg, context)
                                }
                            }
                        }

                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                call: Call<PurchaseEpisode?>,
                t: Throwable
            ) {
//                    hideProgress()
                try {
//                        checkNetworkConnection(
//                            context,
//                            t,
//                            viewError
//                        )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun initEpPurchaseSuccesDialog() {
        epPurchaseSuceesDialog.apply {
            visibility = View.VISIBLE
            successTitleTextView.text = viewModel.seriesItem.title
            successEpCountTextView.text = getString(R.string.str_episodes_seq_format1, viewModel.epList.size)
//            successRemainTime.text
//            successTime.text
//            setOnClickListener { }
            viewModel.epList.clear()
            viewModel.epTitleList.clear()
            doneButton.setOnClickListener {
                showEp()
                epPurchaseSuceesDialog.visibility = View.GONE
            }
        }
    }

    /**
     * 리스트에서 선택한 회차부터 마지막 회차까지 구매가능한 회차 계산
     *
     * @param
     * @param ep_seq
     */
    private fun calcPurchaseCurrentToLastEp(ep_seq: Int) {
        viewModel.items.forEach {
            if (it is DataEpisode) {
                if (it.ep_seq <= it.ep_seq) {
                    if ("0" == it.isunlocked) {
                        viewModel.selectedEpList?.add(viewModel.selectBuyPosibilityCount, it.eid)
                        viewModel.selectBuyPosibilityCount++
                    }
                }
            }
        }
    }

    //전체구매 코인 계산
    fun allBuyCal(): Int {
        viewModel.let {
            it.allbuy_possibility_count = 0
            it.allbuy_count = 0
            it.allbuySaveRate = 0f
            it.allbuy_coin = 0
            it.allbuyRentCoin = 0
            it.arr_episode.forEach { item ->
                if (item.isChecked) {
                    it.allbuy_coin = it.allbuy_coin + item.ep_store_price
                    it.allbuyRentCoin = it.allbuyRentCoin + item.ep_rent_price
                    it.allbuy_count++
                }
                if (item.possibility_allbuy) {
                    it.allbuy_possibility_count++
                }
            }// 대여
            // 소장
            when {
                it.allbuy_count in 2..2 -> {
                    it.allbuySaveRate = 0f
                }
                it.allbuy_count in 3..9 -> {
                    it.allbuySaveRate = 0.1f
                }
                it.allbuy_count in 10..29 -> {
                    it.allbuySaveRate = 0.2f
                }
                it.allbuy_count >= 30 -> {
                    it.allbuySaveRate = 0.3f
                }
            }
            it.allbuy_coin = it.allbuy_coin - Math.round(it.allbuy_coin * it.allbuySaveRate)

            it.allbuyRentCoin =
                it.allbuyRentCoin - Math.round(it.allbuyRentCoin * it.allbuySaveRate)

            val saveRate = "${(it.allbuySaveRate * 100).toInt()}%"
            savePurchaseTextView.text = getString(R.string.str_purchase_save_rate_format, saveRate)

            if (purchaseButton.isSelected) {
                totalTextView.text = "${viewModel.allbuy_coin}"
            }
            if (rentalButton.isSelected) {
                totalTextView.text = "${viewModel.allbuyRentCoin}"
            }

            if (it.selectEpItem.user_cash.toInt() >= totalTextView.text.toString().toInt()) {
                unlockButton.text = getString(R.string.str_buy)
            } else {
                unlockButton.text = getString(R.string.str_charge)
            }
            if (it.allbuy_count > 0) {
                unlockButton.isEnabled = true
                epPurchaseDialog.visibility = View.VISIBLE
            } else {
                unlockButton.isEnabled = false
                epPurchaseDialog.visibility = View.GONE
            }

            epPurchaseCountTextView.text = "${viewModel.allbuy_count}"

            if (viewModel.epList.isNotEmpty()) {
                toolbar.toolbarTitle.text =
                    getString(R.string.str_episodes_seq_format1, viewModel.allbuy_count)
            } else {
                toolbar.toolbarTitle.text = ""
            }

            return it.allbuy_coin
        }
    }

    //전체구매 리스트 전체 선택
    fun allBuyAll(isAllCheck: Boolean): Int {
        viewModel.let {
            it.epList.clear()
            it.epTitleList.clear()

            it.allbuy_possibility_count = 0
            it.allbuy_count = 0
            it.allbuySaveRate = 0f
            it.allbuy_coin = 0
            it.allbuyRentCoin = 0
            it.arr_episode.forEach { item ->
                if (item.possibility_allbuy) {
                    item.isChecked = isAllCheck
                    item.isCheckVisible = isAllCheck
                    if (isAllCheck) {
                        it.epList.add(it.allbuy_count, item.eid)
                        it.allbuy_coin = it.allbuy_coin + item.ep_store_price
                        it.allbuyRentCoin = it.allbuyRentCoin + item.ep_rent_price
                        it.allbuy_count++
                    }
                }
            }
            recyclerView.adapter?.notifyDataSetChanged()
            when {
                it.allbuy_count in 2..2 -> {
                    it.allbuySaveRate = 0f
                }
                it.allbuy_count in 3..9 -> {
                    it.allbuySaveRate = 0.1f
                }
                it.allbuy_count in 10..29 -> {
                    it.allbuySaveRate = 0.2f
                }
                it.allbuy_count >= 30 -> {
                    it.allbuySaveRate = 0.3f
                }
            }
            it.allbuy_coin = it.allbuy_coin - Math.round(it.allbuy_coin * it.allbuySaveRate)

            it.allbuyRentCoin =
                it.allbuyRentCoin - Math.round(it.allbuyRentCoin * it.allbuySaveRate)

            val saveRate = "${(it.allbuySaveRate * 100).toInt()}%"
            savePurchaseTextView.text = getString(R.string.str_purchase_save_rate_format, saveRate)

            if (purchaseButton.isSelected) {
                totalTextView.text = "${viewModel.allbuy_coin}"
            }
            if (rentalButton.isSelected) {
                totalTextView.text = "${viewModel.allbuyRentCoin}"
            }

            if (it.selectEpItem.user_cash.toInt() >= totalTextView.text.toString().toInt()) {
                unlockButton.text = getString(R.string.str_buy)
            } else {
                unlockButton.text = getString(R.string.str_charge)
            }
            if (it.allbuy_count > 0) {
                unlockButton.isEnabled = true
                epPurchaseDialog.visibility = View.VISIBLE
            } else {
                unlockButton.isEnabled = false
                resetDefaultView()
            }

            epPurchaseCountTextView.text = "${viewModel.allbuy_count}"

            if (viewModel.epList.isNotEmpty()) {
                toolbar.toolbarTitle.text =
                    getString(R.string.str_episodes_seq_format1, viewModel.allbuy_count)
            } else {
                toolbar.toolbarTitle.text = ""
            }

            return it.allbuy_coin
        }
    }

    private fun showWaitFreeInfoAlert(msg: String?) {
        try {
            val innerView: View =
                layoutInflater.inflate(R.layout.dialog_wait_free, null)
            val dialog = initDialog(innerView)
            val msgTextView = innerView.findViewById<TextView>(R.id.msgTextView)
            msgTextView.text = msg
            val btnConfirm =
                innerView.findViewById<Button>(R.id.btn_confirm)
            btnConfirm.setOnClickListener {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}