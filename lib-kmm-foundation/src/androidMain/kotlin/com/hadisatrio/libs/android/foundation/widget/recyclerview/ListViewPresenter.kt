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

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

class ListViewPresenter<T : Any>(
    private val recyclerView: RecyclerView,
    private val orientation: Int = RecyclerView.VERTICAL,
    private val viewFactory: ViewFactory = NaiveViewFactory,
    private val viewRenderer: ViewRenderer<T> = NaiveViewRenderer(),
    private val differ: ItemDiffer<T> = NaiveItemDiffer(),
    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor(),
) : Presenter<Iterable<T>> {

    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(
            recyclerView.context,
            orientation,
            false
        )
    }
    private val adapter: Adapter<T> by lazy { Adapter(viewFactory, viewRenderer, differ, backgroundExecutor) }

    private var items = mutableListOf<T>()
    private var iterator = emptyList<T>().iterator()

    private val isSetupRequired = AtomicBoolean(true)
    private val isLoading = AtomicBoolean(false)

    override fun present(thing: Iterable<T>) {
        if (isSetupRequired.getAndSet(false)) setupRecyclerView()
        backgroundExecutor.execute {
            items.clear()
            iterator = thing.iterator()
            loadItems(max(layoutManager.findLastVisibleItemPosition(), DEFAULT_PAGE_SIZE))
        }
    }

    private fun setupRecyclerView() = with(recyclerView) {
        adapter = this@ListViewPresenter.adapter
        layoutManager = this@ListViewPresenter.layoutManager
        addOnScrollListener(ScrollListener())
    }

    private fun loadItems(count: Int) {
        if (isLoading.getAndSet(true)) return
        val targetSize = items.size + count
        while (iterator.hasNext() && items.size < targetSize) items.add(iterator.next())
        recyclerView.post { adapter.submitList(items.toList()) }
        isLoading.set(false)
    }

    private inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            backgroundExecutor.execute {
                val totalItem: Int = this@ListViewPresenter.layoutManager.getItemCount()
                val lastVisibleItem: Int = this@ListViewPresenter.layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem == totalItem - 1) loadItems(DEFAULT_PAGE_SIZE)
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
