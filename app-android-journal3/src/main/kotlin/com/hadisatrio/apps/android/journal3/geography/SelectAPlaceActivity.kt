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

package com.hadisatrio.apps.android.journal3.geography

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.geography.SelectAPlaceUseCase
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.StringRecyclerViewPresenter
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.event.FilteringEventSink
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter

class SelectAPlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select_a_place)

        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = SelectAPlaceUseCase(
                places = journal3Application.places,
                presenter = AdaptingPresenter(
                    origin = ExecutorDispatchingPresenter(
                        executor = journal3Application.foregroundExecutor,
                        origin = StringRecyclerViewPresenter(findViewById(R.id.places_list))
                    ),
                    adapter = { places -> places.map { it.name } }
                ),
                eventSource = ExecutorDispatchingEventSource(
                    executor = journal3Application.foregroundExecutor,
                    origin = EventSources(
                        journal3Application.globalEventSource,
                        LifecycleTriggeredEventSource(
                            lifecycleOwner = this,
                            lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                            eventFactory = { CancellationEvent("system") }
                        ),
                        RecyclerViewItemSelectionEventSource(findViewById(R.id.places_list)),
                        BackButtonCancellationEventSource(this)
                    )
                ),
                eventSink = EventSinks(
                    ActivityCompletionEventSink(this),
                    FilteringEventSink(
                        predicate = { it is SelectionEvent && it.selectionKind == "place" },
                        origin = journal3Application.globalEventSource
                    ),
                    journal3Application.globalEventSink
                )
            )
        )()
    }
}
