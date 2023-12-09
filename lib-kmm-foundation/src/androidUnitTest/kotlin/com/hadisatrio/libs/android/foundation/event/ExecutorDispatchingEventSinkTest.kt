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

package com.hadisatrio.libs.android.foundation.event

import com.hadisatrio.libs.android.foundation.concurrent.CurrentThreadExecutor
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEvent
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.concurrent.Executor
import kotlin.test.Test

class ExecutorDispatchingEventSinkTest {

    private val executor: Executor = spyk(CurrentThreadExecutor())
    private val origin: EventSink = mockk(relaxUnitFun = true)
    private val eventSink = ExecutorDispatchingEventSink(executor, origin)

    @Test
    fun `Forwards call to the origin on the given executor`() {
        val event = FakeEvent()
        eventSink.sink(event)
        verify(exactly = 1) { executor.execute(any()) }
        verify(exactly = 1) { origin.sink(event) }
    }
}
