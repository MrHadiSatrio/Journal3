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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.hadisatrio.apps.android.journal3.ActivityRouter
import com.hadisatrio.apps.android.journal3.Journal3.Companion.journal3Application
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoriesUseCase
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoriesPresenter
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.CoroutineDispatchingEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.StringRecyclerViewPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.CoroutineDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.CoroutineDispatchingPresenter
import kotlinx.coroutines.Dispatchers

class ViewStoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_stories)

        CoroutineDispatchingUseCase(
            coroutineScope = lifecycleScope,
            coroutineDispatcher = Dispatchers.Default,
            origin = ShowStoriesUseCase(
                stories = journal3Application.stories,
                presenter = CoroutineDispatchingPresenter(
                    coroutineScope = lifecycleScope,
                    coroutineDispatcher = Dispatchers.Default,
                    origin = CachingStoriesPresenter(
                        origin = CoroutineDispatchingPresenter(
                            coroutineScope = lifecycleScope,
                            coroutineDispatcher = Dispatchers.Main,
                            origin = AdaptingPresenter(
                                origin = StringRecyclerViewPresenter(recyclerView = findViewById(R.id.stories_list)),
                                adapter = { stories -> stories.map { "${it.title}\n${it.synopsis}" } }
                            )
                        )
                    )
                ),
                eventSource = CoroutineDispatchingEventSource(
                    coroutineDispatcher = Dispatchers.Main,
                    origin = EventSources(
                        journal3Application.globalEventSource,
                        LifecycleTriggeredEventSource(
                            lifecycleOwner = this,
                            lifecycleEvent = Lifecycle.Event.ON_START,
                            eventFactory = { RefreshRequestEvent("lifecycle") }
                        ),
                        ViewClickEventSource(
                            view = findViewById(R.id.add_button),
                            eventFactory = { SelectionEvent("action", "add") }
                        ),
                        RecyclerViewItemSelectionEventSource(
                            recyclerView = findViewById(R.id.stories_list)
                        )
                    )
                ),
                eventSink = journal3Application.globalEventSink,
                router = ActivityRouter(this)
            )
        )()
    }
}
