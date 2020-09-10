package com.krosskomics.genre.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.krosskomics.genre.fragment.GenreFragment

class GenrePagerAdapter(fa: FragmentActivity, val size: Int) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> GenreFragment()
            1 -> GenreFragment()
            else -> GenreFragment()
        }
    }
}