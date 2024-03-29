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

package com.hadisatrio.apps.android.journal3.moment

import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.libs.android.foundation.widget.recyclerview.ItemDiffer

object MomentItemDiffer : ItemDiffer<Moment> {

    override fun areItemsTheSame(oldItem: Moment, newItem: Moment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Moment, newItem: Moment): Boolean {
        return oldItem.timestamp == newItem.timestamp &&
            oldItem.description == newItem.description &&
            oldItem.sentiment == newItem.sentiment &&
            oldItem.place == newItem.place &&
            oldItem.attachments.toSet() == newItem.attachments.toSet()
    }
}
