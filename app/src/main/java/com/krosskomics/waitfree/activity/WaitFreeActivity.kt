package com.krosskomics.waitfree.activity

import android.content.Intent
import android.widget.TextView
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.waitfree.adapter.WaitFreeRemainTimeAdapter
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_waitfree.*
import kotlinx.android.synthetic.main.activity_waitfree.recyclerView

class WaitFreeActivity : RecyclerViewBaseActivity() {
    private val TAG = "WaitFreeActivity"

//    private val viewModel: WaitViewModel by lazy {
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                return WaitViewModel(application) as T
//            }
//        }).get(WaitViewModel::class.java)
//    }
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
        super.initLayout()
        initDateRecyclerView()

    }

    private fun initDateRecyclerView() {
        remainRecyclerView.apply {
            adapter = WaitFreeRemainTimeAdapter(viewModel.items)
            (adapter as WaitFreeRemainTimeAdapter).setOnItemClickListener(object : WaitFreeRemainTimeAdapter.OnItemClickListener {
                override fun onItemClick(item: Any?, position: Int) {
                    if (item is DataBook) {
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