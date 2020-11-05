package com.krosskomics.waitfree.activity

import android.widget.TextView
import com.krosskomics.R
import com.krosskomics.common.activity.RecyclerViewBaseActivity
import kotlinx.android.synthetic.main.activity_waitfree.*

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
        initDateView()
    }

    private fun initDateView() {
        dateViewItems = arrayListOf(oneDateTextView, twoDateTextView, threeDateTextView,
            fourDateTextView, fiveDateTextView)
        dateViewItems.forEach { dateView ->
            dateView.setOnClickListener {
                resetDateViewItems()
                it.isSelected = true
                requestServer()
            }
        }
    }

    private fun resetDateViewItems() {
        dateViewItems.forEach { it.isSelected = false }
    }
}