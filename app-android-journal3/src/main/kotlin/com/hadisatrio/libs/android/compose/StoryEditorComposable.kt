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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.hadisatrio.libs.android.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class StoryEditorComposable : ComposableComponent<StoryEditorComposable.State>, EventSource {

    private val states by lazy { MutableStateFlow(State()) }
    private val events by lazy { MutableSharedFlow<Event>(extraBufferCapacity = 1) }

    override fun present(thing: State) {
        states.tryEmit(thing)
    }

    override fun events(): Flow<Event> {
        return events.asSharedFlow()
    }

    @Composable
    override fun invoke() {
        with(states.collectAsState().value) {
            Column {
                TextField(
                    value = this@with.titleText,
                    onValueChange = { new -> events.tryEmit(TextInputEvent("title", new)) }
                )
                TextField(
                    value = this@with.synopsisString,
                    onValueChange = { new -> events.tryEmit(TextInputEvent("synopsis", new)) }
                )
            }
        }
    }

    data class State(
        val titleText: String = "",
        val synopsisString: String = ""
    )
}
