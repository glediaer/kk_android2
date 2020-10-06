package com.krosskomics.library.activity

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.data.DataEpisode
import com.krosskomics.common.data.DataFile
import com.krosskomics.common.model.Default
import com.krosskomics.library.viewmodel.DownloadViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.activity_download_ep.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DownloadEpActivity : ToolbarTitleActivity() {
    private val TAG = "DownloadEpActivity"

    public override val viewModel: DownloadViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DownloadViewModel(application) as T
            }
        }).get(DownloadViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_download_ep
        return R.layout.activity_download_ep
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_downloaded))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_downloaded)
        super.initLayout()
        setHeaderView()
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
            viewModel.title = extras?.getString("title") ?: ""
            viewModel.mPath = extras?.getString("path") ?: ""
            viewModel.mThumbnail = extras?.getString("thumbnail") ?: ""
            viewModel.mSid = extras?.getString("sid") ?: ""
        }
        super.initModel()
        getDownloadedData()
    }

    private fun setHeaderView() {
        toolbarTrash?.setOnClickListener { _ ->
            toolbarTrash.visibility = View.GONE
            toolbarDone.visibility = View.VISIBLE
            viewModel.items.forEach { it as DataFile
                it.isCheckVisible = true
                it.isChecked = true
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }
        toolbarDone?.setOnClickListener { _ ->
            toolbarTrash.visibility = View.VISIBLE
            toolbarDone.visibility = View.GONE
            viewModel.items.forEach { it as DataFile
                it.isCheckVisible = false
                it.isChecked = false
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }
        seriesTitleTextView.text = viewModel.title
    }

    private fun getDownloadedData() {
        viewModel.mFile = File(viewModel.mPath)
        if (viewModel.mFile.length() == 0L) {
            return
        }
        viewModel.mFile.listFiles().forEach {
            Log.e("TAG", "file name : " + it.name)
            val item = DataFile()
            val eid = it.name.split("_")[0]
            val title = it.name.split("_")[1]
            val showDate = it.name.split("_")[2]
            val expireDate = it.name.split("_")[3]
            val isVerticalView = it.name.split("_")[4]
            val revPager = it.name.split("_")[5]
            item.filePath = it.absolutePath
            item.eid = eid
            item.ep_title = title
            item.ep_show_date = showDate
            item.expireDate = expireDate
            item.image = viewModel.mThumbnail + item.eid + ".png"
            item.isVerticalView = isVerticalView
            item.revPager = revPager

            viewModel.items.add(item)

//            if (mItems.size > 0) {
//                actBinding.flEmptyDataView.visibility = View.GONE
//                actBinding.flDefaultView.visibility = View.VISIBLE
//                actBinding.ivEdit.visibility = View.VISIBLE
//            } else {
//                actBinding.flEmptyDataView.visibility = View.VISIBLE
//                actBinding.flDefaultView.visibility = View.GONE
//                actBinding.ivEdit.visibility = View.GONE
//            }
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    /**
     * 삭제
     */
    private fun requestDeleteFile() {
            var eids = viewModel.mEpList.toString()
            eids = eids.trim { it <= ' ' }.replace(" ", "")
            eids = eids.substring(1, eids.length - 1)

            val api = ServerUtil.service.setDeleteEpisodes(
                CommonUtil.read(this, CODE.CURRENT_LANGUAGE, "en"),
                "delete_download_episode", viewModel.mSid, eids)
            api.enqueue(object : Callback<Default> {
                override fun onResponse(call: Call<Default>, response: Response<Default>) {
                    try {
                        if (response.isSuccessful) {
                            if ("00" == response.body()!!.retcode) {
                                viewModel.mEpList.clear()
//                                if (actBinding.flEditMode.isShown()) {
//                                    actBinding.flEditMode.setVisibility(View.GONE)
//                                    actBinding.bottomNavigationView.setVisibility(View.VISIBLE)
//                                    actBinding.ivEdit.isSelected = false
//                                }
                            }
                            if ("" != response.body()!!.msg) {
                                CommonUtil.showToast(response.body()!!.msg, context)
                            }
                        } else {
                            CommonUtil.showToast(getString(R.string.msg_fail_dataloading), context)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<Default>, t: Throwable) {
                    try {
                        t.printStackTrace()
                        checkNetworkConnection(context, t, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }
}