package com.krosskomics.series.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.model.Episode
import com.krosskomics.common.viewmodel.BaseViewModel
import com.krosskomics.series.adapter.SeriesAdapter
import com.krosskomics.series.viewmodel.SeriesViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.convertUno
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.FileUtils
import com.krosskomics.viewer.activity.ViewerActivity
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_content.recyclerView
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.view_action_item.view.*
import kotlinx.android.synthetic.main.view_content_like_white.*
import kotlinx.android.synthetic.main.view_toolbar.view.*
import java.util.*

class SeriesActivity : ToolbarTitleActivity() {
    private val TAG = "SeriesActivity"

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
        toolbar.apply {
            actionItem.visibility = View.VISIBLE
            actionItem.giftboxImageView.visibility = View.GONE
            actionItem.searchImageView.visibility = View.GONE
            actionItem.scribeImageView.visibility = View.VISIBLE
            actionItem.scribeImageView.setOnClickListener {
                it.isSelected = !it.isSelected
                // 구독 요청/해지 api request
            }

            toolbarTitle.visibility = View.VISIBLE
            toolbarTitle.text = toolbarTitleString
        }
    }

    override fun initModel() {
        intent?.apply {
            toolbarTitleString = extras?.getString("title").toString()
            viewModel.sid = extras?.getString("sid").toString()
        }
        super.initModel()
        viewModel.getCheckEpResponseLiveData().observe(this, this)
        viewModel.getImageUrlResponseLiveData().observe(this, this)
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    private fun requestImageUrl() {
        viewModel.requestImageUrl()
    }

    override fun onChanged(t: Any?) {
        if (t is Episode) {
            when(viewModel.requestType) {
                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_A -> {
                    if ("00" == t.retcode) {
                        viewModel.seriesItem = t.series!!
                        setMainContentView(t)
                        setHeaderContentView(t)
                    }
                }

                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_B -> {
                    when(t.retcode) {
                        "00" -> showEp()
                        "201" -> goLoginAlert(context)
                        "202" -> goCoinAlert(context)
                        "205" -> {
                            // 구매팝업
                            // ablestore == 1 소장구매 가능
                            // ablerent == 1 렌트 가능
                            showPurchaseRentDialog(t.episode);
                        }
                        else -> {
                            t.msg?.let {
                                CommonUtil.showToast(it, context)
                            }
                        }
                    }
                }

                BaseViewModel.REQUEST_TYPE.REQUEST_TYPE_C -> {
                    when(t.retcode) {
                        "00" -> goDownload(t)
                        "201" -> goLoginAlert(context)
                        "202" -> goCoinAlert(context)
                        "205" -> {
                            // 구매팝업
                            // ablestore == 1 소장구매 가능
                            // ablerent == 1 렌트 가능
                            showPurchaseRentDialog(t.episode);
                        }
                        else -> {
                            t.msg?.let {
                                CommonUtil.showToast(it, context)
                            }
                        }
                    }
//                    Episode body = response.body();
//                    if ("00".equals(body.retcode)) {
//                        if (null != body.episode.ep_contents && 0 < body.episode.ep_contents.length()) {
//                            arr_url = (body.episode.ep_contents).split(",");
//                            mDownloadExpire = body.episode.download_expire;
//                            saveThumbnailFile(eid);
//                            KJKomicsApp.DOWNLOAD_COUNT = 0;
//                            mIsCompleteDownload = false;
//                            downLoadAsyncTask = new DownloadFileFromURL();
//                            downLoadAsyncTask.execute();
//                        }
//                    } else {
//                        if (!"".equals(body.msg)) {
//                            CommonUtil.showToast(body.msg, context);
//                        }
//                        isSelectDownload = false;
//                    }
                }
            }
        }
    }

    private fun goDownload(t: Episode) {
        t.episode?.apply {
            if (ep_contents.isNotEmpty()) {
                viewModel.imageUrlItems = (ep_contents).split(",").toString()
                viewModel.downloadExpire = download_expire
                saveThumbnailFile(viewModel.downloadEpEid)
                KJKomicsApp.DOWNLOAD_COUNT = 0
                viewModel.isCompleteDownload = false
//                downLoadAsyncTask = new DownloadFileFromURL();
//                            downLoadAsyncTask.execute();
            }
        }
    }

    private fun saveThumbnailFile(eid: String) {
        // save series thumbnail
        val bitmap: Bitmap = FileUtils.getBitmapFromURL(viewModel.seriesItem.image1)!!
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
        for (item in viewModel.items) {
            if (item is DataEpisode && eid == item.eid) {
                val epBitmap: Bitmap = FileUtils.getBitmapFromURL(item.image)!!
                FileUtils.saveBitmapToFileCache(
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

    private fun setHeaderContentView(t: Episode) {
        t.series?.let {
            toolbar.actionItem.scribeImageView.isSelected = it.issubscribed != "0"
            mainImageView.setImageURI(it.image)
            tv_like_count.text = it.like_cnt

            // info
            genreTextView.text = it.genre1
            contentTitleTextView.text = it.title
            writerTextView.text = it.writer1
            descTextView.text = it.long_desc
            descButton.setOnClickListener { view ->
                view.isSelected = !view.isSelected
                if (view.isSelected) {
                    descTitleTextView.text = getString(R.string.str_done)
                    descTextView.visibility = View.VISIBLE
                } else {
                    descTitleTextView.text = getString(R.string.str_description)
                    descTextView.visibility = View.GONE
                }
            }
            // permanent
            permanentCntTextView.text = it.sticket.toString()
            permanentCntTextView.isSelected = it.sticket > 0
            rentalCntTextView.text = it.rticket.toString()
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
            epCountTextView.text = getString(R.string.str_episodes_seq_format1, t.list?.size)
            listOrderImageView.setOnClickListener { view ->
                view.isSelected = !view.isSelected
                // 리스트 정렬 변경
                if (view.isSelected) {
                    Collections.sort(t.list, DataEpisode.seq)
                } else {
                    Collections.sort(t.list, DataEpisode.seq)
                    t.list?.reverse()
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
            if ("0" == it.read_next_ep_seq || read(context, CODE.LOCAL_loginYn, "N")
                    .equals("N", ignoreCase = true)) {
                // 첫화보기
                firstContinueEpView.visibility = View.VISIBLE
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
                t.list?.forEach {item ->
                    if (item.eid == viewModel.nextEp) {
                        // 현재 선택한 회차부터 구매가능한 회차까지 계산
                        viewModel.selectBuyPosibilityCount = 0
                        calcPurchaseCurrentToLastEp(item.ep_seq)
                        viewModel.item = item
                        loadEpCheck()
                        return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun showPurchaseRentDialog(episode: DataEpisode?) {

    }

    private fun showEp() {
        viewModel.item.let {
            val intent = Intent(context, ViewerActivity::class.java)
            val bundle = Bundle().apply {
                putString("title", it.ep_title)
                putString("eid", it.eid)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun initRecyclerViewAdapter() {
        if (viewModel.listViewType == 0) {
            recyclerView?.layoutManager = GridLayoutManager(context, 3)
        } else {
            recyclerView?.layoutManager = LinearLayoutManager(context)
        }
        recyclerView.adapter = SeriesAdapter(viewModel.items, recyclerViewItemLayoutId, context)
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataEpisode) {
                    viewModel.item = item
                    loadEpCheck()
                }
            } })
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
                        }
                    }
                }
            })
        }
    }

    private fun loadEpCheck() {
        viewModel.requestCheckEp()
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
}