/*
 * Copyright (C) 2022 Hadi Satrio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hadisatrio.libs.android.viewpager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SimpleFragmentPagerAdapter(
    activity: FragmentActivity,
    private val factories: List<FragmentFactory>,
) : FragmentStateAdapter(activity) {

    constructor(activity: FragmentActivity, vararg factories: FragmentFactory) : this(activity, factories.toList())

    override fun getItemCount(): Int {
        return factories.size
    }

    override fun createFragment(position: Int): Fragment {
        return factories[position].create()
    }

    fun interface FragmentFactory {
        fun create(): Fragment
    }
}
