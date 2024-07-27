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

package com.hadisatrio.libs.kotlin.foundation

import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEvent
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EventHandlingUseCaseTest {

    @Test
    fun `Handles events from event source`() {
        val testEvent = FakeEvent()
        val eventSource = RecordedEventSource(testEvent, CompletionEvent())
        val eventSink = FakeEventSink()
        val testUseCase = TestEventHandlingUseCase(eventSource, eventSink)

        testUseCase.invoke()

        eventSink.hasSunk { it == testEvent }
        testUseCase.handledEvents.shouldContain(testEvent)
    }

    @Test
    fun `Completes when completion event is emitted`() {
        val testEvent = FakeEvent()
        val eventSource = RecordedEventSource(testEvent, CompletionEvent())
        val eventSink = FakeEventSink()
        val testUseCase = TestEventHandlingUseCase(eventSource, eventSink)

        testUseCase.invoke()

        eventSink.hasSunk { it is CompletionEvent }
        testUseCase.isCompleted shouldBe true
    }

    private class TestEventHandlingUseCase(
        eventSource: EventSource,
        eventSink: EventSink
    ) : EventHandlingUseCase(eventSource, eventSink) {

        val handledEvents = mutableListOf<Event>()
        var isCompleted = false

        override fun invokeInternal() {
            // No-op for test
        }

        override fun handleEvent(event: Event) {
            handledEvents.add(event)
        }

        override fun onComplete() {
            isCompleted = true
        }
    }
}
