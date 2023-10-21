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

class DebouncingEventSource(
    private val timeoutMillis: Long,
    private val scheduler: Scheduler,
    private val origin: EventSource
) : EventSource {

    constructor(origin: EventSource) : this(DEFAULT_TIMEOUT_MILLIS, computationScheduler, origin)

    override fun events(): Observable<Event> {
        return origin.events().debounce(timeoutMillis, scheduler)
    }

    companion object {
        private const val DEFAULT_TIMEOUT_MILLIS = 300L
    }
}
