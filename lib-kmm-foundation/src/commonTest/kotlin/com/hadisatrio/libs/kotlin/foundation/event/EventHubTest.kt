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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.hadisatrio.libs.kotlin.foundation.event

import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration

class EventHubTest {

    @Test
    fun `Forwards sunk events to the collector`() = runTest {
        val eventsToBeSunk = arrayOf(
            PerfSensitiveEvent(Duration.ZERO, TextInputEvent("Foo", "Bar")),
            SelectionEvent("Foo", "Bar"),
            CancellationEvent("system")
        )

        val hub = EventHub(MutableSharedFlow(extraBufferCapacity = 1))
        val eventsPosted = mutableListOf<Event>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            hub.events().toList(eventsPosted)
        }
        eventsToBeSunk.forEach { hub.sink(it) }

        eventsPosted.shouldHaveSize(eventsToBeSunk.size)
        collectJob.cancel()
    }
}
