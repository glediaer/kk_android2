package com.krosskomics.mynews.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.model.Default
import com.krosskomics.common.model.News
import com.krosskomics.mynews.viewmodel.MyNewsViewModel
import com.krosskomics.util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyNewsActivity : ToolbarTitleActivity() {
    private val TAG = "MyNewsActivity"

    public override val viewModel: MyNewsViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MyNewsViewModel(application) as T
            }
        }).get(MyNewsViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_mynews
        return R.layout.activity_mynews
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_my_news))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_my_news)
        viewModel.listType = "mynews"
        super.initLayout()
    }

    fun setReadParams(ntype: String, nid: String) {
//        sendReadNewsApi()
    }

    fun sendReadNewsApi(type: String, param: String) {
        val api: Call<Default> = ServerUtil.service.sendReadNews(
            type,
            param
        )
        api.enqueue(object : Callback<Default> {
            override fun onResponse(call: Call<Default>, response: Response<Default>) {
                if (response.body() != null) {
                }
            }

            override fun onFailure(call: Call<Default>, t: Throwable) {
            }
        })
    }
}