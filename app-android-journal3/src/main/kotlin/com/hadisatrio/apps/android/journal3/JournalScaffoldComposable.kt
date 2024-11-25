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

package com.hadisatrio.apps.android.journal3

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.publish.PublishSubject
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource

class JournalScaffoldComposable(
    private val fabEventFactory: () -> Event,
    private val content: @Composable () -> Unit
) : EventSource {

    private val events by lazy { PublishSubject<Event>() }

    override fun events(): Observable<Event> {
        return events
    }

    @Composable
    operator fun invoke() {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    actions = {
                        Icon(Icons.Default.Favorite, contentDescription = "")
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { events.onNext(fabEventFactory()) }) {
                            Icon(Icons.Default.Add, contentDescription = "")
                        }
                    }
                )
            },
            content = { padding ->
                Surface(modifier = Modifier.padding(padding)) {
                    content()
                }
            }
        )
    }
}
