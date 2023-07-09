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
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.StreamEventsUseCase

class ViewStoriesActivity : AppCompatActivity() {

    private val useCase: UseCase by lazy {
        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = StreamEventsUseCase(
                eventSource = ExecutorDispatchingEventSource(
                    executor = journal3Application.foregroundExecutor,
                    origin = EventSources(
                        LifecycleTriggeredEventSource(
                            lifecycleOwner = this,
                            lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                            eventFactory = { CancellationEvent("system") }
                        ),
                        ViewClickEventSource(
                            view = findViewById(R.id.add_button),
                            eventFactory = { SelectionEvent("action", "add_story") }
                        )
                    )
                ),
                eventSink = journal3Application.globalEventSink
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        useCase()
    }

    private fun setupViews() {
        setContentView(R.layout.activity_view_stories)
        setSupportActionBar(findViewById(R.id.bottom_bar))
    }
}
