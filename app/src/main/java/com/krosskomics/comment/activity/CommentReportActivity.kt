package com.krosskomics.comment.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.comment.viewmodel.CommentViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.adapter.RecyclerViewBaseAdapter
import com.krosskomics.common.data.DataReport
import kotlinx.android.synthetic.main.activity_comment_report.*
import kotlinx.android.synthetic.main.activity_comment_report.recyclerView
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.toolbar

class CommentReportActivity : ToolbarTitleActivity() {
    private val TAG = "CommentReportActivity"

    public override val viewModel: CommentViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CommentViewModel(application) as T
            }
        }).get(CommentViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_comment_report
        return R.layout.activity_comment_report
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_report))
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbarTitle.text = toolbarTitleString
    }

    override fun requestServer() {}

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_report)
        super.initLayout()

        cancelButton.setOnClickListener { finish() }
        doneButton.setOnClickListener {
            if (viewModel.reportType.isNullOrEmpty()) return@setOnClickListener
            sendReportApi()
        }
    }

    private fun sendReportApi() {
        // 신고 api
        viewModel.type = "report"
        viewModel.reportType = "report"
        viewModel.reportContent = otherEditTextView.text.toString()

        requestServer()
    }

    override fun initModel() {
        intent.extras?.apply {
            viewModel.sid = getString("sid").toString()
            viewModel.seq = getString("seq").toString()
        }
        super.initModel()
        viewModel.items = arrayListOf(
            DataReport(getString(R.string.str_report1), false),
            DataReport(getString(R.string.str_report2), false),
            DataReport(getString(R.string.str_report3), false),
            DataReport(getString(R.string.str_report4), false),
            DataReport(getString(R.string.str_report5), false)
        )
    }

    fun checkVisibleReportEditText(isVisible: Boolean) {
        if (isVisible) otherEditTextView.visibility = View.VISIBLE
        else otherEditTextView.visibility = View.GONE
    }

    fun resetReportSelect(position: Int) {
        viewModel.items.forEachIndexed { index, item ->
            if (item is DataReport) {
                item.isSelect = index == position
                if (index == position) {
                    viewModel.reportType = item.title
                }
            }
        }
        (recyclerView?.adapter as RecyclerViewBaseAdapter).notifyDataSetChanged()
        (context as CommentReportActivity).checkVisibleReportEditText(position == viewModel.items.size - 1)
    }
}