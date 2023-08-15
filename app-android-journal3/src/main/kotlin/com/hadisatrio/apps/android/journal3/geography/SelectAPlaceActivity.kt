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

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.geography.SelectAPlaceUseCase
import com.hadisatrio.libs.android.dimensions.dp
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.activity.ActivityResultSettingEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Places

class SelectAPlaceActivity : AppCompatActivity() {

    private val presenter: Presenter<Places> by lazy {
        val viewFactory = RecyclerViewPresenter.ViewFactory { parent, _ ->
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_place_snippet_card, parent, false)
        }
        val viewRenderer = RecyclerViewPresenter.ViewRenderer<Place> { view, item ->
            view.findViewById<TextView>(R.id.name_label).text = item.name
        }

        ExecutorDispatchingPresenter(
            executor = journal3Application.backgroundExecutor,
            origin = AdaptingPresenter(
                adapter = { it.toList() },
                origin = ExecutorDispatchingPresenter(
                    executor = journal3Application.foregroundExecutor,
                    origin = RecyclerViewPresenter(
                        recyclerView = findViewById(R.id.places_list),
                        viewFactory = viewFactory,
                        viewRenderer = viewRenderer,
                        differ = PlaceItemDiffer
                    ),
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
                    lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                    eventFactory = { CancellationEvent("system") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.back_button),
                    eventFactory = { CancellationEvent("user") }
                ),
                RecyclerViewItemSelectionEventSource(findViewById(R.id.places_list)),
                BackButtonCancellationEventSource(this)
            )
        )
    }

    private val eventSink: EventSink by lazy {
        EventSinks(
            journal3Application.globalEventSink,
            ActivityResultSettingEventSink(
                activity = this,
                adapter = { event ->
                    val values = mutableMapOf<String, Any>()
                    if (event is SelectionEvent && event.selectionKind == "place") {
                        values["place"] = event.selectedIdentifier
                    }
                    values
                }
            ),
            ActivityCompletionEventSink(this)
        )
    }

    private val useCase: UseCase by lazy {
        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = SelectAPlaceUseCase(
                places = journal3Application.places,
                presenter = presenter,
                eventSource = eventSource,
                eventSink = eventSink
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        useCase()
    }

    private fun setupViews() {
        setContentView(R.layout.activity_select_a_place)
        setSupportActionBar(findViewById(R.id.bottom_bar))
        findViewById<RecyclerView>(R.id.places_list).addItemDecoration(
            SpacingItemDecoration(
                Spacing(
                    edges = Rect(16.dp, 16.dp, 16.dp, 16.dp),
                    vertical = 8.dp
                )
            )
        )
    }
}
