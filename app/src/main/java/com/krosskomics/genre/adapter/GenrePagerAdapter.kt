package com.krosskomics.genre.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.krosskomics.genre.fragment.GenreFragment
import com.krosskomics.library.fragment.GiftBoxFragment
import com.krosskomics.library.fragment.LibraryFragment
import com.krosskomics.notice.fragment.FAQFragment
import com.krosskomics.notice.fragment.NoticeFragment

class GenrePagerAdapter(
    fa: FragmentActivity,
    val size: Int,
    private val adapterType: Int
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when(adapterType) {
            0 -> GenreFragment()
            1 -> {
                if (position == 0) {
                    LibraryFragment()
                } else {
                    GiftBoxFragment()
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