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

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.debounce
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.computationScheduler
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * An [EventSource] that suppresses rapid-fire emissions from an upstream [EventSource],
 * forwarding an event only once the stream has been idle for [timeoutMillis].
 *
 * The no-argument constructor uses a 300 ms timeout on the computation scheduler.
 *
 * @param timeoutMillis Idle duration required before an event is forwarded.
 * @param scheduler Scheduler used for the debounce timer.
 * @param origin The upstream [EventSource] to debounce.
 */
class DebouncingEventSource(
    private val timeoutMillis: Duration,
    private val scheduler: Scheduler,
    private val origin: EventSource
) : EventSource {

    constructor(origin: EventSource) : this(DEFAULT_TIMEOUT, computationScheduler, origin)

    override fun events(): Observable<Event> {
        return origin.events().debounce(timeoutMillis, scheduler)
    }

    companion object {
        private val DEFAULT_TIMEOUT = 300L.milliseconds
    }
}
