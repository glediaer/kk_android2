package com.krosskomics.common.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.krosskomics.coin.fragment.CashHistoryFragment
import com.krosskomics.genre.fragment.GenreDetailFragment
import com.krosskomics.library.fragment.GiftBoxFragment
import com.krosskomics.library.fragment.LibraryFragment
import com.krosskomics.notice.fragment.FAQFragment
import com.krosskomics.notice.fragment.NoticeFragment

class CommonPagerAdapter(
    fa: FragmentActivity,
    val size: Int,
    private val adapterType: Int
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when(adapterType) {
            0 -> GenreDetailFragment().apply {
                val bundle = Bundle()
                bundle.putString("listType", "genre")
                bundle.putInt("position", position)
                arguments = bundle
            }
            1 -> {
                if (position == 0) {
                    LibraryFragment()
                } else {
                    GiftBoxFragment()
                }
            }
            2 -> {
                CashHistoryFragment().apply {
                    val bundle = Bundle()
                    if (position == 0) {
                        bundle.putString("listType", "charge")
                    } else {
                        bundle.putString("listType", "use")
                    }
                    arguments = bundle
                }
            }
            else -> {
                if (position == 0) {
                    NoticeFragment()
                } else {
                    FAQFragment()
                }
            }
        }
    }
}