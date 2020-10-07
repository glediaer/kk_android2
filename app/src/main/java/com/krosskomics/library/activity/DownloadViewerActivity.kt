package com.krosskomics.library.activity

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.data.DataImage
import com.krosskomics.library.viewmodel.DownloadViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.getScreenHeight
import com.krosskomics.util.FileUtils.deleteDir
import com.krosskomics.util.FileUtils.fileToByte
import com.krosskomics.util.FileUtils.generateKey
import com.krosskomics.util.FileUtils.getStream
import com.krosskomics.util.FileUtils.writeFile2
import com.krosskomics.util.PreCachingLayoutManager
import com.krosskomics.viewer.adapter.ViewerAdapter
import com.scottyab.aescrypt.AESCrypt
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_content.recyclerView
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.toolbar
import java.io.File
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.*

class DownloadViewerActivity : ToolbarTitleActivity() {
    private val TAG = "DownloadViewerActivity"

    public override val viewModel: DownloadViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DownloadViewModel(application) as T
            }
        }).get(DownloadViewModel::class.java)
    }

    private var pDialog: ProgressDialog? = null
    var autoScrollTimer: CountDownTimer? = null

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_viewer
        return R.layout.activity_viewer
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_downloaded))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_downloaded)
        super.initLayout()

        requestViewerData()
    }

    override fun onDestroy() {
        deleteDir(filesDir.absolutePath.toString() + "/decFile")
        //        FileUtils.deleteDir(KJKomicsApp.DOWNLOAD_ROOT_PATH + "decFile");
        super.onDestroy()
        if (pDialog != null) {
            if (pDialog?.isShowing!!) {
                pDialog?.dismiss()
            }
            pDialog = null
        }
        autoScrollCancel()
    }

    private fun requestViewerData() {
        // 기존 디코딩 된 파일 있으면 삭제
        deleteDir(filesDir.absolutePath.toString() + "/decFile")
        //        FileUtils.deleteDir(KJKomicsApp.DOWNLOAD_ROOT_PATH + "decFile");
        viewModel.mFile = File(viewModel.mPath)
        DecodeFromFile().execute()
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
    }

    override fun initModel() {
        intent?.apply {
            viewModel.title = extras?.getString("episode_title") ?: ""
            viewModel.mPath = extras?.getString("path") ?: ""
            viewModel.isVerticalView = null == intent.extras?.getString("isVerticalView") ||
                    "true" == intent.extras?.getString("isVerticalView")
            viewModel.revPager = "true" == intent.extras?.getString("revPager")
        }
    }

    /**
     * Background Async Task to download file
     */
    inner class DecodeFromFile :
        AsyncTask<String?, String?, String?>() {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        override fun onPreExecute() {
            super.onPreExecute()
            try {
                pDialog = ProgressDialog(this@DownloadViewerActivity).apply {
                    setTitle("#$title")
                    setIndeterminate(false)
                    if (viewModel.mFile.list() != null) {
                        pDialog?.setMax(viewModel.mFile.list().size)
                    }
                    setCanceledOnTouchOutside(false)
                    setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    show()
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
                KJKomicsApp.DOWNLOAD_COUNT = 0
                val secretKeySpec = generateKey(CODE.ENC_PASSWORD)
                for (i in viewModel.mFile.list().indices) {
                    val item = DataImage()
                    try {
                        val fileName: String = viewModel.mFile.list().get(i)
                        var imgDecode = fileToByte(getStream(viewModel.mPath + "/" + fileName)!!)
                        imgDecode = AESCrypt.decrypt(
                            secretKeySpec,
                            CODE.ivBytes,
                            imgDecode
                        )
                        // bitmap to save file
                        var indexFormat = fileName.split("_".toRegex()).toTypedArray()[0]
                        indexFormat = indexFormat.substring(indexFormat.length - 3)
                        if (!TextUtils.isEmpty(indexFormat)) {
                            item.indexFormat = indexFormat.toInt()
                        }
                        writeFile2(
                            filesDir.absolutePath.toString() + "/decFile",
                            fileName,
                            imgDecode
                        )
                        //                        FileUtils.writeFile2(KJKomicsApp.DOWNLOAD_ROOT_PATH + "decFile", fileName, imgDecode);
                        val decPath = filesDir.absolutePath.toString() + "/decFile/" + fileName
                        //                        decPath = KJKomicsApp.DOWNLOAD_ROOT_PATH + "decFile/" + fileName;
                        item.decPath = decPath
                        // get ratio
                        if (viewModel.mFile.list().get(i).contains("_")) {
                            var splitFileName: Array<String> =
                                viewModel.mFile.list().get(i).split("_".toRegex()).toTypedArray()
                            splitFileName = splitFileName[1].split("\\.".toRegex()).toTypedArray()
                            var ratio = ""
                            if (splitFileName[0].contains("p")) {
                                ratio = splitFileName[0].replace("p", ".")
                            } else if (splitFileName[0].contains(",")) {
                                ratio = splitFileName[0].replace(",", ".")
                            }
                            if ("" != ratio) {
                                item.ratio = ratio.toFloat()
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: GeneralSecurityException) {
                        e.printStackTrace()
                    }
                    viewModel.items.add(i, item)
                    publishProgress("" + KJKomicsApp.DOWNLOAD_COUNT)
                }
                Collections.sort<DataImage>(viewModel.items as ArrayList<DataImage>, DataImage.seq)
            } catch (e: java.lang.Exception) {
//                Crashlytics.logException(java.lang.Exception(DownloadViewerActivity.TAG + " " + "DecodeFromFile : " + "decPath : " + decPath + ", message : " + e.message))
            }

            return null
        }

        /**
         * Updating progress bar
         */
        override fun onProgressUpdate(vararg progress: String?) {
            // setting progress percentage

            // setting progress percentage
            try {
                pDialog?.progress = progress[0]!!.toInt()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String?) {

            // dismiss the dialog after the file was downloaded
//            dismissDialog(progress_bar_type);
            try {
                if (!this@DownloadViewerActivity.isFinishing && pDialog != null && pDialog?.isShowing!!) {
                    pDialog?.dismiss()
                }
                if (viewModel.isVerticalView) {
                    recyclerView?.apply {
                        layoutManager = PreCachingLayoutManager(context)
                        (layoutManager as PreCachingLayoutManager).orientation = LinearLayoutManager.VERTICAL
                        (layoutManager as PreCachingLayoutManager).setExtraLayoutSpace(
                            getScreenHeight(
                                context
                            )
                        )

                        recyclerView.adapter = ViewerAdapter(
                            viewModel.items,
                            recyclerViewItemLayoutId,
                            context
                        )
                    }
                } else {
                    recyclerView?.apply {
                        layoutManager = PreCachingLayoutManager(context)
                        (layoutManager as PreCachingLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
                        (layoutManager as PreCachingLayoutManager).setExtraLayoutSpace(
                            getScreenHeight(
                                context
                            )
                        )

                        recyclerView.adapter = ViewerAdapter(
                            viewModel.items,
                            R.layout.item_viewer_comic,
                            context
                        )
                        val snapHelper = PagerSnapHelper()
                        snapHelper.attachToRecyclerView(recyclerView)
                    }
                }
                KJKomicsApp.DOWNLOAD_COUNT = 0
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setAutoScroll(speed: Int) {
        settingBottomView.visibility = View.GONE
        autoScrollCancel()
//        isAutoScroll = KJKomicsApp.autoscroll
        KJKomicsApp.autoscroll = 0
        val totalScrollTime = Long.MAX_VALUE
        var scrollPeriod = 20L
        var heightToScroll = 20
        when (speed) {
            1 -> {
                scrollPeriod = 20L
                heightToScroll = 15
            }
            2 -> {
                scrollPeriod = 20L
                heightToScroll = 35
            }
            3 -> {
                scrollPeriod = 30L
                heightToScroll = 50
            }
        }
//        hideToggleToolBar()
        recyclerView.post {
            autoScrollTimer = object : CountDownTimer(totalScrollTime, scrollPeriod) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
                        recyclerView.smoothScrollBy(0, heightToScroll)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFinish() {}
            }.start()
        }
    }

    private fun autoScrollCancel() {
        try {
            autoScrollTimer?.cancel()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}