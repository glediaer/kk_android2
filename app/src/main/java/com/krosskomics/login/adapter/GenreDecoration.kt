package com.krosskomics.login.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krosskomics.util.CommonUtil

class GenreDecoration(context: Context) : RecyclerView.ItemDecoration() {
    var topMargin = 0
    var centerMargin = 0
    init {
        topMargin = CommonUtil.dpToPx(context, 10)
        centerMargin = CommonUtil.dpToPx(context, 6)
    }
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        var position = parent.getChildAdapterPosition(view)
        var itemCount = state.itemCount

        // 상하 설정
        outRect.top = topMargin

        // 가운데 여백
        // spanIndex = 0 -> 왼쪽
        // spanIndex = 1 -> 가운데
        // spanIndex = 2 -> 오른쪽
        var lp = view.layoutParams as GridLayoutManager.LayoutParams
        var spanIndex = lp.spanIndex

        if (spanIndex == 0) {
            outRect.right = centerMargin
        } else if (spanIndex == 1) {
            outRect.left = centerMargin
            outRect.right = centerMargin
        } else {
            outRect.left = centerMargin
        }
    }
}