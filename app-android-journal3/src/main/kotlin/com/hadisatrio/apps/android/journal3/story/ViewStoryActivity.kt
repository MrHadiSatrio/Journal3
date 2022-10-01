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
import com.hadisatrio.apps.android.journal3.id.BundledTargetId
import com.hadisatrio.apps.android.journal3.moment.MomentsRecyclerViewPresenter
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoryUseCase
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoryPresenter
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.CoroutineDispatchingEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.TextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.CoroutineDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.CoroutineDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenters
import kotlinx.coroutines.Dispatchers

class ViewStoryActivity : AppCompatActivity() {

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_story)

        CoroutineDispatchingUseCase(
            coroutineScope = lifecycleScope,
            coroutineDispatcher = Dispatchers.Default,
            origin = ShowStoryUseCase(
                targetId = BundledTargetId(intent, "target_id"),
                stories = journal3Application.stories,
                presenter = CoroutineDispatchingPresenter(
                    coroutineScope = lifecycleScope,
                    coroutineDispatcher = Dispatchers.Main,
                    origin = CachingStoryPresenter(
                        origin = CoroutineDispatchingPresenter(
                            coroutineScope = lifecycleScope,
                            coroutineDispatcher = Dispatchers.Main,
                            origin = Presenters(
                                AdaptingPresenter(
                                    origin = TextViewStringPresenter(findViewById(R.id.title_label)),
                                    adapter = StoryStringAdapter("title")
                                ),
                                AdaptingPresenter(
                                    origin = TextViewStringPresenter(findViewById(R.id.synopsis_label)),
                                    adapter = StoryStringAdapter("synopsis")
                                ),
                                AdaptingPresenter(
                                    origin = MomentsRecyclerViewPresenter(findViewById(R.id.moments_list)),
                                    adapter = { thing -> thing.moments }
                                )
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
                            lifecycleEvent = Lifecycle.Event.ON_RESUME,
                            eventFactory = { RefreshRequestEvent("lifecycle") }
                        ),
                        ViewClickEventSource(
                            view = findViewById(R.id.add_button),
                            eventFactory = { SelectionEvent("action", "add") }
                        ),
                        ViewClickEventSource(
                            view = findViewById(R.id.edit_button),
                            eventFactory = { SelectionEvent("action", "edit") }
                        ),
                        RecyclerViewItemSelectionEventSource(
                            recyclerView = findViewById(R.id.moments_list)
                        )
                    )
                ),
                eventSink = journal3Application.globalEventSink,
                router = ActivityRouter(this)
            )
        )()
    }
}
