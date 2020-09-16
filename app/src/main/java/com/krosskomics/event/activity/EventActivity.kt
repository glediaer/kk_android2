package com.krosskomics.event.activity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import com.krosskomics.R
import com.krosskomics.series.activity.SeriesActivity
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataBook
import com.krosskomics.event.adapter.EventAdapter
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_main_content.recyclerView

class EventActivity : ToolbarTitleActivity() {
    private val TAG = "EventActivity"

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_event
        return R.layout.activity_event
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_event))
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_event)
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
        }
    }

    override fun initRecyclerViewAdapter() {
        recyclerView.adapter = EventAdapter(viewModel.items, recyclerViewItemLayoutId)
        (recyclerView.adapter as RecyclerViewBaseAdapter).setOnItemClickListener(object : RecyclerViewBaseAdapter.OnItemClickListener {
            override fun onItemClick(item: Any?) {
                if (item is DataBook) {
                    val intent = Intent(context, SeriesActivity::class.java).apply {
                        putExtra("sid", item.sid)
                        putExtra("title", item.title)
                    }
                    startActivity(intent)
                }
            }
        })
    }
}