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

package com.hadisatrio.libs.kotlin.collection

class PagingIterator<T>(
    private val remote: Source<T>,
    private val pageSize: Int = 20
) : Iterator<T> {

    private var currentPage = 0
    private var currentItems: List<T> = emptyList()
    private var currentItemIndex = 0
    private var hasMoreData = true

    override fun hasNext(): Boolean {
        return when {
            currentItemIndex < currentItems.size -> true
            !hasMoreData -> false
            else -> loadNextPage()
        }
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException("No more items available.")
        return currentItems[currentItemIndex++]
    }

    private fun loadNextPage(): Boolean {
        val nextPage = remote.obtain(currentPage, pageSize)

        currentItems = nextPage.items
        currentItemIndex = 0
        currentPage++
        hasMoreData = nextPage.hasNext

        return currentItems.isNotEmpty()
    }

    fun interface Source<T> {
        fun obtain(page: Int, pageSize: Int): Page<T>
    }

    interface Page<T> {
        val items: List<T>
        val hasNext: Boolean
    }
}
