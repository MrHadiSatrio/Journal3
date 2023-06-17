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

package com.hadisatrio.libs.android.foundation.widget

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlin.math.roundToInt

class RecyclerViewPresenter<T>(
    private val recyclerView: RecyclerView,
    private val layoutManager: RecyclerView.LayoutManager,
    private val viewFactory: ViewFactory,
    private val viewRenderer: ViewRenderer<T>,
    private val differ: ItemDiffer<T>
) : Presenter<Iterable<T>> {

    private val adapter: Adapter<T> by lazy { Adapter(viewFactory, viewRenderer, differ) }

    constructor(recyclerView: RecyclerView) : this(
        recyclerView,
        LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false),
        ViewFactory { parent, _ ->
            TextView(parent.context).apply {
                val density = recyclerView.resources.displayMetrics.density
                val itemPadding = (density * DEFAULT_ITEM_PADDING).roundToInt()
                layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                    setMargins(itemPadding, itemPadding, itemPadding, itemPadding)
                }
            }
        },
        ViewRenderer { view, item ->
            (view as TextView).text = item.toString()
        },
        object : ItemDiffer<T> {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
        }
    )

    constructor(
        recyclerView: RecyclerView,
        viewFactory: ViewFactory,
        viewRenderer: ViewRenderer<T>
    ) : this(
        recyclerView,
        LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false),
        viewFactory,
        viewRenderer,
        NaiveItemDiffer()
    )

    constructor(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager,
        viewFactory: ViewFactory,
        viewRenderer: ViewRenderer<T>
    ) : this(recyclerView, layoutManager, viewFactory, viewRenderer, NaiveItemDiffer())

    override fun present(thing: Iterable<T>) {
        setupRecyclerView()
        adapter.submitList(thing.toList())
    }

    private fun setupRecyclerView() = with(recyclerView) {
        adapter = this@RecyclerViewPresenter.adapter
        layoutManager = this@RecyclerViewPresenter.layoutManager
    }

    fun interface ViewFactory {
        fun create(parent: ViewGroup, viewType: Int): View
    }

    fun interface ViewRenderer<T> {
        fun render(view: View, item: T)
    }

    interface ItemDiffer<T> {
        fun areItemsTheSame(oldItem: T, newItem: T): Boolean
        fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    }

    private class NaiveItemDiffer<T> : ItemDiffer<T> {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }

    private class Adapter<T>(
        private val viewFactory: ViewFactory,
        private val viewRenderer: ViewRenderer<T>,
        private val differ: ItemDiffer<T>
    ) : ListAdapter<T, ViewHolder>(DiffCallback(differ)) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(viewFactory.create(parent, viewType))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            viewRenderer.render(holder.view, getItem(position))
        }
    }

    private class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private class DiffCallback<T>(private val differ: ItemDiffer<T>) : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return differ.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return differ.areContentsTheSame(oldItem, newItem)
        }
    }

    companion object {

        private const val DEFAULT_ITEM_PADDING = 16
    }
}
