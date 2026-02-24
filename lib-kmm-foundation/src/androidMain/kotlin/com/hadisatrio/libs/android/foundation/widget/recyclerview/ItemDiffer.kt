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

package com.hadisatrio.libs.android.foundation.widget.recyclerview

/**
 * A comparator that determines whether two items represent the same entity and whether their visible contents differ.
 *
 * Used by [RecyclerViewPresenter] and [ListViewPresenter] for efficient diff computation.
 */
interface ItemDiffer<T> {
    /**
     * Returns `true` if [oldItem] and [newItem] represent the same logical entity
     * (typically same ID), regardless of content.
     *
     * @param oldItem The item from the previous list.
     * @param newItem The item from the new list.
     */
    fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    /**
     * Returns `true` if [oldItem] and [newItem] have identical visible contents.
     * Only called when [areItemsTheSame] returns `true`.
     *
     * @param oldItem The item from the previous list.
     * @param newItem The item from the new list.
     */
    fun areContentsTheSame(oldItem: T, newItem: T): Boolean
}
