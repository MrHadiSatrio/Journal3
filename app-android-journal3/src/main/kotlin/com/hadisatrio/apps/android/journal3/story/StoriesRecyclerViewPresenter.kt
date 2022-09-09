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

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class StoriesRecyclerViewPresenter(
    private val recyclerView: RecyclerView
) : Presenter<Stories> {

    private val adapter: Adapter by lazy {
        Adapter()
    }
    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)
    }

    override fun present(thing: Stories) {
        setupRecyclerView()
        adapter.submitList(thing.map { "${it.title}\n${it.synopsis}" })
    }

    private fun setupRecyclerView() = with(recyclerView) {
        adapter = this@StoriesRecyclerViewPresenter.adapter
        layoutManager = this@StoriesRecyclerViewPresenter.layoutManager
        if (itemDecorationCount == 0) {
            addItemDecoration(
                MaterialDividerItemDecoration(
                    recyclerView.context,
                    RecyclerView.VERTICAL
                )
            )
        }
    }

    private class Adapter : ListAdapter<String, ViewHolder>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            view.setPadding(parent.resources.getDimensionPixelSize(R.dimen.margin))
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.view.text = getItem(position)
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
