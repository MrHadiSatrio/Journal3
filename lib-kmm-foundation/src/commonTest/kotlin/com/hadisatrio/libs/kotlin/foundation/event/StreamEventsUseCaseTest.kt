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

import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEvent
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import kotlin.test.Test

class StreamEventsUseCaseTest {

    private val sink = FakeEventSink()

    @Test
    fun `Streams events coming from the source to the sink until completion`() {
        val events = listOf(
            FakeEvent("foo" to "bar"),
            FakeEvent("baz" to "qux"),
            CompletionEvent()
        )
        val source = AdaptedRxEventSource(RecordedEventSource(events))

        StreamEventsUseCase(source, sink)()

        events.forEach { event -> sink.hasSunk { it == event } }
    }

    @Test
    fun `Streams events coming from the source to the sink until cancellation`() {
        val events = listOf(
            FakeEvent("foo" to "bar"),
            FakeEvent("baz" to "qux"),
            CancellationEvent("system")
        )
        val source = AdaptedRxEventSource(RecordedEventSource(events))

        StreamEventsUseCase(source, sink)()

        events.forEach { event -> sink.hasSunk { it == event } }
    }
}
