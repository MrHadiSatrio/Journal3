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

import android.view.LayoutInflater
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoriesPresenter
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewPresenter
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class UserStoriesListFragment : StoriesListFragment() {

    override val stories: Stories by lazy {
        journal3Application.stories
    }

    override val presenter: Presenter<Stories> by lazy {
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
                            },
                            differ = StoryItemDiffer
                        ),
                        adapter = { stories -> stories.toList() }
                    )
                )
            )
        )
    }

    override val eventSource: EventSource by lazy {
        SchedulingEventSource(
            subscriptionScheduler = mainScheduler,
            observationScheduler = computationScheduler,
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
}
