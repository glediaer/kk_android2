package com.krosskomics.coin.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.coin.viewmodel.CoinViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataCoin
import com.krosskomics.common.model.Coin
import com.krosskomics.util.CODE
import com.krosskomics.util.CommonUtil.read
import kotlinx.android.synthetic.main.activity_main_content.*

class CoinActivity : ToolbarTitleActivity() {
    private val TAG = "CoinActivity"

    override val viewModel: CoinViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CoinViewModel(application) as T
            }
        }).get(CoinViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_coin
        return R.layout.activity_coin
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_coin_shop))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_coin_shop)
        super.initLayout()
    }

    override fun onChanged(t: Any?) {
        if (t is Coin) {
            if ("00" == t.retcode) {
                setMainContentView(t)
            }
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = RecyclerViewBaseAdapter(viewModel.items, recyclerViewItemLayoutId)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataCoin) {
                    if (read(context, CODE.LOCAL_loginYn, "N").equals("Y", ignoreCase = true)) {
//                        if (null != arr_coin && 0 < arr_coin.size) {
//                            data = arr_coin.get(position)
//                        }
//                        // 결제 요청 팝업
//                        payment(data)
                    } else {
                        goLoginAlert(context)
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
        }
    }
}