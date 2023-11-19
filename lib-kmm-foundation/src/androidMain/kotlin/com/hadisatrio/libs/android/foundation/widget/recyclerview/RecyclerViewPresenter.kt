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
import java.util.concurrent.atomic.AtomicBoolean

class RecyclerViewPresenter<T : Any>(
    private val recyclerView: RecyclerView,
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(recyclerView.context),
    private val viewFactory: ViewFactory = NaiveViewFactory,
    private val viewRenderer: ViewRenderer<T> = NaiveViewRenderer(),
    private val differ: ItemDiffer<T> = NaiveItemDiffer()
) : Presenter<Iterable<T>> {

    private val adapter: Adapter<T> by lazy { Adapter(viewFactory, viewRenderer, differ) }
    private val isSetupRequired: AtomicBoolean = AtomicBoolean(true)

    override fun present(thing: Iterable<T>) {
        if (isSetupRequired.getAndSet(false)) setupRecyclerView()
        adapter.submitList(thing.toList())
    }

    private fun setupRecyclerView() = with(recyclerView) {
        adapter = this@RecyclerViewPresenter.adapter
        layoutManager = this@RecyclerViewPresenter.layoutManager
    }

    companion object {

        const val DEFAULT_ITEM_PADDING = 16
    }
}
