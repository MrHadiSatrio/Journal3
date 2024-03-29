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

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal class Adapter<T : Any>(
    private val viewFactory: ViewFactory,
    private val viewRenderer: ViewRenderer<T>,
    private val config: AsyncDifferConfig<T>
) : ListAdapter<T, ViewHolder>(config) {

    constructor(
        viewFactory: ViewFactory,
        viewRenderer: ViewRenderer<T>,
        differ: ItemDiffer<T>,
        backgroundExecutor: Executor = Executors.newFixedThreadPool(2)
    ) : this(
        viewFactory,
        viewRenderer,
        AsyncDifferConfig.Builder(DiffCallback(differ))
            .setBackgroundThreadExecutor(backgroundExecutor)
            .build()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewFactory.create(parent, viewType))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewRenderer.render(holder.view, getItem(position))
    }
}
