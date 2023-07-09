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

package com.hadisatrio.libs.kotlin.foundation.event

import com.hadisatrio.libs.kotlin.foundation.UseCase
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

class StreamEventsUseCase(
    private val eventSource: EventSource,
    private val eventSink: EventSink
) : UseCase {

    override fun invoke() = runBlocking {
        eventSource.events()
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CancellationEvent) == null }
            .takeWhile { event -> (event as? CompletionEvent) == null }
            .collect {}
    }
}
