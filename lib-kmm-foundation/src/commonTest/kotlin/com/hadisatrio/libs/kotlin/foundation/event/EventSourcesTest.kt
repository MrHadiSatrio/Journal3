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

import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSource
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class EventSourcesTest {

    @Test
    fun `Channels events posted from participating sources`() = runTest {
        val rawSources = arrayOf(
            FakeEventSource(MutableSharedFlow(extraBufferCapacity = 1)),
            FakeEventSource(MutableSharedFlow(extraBufferCapacity = 1)),
            FakeEventSource(MutableSharedFlow(extraBufferCapacity = 1))
        )

        val events = mutableListOf<Event>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            EventSources(*rawSources).events().toList(events)
        }
        rawSources.forEachIndexed { i, s -> s.produce(CompletionEvent()) }

        events.shouldHaveSize(rawSources.size)
        collectJob.cancel()
    }
}
