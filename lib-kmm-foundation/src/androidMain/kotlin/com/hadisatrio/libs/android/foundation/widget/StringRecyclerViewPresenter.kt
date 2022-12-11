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

import android.content.res.Resources
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

class StringRecyclerViewPresenter(
    private val recyclerView: RecyclerView,
    private val layoutManager: RecyclerView.LayoutManager
) : Presenter<Iterable<String>> {

    private val adapter: Adapter by lazy {
        Adapter(recyclerView.resources)
    }

    constructor(recyclerView: RecyclerView) : this(
        recyclerView,
        LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)
    )

    override fun present(thing: Iterable<String>) {
        setupRecyclerView()
        adapter.submitList(thing.toList())
    }

    private fun setupRecyclerView() = with(recyclerView) {
        adapter = this@StringRecyclerViewPresenter.adapter
        layoutManager = this@StringRecyclerViewPresenter.layoutManager
    }

    private class Adapter(resources: Resources) : ListAdapter<String, ViewHolder>(DiffCallback()) {

        private val itemPadding: Int by lazy { (resources.displayMetrics.density * ITEM_PADDING_DP).roundToInt() }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            view.setPadding(itemPadding, itemPadding, itemPadding, itemPadding)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.view.text = getItem(position)
        }

        companion object {
            private const val ITEM_PADDING_DP = 16
        }
    }

    private class ViewHolder constructor(val view: TextView) :
        RecyclerView.ViewHolder(view)

    private class DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
