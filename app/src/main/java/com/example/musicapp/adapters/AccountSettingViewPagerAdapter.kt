package com.example.musicapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AccountSettingViewPagerAdapter(private val listFragment: List<Fragment>,
                                     fm: FragmentManager,
                                     lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
}