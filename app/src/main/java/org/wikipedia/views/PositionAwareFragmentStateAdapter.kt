package org.wikipedia.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class PositionAwareFragmentStateAdapter: FragmentStateAdapter {
    private val fragmentManager: FragmentManager
    constructor(fragment: Fragment): super(fragment) { fragmentManager = fragment.childFragmentManager }
    constructor(activity: FragmentActivity): super(activity) { fragmentManager = activity.supportFragmentManager }

    fun getFragmentAt(position: Int): Fragment? {
        return fragmentManager.findFragmentByTag("f$position")
    }
}
