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
import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.test.scheduler.TestScheduler
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test
import kotlin.time.Duration

class EventHubTest {

    @Test
    fun `Forwards sunk events to the collector`() {
        val eventsToBeSunk = arrayOf(
            PerfSensitiveEvent(Duration.ZERO, TextInputEvent("Foo", "Bar")),
            SelectionEvent("Foo", "Bar"),
            CancellationEvent("system")
        )
        val hub = EventHub(PublishSubject())
        val eventsPosted = mutableListOf<Event>()
        val scheduler = TestScheduler()
        val disposable = SchedulingRxEventSource(scheduler, hub).events().subscribe { eventsPosted.add(it) }

        eventsToBeSunk.forEach { hub.sink(it) }

        eventsPosted.shouldHaveSize(eventsToBeSunk.size)
        disposable.dispose()
    }
}
