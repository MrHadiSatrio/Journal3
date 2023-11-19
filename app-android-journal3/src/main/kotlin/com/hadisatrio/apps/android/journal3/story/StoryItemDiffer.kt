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

package com.hadisatrio.apps.android.journal3.story

import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.libs.android.foundation.widget.recyclerview.ItemDiffer

object StoryItemDiffer : ItemDiffer<Story> {

    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.title == newItem.title &&
            oldItem.synopsis == newItem.synopsis &&
            areMomentsTheSame(oldItem, newItem)
    }

    private fun areMomentsTheSame(oldItem: Story, newItem: Story): Boolean {
        val oldIds = oldItem.moments.mapTo(HashSet()) { it.id }
        val newIds = newItem.moments.mapTo(HashSet()) { it.id }
        return oldIds == newIds
    }
}
