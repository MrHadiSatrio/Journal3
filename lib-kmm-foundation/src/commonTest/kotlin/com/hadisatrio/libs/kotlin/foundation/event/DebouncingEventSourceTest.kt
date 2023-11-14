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

import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.subject.publish.PublishSubject
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class DebouncingEventSourceTest {

    @Test
    fun `Debounces the given origin`() {
        val timeout = 100L.milliseconds
        val scheduler = mockk<Scheduler>(relaxed = true)
        val origin = mockk<EventSource>(relaxed = true)
        val source = DebouncingEventSource(timeout, scheduler, origin)
        every { origin.events() } returns PublishSubject()

        source.events().subscribe { }

        verify(atLeast = 1) { origin.events() }
        verify(atLeast = 1) { scheduler.newExecutor() }
    }
}
