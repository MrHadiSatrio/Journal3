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
import androidx.lifecycle.lifecycleScope
import com.hadisatrio.apps.android.journal3.Journal3.Companion.journal3Application
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.kotlin.journal3.geography.SelectAPlaceUseCase
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.StringRecyclerViewPresenter
import com.hadisatrio.libs.kotlin.foundation.CoroutineDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.FilteringEventSink
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.CoroutineDispatchingPresenter
import kotlinx.coroutines.Dispatchers

class SelectAPlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select_a_place)

        CoroutineDispatchingUseCase(
            coroutineScope = lifecycleScope,
            coroutineDispatcher = Dispatchers.Default,
            origin = SelectAPlaceUseCase(
                places = journal3Application.places,
                presenter = AdaptingPresenter(
                    origin = CoroutineDispatchingPresenter(
                        coroutineScope = lifecycleScope,
                        coroutineDispatcher = Dispatchers.Main,
                        origin = StringRecyclerViewPresenter(findViewById(R.id.places_list))
                    ),
                    adapter = { places -> places.map { it.name } }
                ),
                eventSource = EventSources(
                    journal3Application.globalEventSource,
                    RecyclerViewItemSelectionEventSource(findViewById(R.id.places_list)),
                    BackButtonCancellationEventSource(this)
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
