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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoriesUseCase
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoriesPresenter
import com.hadisatrio.libs.android.dimensions.dp
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewPresenter
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class StoriesListFragment : Fragment() {

    private val storiesListView: RecyclerView by lazy {
        requireView().findViewById(R.id.stories_list)
    }

    private val presenter: Presenter<Stories> by lazy {
        ExecutorDispatchingPresenter(
            executor = journal3Application.backgroundExecutor,
            origin = CachingStoriesPresenter(
                origin = ExecutorDispatchingPresenter(
                    executor = journal3Application.foregroundExecutor,
                    origin = AdaptingPresenter(
                        origin = RecyclerViewPresenter(
                            recyclerView = storiesListView,
                            viewFactory = { parent, _ ->
                                LayoutInflater.from(parent.context)
                                    .inflate(R.layout.view_story_snippet_card, parent, false)
                            },
                            viewRenderer = { view, item ->
                                view.findViewById<TextView>(R.id.title_label).text = item.title
                                view.findViewById<TextView>(R.id.synopsis_label).text = item.synopsis.toString()
                            }
                        ),
                        adapter = { stories -> stories.toList() }
                    )
                )
            )
        )
    }

    private val eventSource: EventSource by lazy {
        ExecutorDispatchingEventSource(
            executor = journal3Application.foregroundExecutor,
            origin = EventSources(
                journal3Application.globalEventSource,
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_START,
                    eventFactory = { RefreshRequestEvent("lifecycle") }
                ),
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                    eventFactory = { CancellationEvent("system") }
                ),
                RecyclerViewItemSelectionEventSource(
                    recyclerView = storiesListView
                )
            )
        )
    }

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
                stories = journal3Application.stories,
                presenter = presenter,
                eventSource = eventSource,
                eventSink = eventSink
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        useCase()
    }

    private fun setupViews() {
        storiesListView.addItemDecoration(
            SpacingItemDecoration(
                Spacing(
                    edges = Rect(16.dp, 16.dp, 16.dp, 16.dp),
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        )
    }
}
