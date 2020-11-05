package com.krosskomics.library.fragment

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataGift
import com.krosskomics.common.fragment.RecyclerViewBaseFragment
import com.krosskomics.common.model.Gift
import com.krosskomics.library.viewmodel.GiftBoxViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GiftBoxFragment : RecyclerViewBaseFragment() {

    override val viewModel: GiftBoxViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GiftBoxViewModel(requireContext()) as T
            }
        }).get(GiftBoxViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_gift
        return R.layout.fragment_giftbox
    }

    override fun initRecyclerViewAdapter() {
        super.initRecyclerViewAdapter()
        (recyclerView.adapter as RecyclerViewBaseAdapter).apply {
            setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?) {
                    if (item is DataGift) {
                        requestGetGift(item.seq)
                    }
                }
            })

            setOnDelteItemClickListener(object : RecyclerViewBaseAdapter.OnDeleteItemClickListener {
                override fun onItemClick(item: Any) {
                    if (item is DataBook) {
                        // remove request

                    }
                }
            })
        }
    }

    override fun showEmptyView() {
        super.showEmptyView()
        emptyView?.apply {
            errorTitle.text = getString(R.string.msg_empty_gift)
            errorMsg.visibility = View.GONE
            goSeriesButton.visibility = View.GONE
        }
    }

    /**
     * 선물 받기
     */
    private fun requestGetGift(seq: String?) {
        if (isAdded) {
            val getGift = ServerUtil.service.setGetGift(
                read(context, CODE.CURRENT_LANGUAGE, "en"),
                "earn_gift", seq
            )
            getGift.enqueue(object : Callback<Gift> {
                override fun onResponse(
                    call: Call<Gift>,
                    response: Response<Gift>
                ) {
                    try {
                        if ("00" == response.body()?.retcode) {
                            val coin = response.body()?.user_coin
                            if ("" != coin && coin != null) {
                                write(context, CODE.LOCAL_coin, coin)
                            }
//                            showRecieveGiftDialog()
                            requestServer()
//                            requestHistory(true)
                        } else {
                            if ("" != response.body()?.msg) {
                                showToast(response.body()?.msg, context)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<Gift>, t: Throwable) {
                    try {
                        t.printStackTrace()
//                        checkNetworkConnection(context, t, actBinding.viewError)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }
}