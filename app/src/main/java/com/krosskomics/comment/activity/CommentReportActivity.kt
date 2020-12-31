package com.krosskomics.comment.activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.comment.viewmodel.CommentViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.common.data.DataReport
import kotlinx.android.synthetic.main.activity_comment_report.*
import kotlinx.android.synthetic.main.view_toolbar_black.*

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
        doneButton.setOnClickListener { sendReportApi() }
    }

    private fun sendReportApi() {
        // 신고 api
    }

    override fun initModel() {
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
}