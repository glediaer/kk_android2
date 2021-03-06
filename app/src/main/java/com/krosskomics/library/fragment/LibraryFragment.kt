package com.krosskomics.library.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.User
import com.krosskomics.library.activity.DownloadEpActivity
import com.krosskomics.library.activity.LibraryActivity
import com.krosskomics.library.viewmodel.LibraryViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.FileUtils
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import kotlinx.android.synthetic.main.view_mytoon_category.*
import kotlinx.android.synthetic.main.view_mytoon_filter.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LibraryFragment : RecyclerViewBaseFragment() {
    var currentCategory = 0 // all, unlock, download

    override val viewModel: LibraryViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LibraryViewModel(requireContext()) as T
            }
        }).get(LibraryViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (currentCategory == 2) {
            viewModel.items.clear()
            getDownloadedData()
        }
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_more
        return R.layout.fragment_library
    }

    override fun initLayout() {
        initMainView()
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    private fun requestEpDeleteFile(sid: String) {
        var eids = viewModel.mEpExpireDateList.toString()
        eids = eids.trim { it <= ' ' }.replace(" ", "")
        eids = eids.substring(1, eids.length - 1)

        val api = ServerUtil.service.setDeleteEpisodes(CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "delete_download_episode", sid, eids)
        api.enqueue(object : Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
                try {
                    t.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 삭제
     */
    private fun requestDeleteLibrary() {
        var sids = viewModel.mSeriesList.toString()
        sids = sids.trim { it <= ' ' }.replace(" ", "")
        sids = sids.substring(1, sids.length - 1)
            val api = ServerUtil.service.setDeleteContents(CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
                "delete_library", sids)
            api.enqueue(object : Callback<Default> {
                override fun onResponse(call: Call<Default>, response: Response<Default>) {
                    try {
                        if (response.isSuccessful) {
                            if ("00" == response.body()!!.retcode) {
                                viewModel.page = 1
                                viewModel.isRefresh = true
                                viewModel.mSeriesList.clear()
                                requestServer()
                            } else {
                                if ("" != response.body()!!.msg) {
                                    CommonUtil.showToast(response.body()!!.msg, context)
                                }
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
//                        checkNetworkConnection(context, t, actBinding.viewError)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    /**
     * 삭제
     */
    private fun requestDeleteFile() {
            var sids = viewModel.mSeriesList.toString()
            sids = sids.trim { it <= ' ' }.replace(" ", "")
            sids = sids.substring(1, sids.length - 1)

            val api = ServerUtil.service.setDeleteContents(CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
                "delete_download_series", sids)
            api.enqueue(object : Callback<Default> {
                override fun onResponse(call: Call<Default>, response: Response<Default>) {
                    try {
                        if (response.isSuccessful) {
                            if ("00" == response.body()!!.retcode) {
                                viewModel.isRefresh = true
                                viewModel.mSeriesList.clear()
                                getDownloadedData()
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun initMainView() {
        super.initMainView()
        activity?.apply {
            toolbarTrash?.setOnClickListener { _ ->
                toolbarTrash.visibility = View.GONE
                toolbarDone.visibility = View.VISIBLE
                viewModel.items.forEach {
                    if (it is DataBook) {
                        it.isCheckVisible = true
                        it.isChecked = true
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
            toolbarDone?.setOnClickListener { _ ->
                toolbarTrash.visibility = View.VISIBLE
                toolbarDone.visibility = View.GONE
                viewModel.items.forEach {
                    if (it is DataBook) {
                        it.isCheckVisible = false
                        it.isChecked = false
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        initCategory()
        initfilter()
    }

    private fun initfilter() {
        filterRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.recentRadioButton -> {
                    viewModel.repository.listType = "R"
                    viewModel.isRefresh = true
                    requestServer()
                }
                R.id.scribeRadioButton -> {
                    viewModel.repository.listType = "S"
                    viewModel.isRefresh = true
                    requestServer()
                }
            }
        }
    }

    private fun initCategory() {
        allTextView.isSelected = true
        unlockTextView.isSelected = false
        downloadTextView.isSelected = false
        allTextView.setOnClickListener {
            resetCategory()
            allTextView.isSelected = true
            activity?.toolbarDone?.visibility = View.GONE
            activity?.toolbarTrash?.visibility = View.VISIBLE
            filterView.visibility = View.VISIBLE
            networkStateView.visibility = View.GONE
            currentCategory = 0

            viewModel.repository.listType = "R"
            requestServer()
        }
        unlockTextView.setOnClickListener {
            resetCategory()
            unlockTextView.isSelected = true
            activity?.toolbarDone?.visibility = View.GONE
            activity?.toolbarTrash?.visibility = View.GONE
            filterView.visibility = View.GONE
            networkStateView.visibility = View.GONE
            currentCategory = 1

            viewModel.repository.listType = "U"
            requestServer()
        }
        downloadTextView.setOnClickListener {
            resetCategory()
            downloadTextView.isSelected = true
            activity?.toolbarDone?.visibility = View.GONE
            activity?.toolbarTrash?.visibility = View.VISIBLE
            filterView.visibility = View.GONE
            networkStateView.visibility = View.VISIBLE
            currentCategory = 2

            if (CommonUtil.getNetworkInfo(requireContext()) != null) {
                requestExpireEpisode()
            } else {
                getDownloadedData()
            }
        }
    }

    private fun resetCategory() {
        allTextView.isSelected = false
        unlockTextView.isSelected = false
        downloadTextView.isSelected = false

        viewModel.items.clear()
        viewModel.mSeriesList.clear()
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                recyclerViewItemLayoutId
            )
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is DataBook) {
                        var intent: Intent
                        if (currentCategory == 2) {     // download
                            intent = Intent(context, DownloadEpActivity::class.java)
                            val bundle = Bundle().apply {
                                putString("path", item.filePath)
                                putString("thumbnail", KJKomicsApp.DOWNLOAD_ROOT_PATH + CommonUtil.convertUno(CommonUtil.read(context, CODE.LOCAL_RID, ""))
                                        + "/thumbnail/" + item.sid + "/")
                                putString("sid", item.sid)
                                putString("title", item.title)
                            }
                            intent.putExtras(bundle)
                        } else {
                            intent = Intent(context, SeriesActivity::class.java).apply {
                                putExtra("sid", item.sid)
                                putExtra("title", item.title)
                            }
                        }
                        startActivity(intent)
                    }
                }
            })

            setOnDelteItemClickListener(object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                override fun onItemClick(item: Any) {
                    if (viewModel.items.size == 0) {
                        return
                    }
                    if (CommonUtil.getNetworkInfo(requireContext()) == null) {
                        CommonUtil.showToast(getString(R.string.msg_disable_remove_file), context)
                        return
                    }
                    if (item is DataBook) {
                        // remove request
//                        if (item.isChecked) {
//                            item.isChecked = false;
//                            viewModel.mSeriesList.remove(item.sid);
//                        } else {
//                            item.isChecked = true;
//                            viewModel.mSeriesList.add(item.sid);
//                        }
                        item.isChecked = true;
                        viewModel.mSeriesList.add(item.sid);
                        when (currentCategory) {
                            0 -> {
                                requestDeleteLibrary()
                            }
                            2 -> {
                                removeFile()
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * 파일 삭제
     */
    private fun removeFile() {
        viewModel.items.forEach {
            if (it is DataBook) {
                it.isCheckVisible = false
                if (it.isChecked) {
                    FileUtils.deleteDir(it.filePath)
                    viewModel.items.remove(it)
                }
            }
        }
        if (viewModel.items.size > 0) {
            getDownloadedData()
        } else {
            viewModel.mFile.delete()
        }
        recyclerView.adapter?.notifyDataSetChanged()

        requestDeleteFile()
        removeThumbnailFile()
    }

    private fun removeThumbnailFile() {
        var seriesPath = KJKomicsApp.DOWNLOAD_ROOT_PATH +
                CommonUtil.convertUno(CommonUtil.read(context, CODE.LOCAL_RID, "")) +
                "/thumbnail/"
        viewModel.mSeriesList.forEach {
            seriesPath = seriesPath + it
            FileUtils.deleteDir(seriesPath)
        }
    }

    override fun showEmptyView() {
        super.showEmptyView()
        emptyView?.apply {
            when(currentCategory) {
                0 -> {
                    emptyNetworkStateView.visibility = View.GONE
                    errorTitle.text = getString(R.string.msg_empty_series)
                    errorMsg.visibility = View.GONE
                    goSeriesButton.text = getString(R.string.str_go_to_series)
                    goSeriesButton.visibility = View.VISIBLE
                }
                1 -> {
                    emptyNetworkStateView.visibility = View.GONE
                    errorTitle.text = getString(R.string.msg_empty_unlock_ep)
                    errorMsg.text = getString(R.string.msg_empty_unlock_ep2)
                    errorMsg.visibility = View.VISIBLE
                    goSeriesButton.text = getString(R.string.str_go_to_series)
                    goSeriesButton.visibility = View.VISIBLE
                }
                2 -> {
                    emptyNetworkStateView?.visibility = View.VISIBLE
                    errorTitle.text = getString(R.string.msg_empty_download)
                    errorMsg.text = getString(R.string.msg_empty_download2)
                    errorMsg.visibility = View.VISIBLE
                    goSeriesButton.text = getString(R.string.str_go_to_download)
                    goSeriesButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun requestExpireEpisode() {
        val api = ServerUtil.service.getUser(CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "expire_episode")
        api.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item!!.retcode) {
                            viewModel.mEpExpireDateList = item.expire_episode!!
                            getDownloadedData()
                        } else {
                            if ("" != item.msg) {
                                CommonUtil.showToast(item.msg, context)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun getDownloadedData() {
        try {
            viewModel.apply {
                mPath = KJKomicsApp.DOWNLOAD_ROOT_PATH +
                        CommonUtil.convertUno(CommonUtil.read(context, CODE.LOCAL_RID, "")!!) + "/" +
                        CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en")
                mFile = File(mPath)
                if (mFile.length() == 0L) {
                    showEmptyView()
                    return
                }
                mFile.listFiles().forEach { it ->
                    Log.e("TAG", "file name : " + it.name)
                    if (!"thumbnail".equals(it.name)) {
                        mFileName = it.name
                        val item = DataBook()
                        val sid = it.name.split("_")[0]
                        val title = it.name.split("_")[1]
                        val genre = it.name.split("_")[2]
                        val writer = it.name.split("_")[3]
                        item.filePath = it.absolutePath
                        item.sid = sid
                        item.title = title
                        item.genre1 = genre.split(" ")[0]
                        item.genre2 = genre.split(" ")[1]
                        item.genre3 = genre.split(" ")[2]
                        item.writer1 = writer.split(" ")[0]
                        item.writer2 = writer.split(" ")[1]
                        item.writer3 = writer.split(" ")[2]

                        if (mEpExpireDateList.isEmpty()) {
                            // check expire date
                            it.listFiles().forEach {
                                // 2019-12-19 19:50:15
                                if (TextUtils.isEmpty(it.name)) return@forEach
                                val expireDateString = it.name.split("_")[3]

                                val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                val expireDate = formatter.parse(expireDateString)

                                // Calendar 객체 생성
                                val cal = Calendar.getInstance()
                                val todayMil = cal.timeInMillis     // 현재 시간(밀리 세컨드)

                                // 파일의 마지막 수정시간 가져오기
//                    val fileDate = Date(it.lastModified())
                                val fileCal = Calendar.getInstance()
                                // 현재시간과 파일 수정시간 시간차 계산(단위 : 밀리 세컨드)
                                fileCal.setTime(expireDate)
                                val diffMil = todayMil - fileCal.getTimeInMillis()

                                //날짜로 계산
                                val diffDay = (diffMil / ONE_DAY_MIL).toInt()
                                if (diffDay > 0 && it.exists()) {
                                    // 파일 삭제
                                    FileUtils.deleteDir(it.absolutePath)
                                }
                            }
                        } else {
                            it.listFiles().forEachIndexed { index, file ->
                                val expireEid = it.name.split("_")[0]
                                mEpExpireDateList.forEach {
                                    if (it == expireEid) {
                                        // 파일 삭제
                                        FileUtils.deleteDir(file.absolutePath)
                                    }
                                }
                            }
                            requestEpDeleteFile(item.sid!!)
                        }

                        val thumbNailPath = KJKomicsApp.DOWNLOAD_ROOT_PATH +
                                CommonUtil.convertUno(
                                    CommonUtil.read(
                                        context,
                                        CODE.LOCAL_RID,
                                        ""
                                    )!!
                                ) + "/thumbnail/" + item.sid + "/"
                        val thumbFile = File(thumbNailPath)
                        thumbFile.listFiles()?.forEach {
                            Log.e("TAG", "thumbnail file name : " + it.name)
                            if (it.name.equals(item.sid + ".png")) {
                                item.image = it.absolutePath
                            }
                        }

                        items.add(item)
                    }
                }

                if (items.size > 0) {
                    showMainView()
                    (activity as LibraryActivity).toolbar.toolbarTrash.visibility = View.VISIBLE
                } else {
                    showEmptyView()
                    (activity as LibraryActivity).toolbar.toolbarTrash.visibility = View.GONE
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}