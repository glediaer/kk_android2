package com.krosskomics.util

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreCachingLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private val DEFAULT_EXTRA_LAYOUT_SPACE = 600
    private var extraLayoutSpace = -1

    fun setExtraLayoutSpace(extraLayoutSpace: Int) {
        this.extraLayoutSpace = extraLayoutSpace
    }

    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return if (extraLayoutSpace > 0) {
            extraLayoutSpace
        } else DEFAULT_EXTRA_LAYOUT_SPACE
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("jj", "RecyclerView exception")
        }
    }
}