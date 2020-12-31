package com.krosskomics.mainmenu.activity

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.common.data.DataBook
import com.krosskomics.common.data.DataWaitFreeTerm
import com.krosskomics.mainmenu.adapter.WaitFreeTermAdapter
import com.krosskomics.mainmenu.viewmodel.MainMenuViewModel
import kotlinx.android.synthetic.main.activity_waitfree.*

class WaitFreeActivity : RecyclerViewBaseActivity() {
    private val TAG = "WaitFreeActivity"

    public override val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainMenuViewModel(
                    application
                ) as T
            }
        }).get(MainMenuViewModel::class.java)
    }

    lateinit var dateViewItems: ArrayList<TextView>

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_waitfree
        return R.layout.activity_waitfree
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_waitforfree))
    }

    override fun initLayout() {
        viewModel.tabIndex = 2
        viewModel.param1 = "waitorpay"
        super.initLayout()
    }

    fun initWaitFreeTermRecyclerView() {
        remainRecyclerView.apply {
            adapter = WaitFreeTermAdapter(
                viewModel.waitFreeTermItems
            )
            (adapter as WaitFreeTermAdapter).setOnItemClickListener(object : WaitFreeTermAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is DataWaitFreeTerm) {
                        viewModel.isRefresh = true
                        viewModel.param2 = item.p_wop_term
                        viewModel.waitFreeTermItems.forEachIndexed { index, dataWaitFreeTerm ->
                            dataWaitFreeTerm.isSelect = index == position
                        }
                        (adapter as WaitFreeTermAdapter).notifyDataSetChanged()
                        requestServer()
                    }
                }
            })
        }
//        dateViewItems = arrayListOf(oneDateTextView, twoDateTextView, threeDateTextView,
//            fourDateTextView, fiveDateTextView)
//        dateViewItems.forEach { dateView ->
//            dateView.setOnClickListener {
//                resetDateViewItems()
//                it.isSelected = true
//                requestServer()
//            }
//        }
    }

    private fun resetDateViewItems() {
        dateViewItems.forEach { it.isSelected = false }
    }
}