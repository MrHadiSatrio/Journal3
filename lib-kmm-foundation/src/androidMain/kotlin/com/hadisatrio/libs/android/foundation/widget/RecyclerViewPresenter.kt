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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

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

    private object NaiveViewFactory : ViewFactory {

        override fun create(parent: ViewGroup, viewType: Int): View {
            return TextView(parent.context).apply {
                val density = parent.resources.displayMetrics.density
                val itemPadding = (density * DEFAULT_ITEM_PADDING).roundToInt()
                layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                    setMargins(itemPadding, itemPadding, itemPadding, itemPadding)
                }
            }
        }
    }

    private class NaiveViewRenderer<T> : ViewRenderer<T> {

        override fun render(view: View, item: T) {
            (view as TextView).text = item.toString()
        }
    }

    private class NaiveItemDiffer<T> : ItemDiffer<T> {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }

    private class Adapter<T : Any>(
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

    private class DiffCallback<T : Any>(private val differ: ItemDiffer<T>) : DiffUtil.ItemCallback<T>() {

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
