package com.krosskomics.event.activity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.model.Coin
import com.krosskomics.event.adapter.EventAdapter
import com.krosskomics.event.viewmodel.EventViewModel
import com.krosskomics.mynews.viewmodel.MyNewsViewModel
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_main_content.recyclerView
import retrofit2.Call
import retrofit2.Response

class EventActivity : ToolbarTitleActivity() {
    private val TAG = "EventActivity"

    public override val viewModel: EventViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return EventViewModel(application) as T
            }
        }).get(EventViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_event
        return R.layout.activity_event
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_event))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_event)
        viewModel.listType = "list"
        super.initLayout()
        initPromotionView()
    }

    private fun initPromotionView() {
        procodeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.length < 8) {
                    promotionCodeView.isSelected = false
                    registerTextView.isEnabled = false
                } else {
                    promotionCodeView.isSelected = true
                    registerTextView.isEnabled = true
                }
            }
        })

        registerTextView.setOnClickListener {
            // request server
            requestPromotionCode()
        }
    }

    private fun requestPromotionCode() {
        val api = ServerUtil.service.setPromotionCode(
            CommonUtil.read(context, CODE.CURRENT_LANGUAGE, "en"),
            "reg_coupon", registerTextView.text.toString())
        api.enqueue(object : retrofit2.Callback<Coin> {
            override fun onResponse(call: Call<Coin>, response: Response<Coin>) {
                try {
                    if (response.isSuccessful) {
                        val item = response.body()
                        if ("00" == item?.retcode) {
                            val coin = item.user_coin.toString()
                            CommonUtil.write(context, CODE.LOCAL_coin, coin)
                            if ("" != item.msg) {
                                CommonUtil.showToast(item.msg, context)
                                registerTextView.text = "";
                            }
                        } else if ("203" == item?.retcode) {
                            goLoginAlert(context)
                        } else {
                            if ("" != item?.msg) {
                                CommonUtil.showToast(item?.msg, context)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Coin>, t: Throwable) {
                try {
                    checkNetworkConnection(context, t, errorView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}