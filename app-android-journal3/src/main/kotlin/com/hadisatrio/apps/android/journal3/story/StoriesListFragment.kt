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

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoriesUseCase
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.libs.android.dimensions.dp
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.RxEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

abstract class StoriesListFragment : Fragment() {

    protected val storiesListView: RecyclerView by lazy {
        requireView().findViewById(R.id.stories_list)
    }

    abstract val presenter: Presenter<Stories>

    abstract val eventSource: RxEventSource

    abstract val stories: Stories

    private val eventSink: EventSink by lazy {
        EventSinks(
            journal3Application.globalEventSink,
            ActivityCompletionEventSink(requireActivity())
        )
    }

    private val useCase: UseCase by lazy {
        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = ShowStoriesUseCase(
                stories = stories,
                presenter = presenter,
                eventSource = eventSource,
                eventSink = eventSink
            )
        )
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_stories, container, false)
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        useCase()
    }

    private fun setupViews() {
        storiesListView.addItemDecoration(
            SpacingItemDecoration(
                Spacing(
                    edges = Rect(16.dp, 16.dp, 16.dp, 16.dp),
                    horizontal = 16.dp,
                    vertical = 16.dp
                )
            )
        )
    }
}
