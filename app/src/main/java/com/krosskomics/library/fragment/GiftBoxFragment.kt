package com.krosskomics.library.fragment

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.R
import com.krosskomics.common.adapter.CommonRecyclerViewAdapter
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataGift
import com.krosskomics.common.fragment.BaseFragment
import com.krosskomics.common.model.Gift
import com.krosskomics.library.viewmodel.GiftBoxViewModel
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil
import com.krosskomics.util.CommonUtil.read
import com.krosskomics.util.CommonUtil.showToast
import com.krosskomics.util.CommonUtil.write
import com.krosskomics.util.ServerUtil
import kotlinx.android.synthetic.main.fragment_genre.recyclerView
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.view_empty_library.view.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_topbutton.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GiftBoxFragment : BaseFragment() {

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

    override fun initModel() {
        viewModel.getMainResponseLiveData().observe(this, this)
    }

    override fun initLayout() {
        initMainView()
    }

    override fun requestServer() {
        viewModel.requestMain()
    }

    override fun onChanged(t: Any?) {
        if (t is Gift) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            } else {
                t.msg?.let {
                    CommonUtil.showToast(it, context)
                }
            }
        }
    }

    private fun initMainView() {
        initRecyclerView()
        topButton.setOnClickListener {
            recyclerView?.smoothScrollToPosition(0)
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (getCurrentItem(recyclerView) > CODE.VISIBLE_LIST_TOPBUTTON_CNT) {
                    topButton.visibility = View.VISIBLE
                } else {
                    topButton.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    //TODO 화면이 바닥에 닿을때 처리
                    if (viewModel.page < viewModel.totalPage) {
                        viewModel.page++
                        requestServer()
                    }
                }
            }
        })
        initRecyclerViewAdapter()
    }

    private fun initRecyclerViewAdapter() {
        recyclerView.adapter =
            CommonRecyclerViewAdapter(
                viewModel.items,
                recyclerViewItemLayoutId
            )
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

    private fun setMainContentView(body: Gift) {
        if (body.list.isNullOrEmpty()) {
            showEmptyView()
            return
        }
        if (viewModel.isRefresh) {
            viewModel.items.clear()
        }
        viewModel.totalPage = body.tot_pages
        body.list?.let {
            showMainView()
            viewModel.items.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
            activity?.apply {
                toolbarTrash?.setOnClickListener { _ ->
                    toolbarTrash.visibility = View.GONE
                    toolbarDone.visibility = View.VISIBLE
                    it.forEach {
//                        it.isCheckVisible = true
//                        it.isChecked = true
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                toolbarDone?.setOnClickListener { _ ->
                    toolbarTrash.visibility = View.VISIBLE
                    toolbarDone.visibility = View.GONE
                    it.forEach {
//                        it.isCheckVisible = false
//                        it.isChecked = false
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
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