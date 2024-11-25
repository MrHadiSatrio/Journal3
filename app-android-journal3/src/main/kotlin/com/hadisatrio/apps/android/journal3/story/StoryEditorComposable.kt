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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.publish.PublishSubject
import com.hadisatrio.libs.android.compose.ComposableComponent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import kotlinx.coroutines.flow.MutableStateFlow

class StoryEditorComposable : ComposableComponent<StoryEditorComposable.State>, EventSource {

    private val states by lazy { MutableStateFlow(State()) }
    private val events by lazy { PublishSubject<Event>() }

    override fun present(thing: State) {
        states.tryEmit(thing)
    }

    override fun events(): Observable<Event> {
        return events
    }

    @Composable
    override fun invoke() {
        with(states.collectAsState().value) {
            Column {
                TextField(
                    value = this@with.titleText,
                    onValueChange = { new -> events.onNext(TextInputEvent("title", new)) }
                )
                TextField(
                    value = this@with.synopsisString,
                    onValueChange = { new -> events.onNext(TextInputEvent("synopsis", new)) }
                )
            }
        }
    }

    data class State(
        val titleText: String = "",
        val synopsisString: String = ""
    )
}
