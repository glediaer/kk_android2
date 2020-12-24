package com.krosskomics.comment.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krosskomics.R
import com.krosskomics.comment.viewmodel.CommentViewModel
import com.krosskomics.common.activity.ToolbarTitleActivity
import com.krosskomics.mynews.viewmodel.MyNewsViewModel
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.view_toolbar_black.*
import kotlinx.android.synthetic.main.view_toolbar_black.view.*

class CommentActivity : ToolbarTitleActivity() {
    private val TAG = "CommentActivity"

    public override val viewModel: CommentViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CommentViewModel(application) as T
            }
        }).get(CommentViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        recyclerViewItemLayoutId = R.layout.item_comment
        return R.layout.activity_comment
    }

    override fun initTracker() {
        setTracker(getString(R.string.str_comments))
    }

    override fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.kk_icon_back_white)
        }
        toolbar.toolbarTrash.visibility = View.GONE
    }

    override fun initLayout() {
        toolbarTitleString = getString(R.string.str_comments)
        super.initLayout()
        initHeaderView()
        initFooterView()
    }

    private fun initFooterView() {
        sendImageView.isEnabled = false
        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                sendImageView.isEnabled = s.isNotEmpty()
            }
        })
        sendImageView.setOnClickListener {
            // 댓글 등록
        }
    }

    fun initHeaderView() {
        commentsCount.text = "(${viewModel.items.size})"
        sortView.setOnClickListener {
            // 정렬 리퀘스트
        }
    }
}