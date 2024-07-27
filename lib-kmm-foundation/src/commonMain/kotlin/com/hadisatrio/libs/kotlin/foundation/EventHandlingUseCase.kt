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

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.merge
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.takeUntil
import com.badoo.reaktive.subject.replay.ReplaySubject
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource

abstract class EventHandlingUseCase(
    protected val eventSource: EventSource,
    protected val eventSink: EventSink
) : UseCase {

    private val completionEvents = ReplaySubject<CompletionEvent>(bufferSize = 1)

    final override fun invoke() {
        invokeInternal()
        observeEvents()
    }

    abstract fun invokeInternal()

    private fun observeEvents() {
        merge(eventSource.events(), completionEvents)
            .doOnBeforeNext { event -> eventSink.sink(event) }
            .takeUntil { event -> (event as? CompletionEvent)?.also { onComplete() } != null }
            .subscribe { event -> handleEvent(event) }
    }

    protected open fun onComplete() {}

    abstract fun handleEvent(event: Event)

    protected fun complete() {
        completionEvents.onNext(CompletionEvent())
    }
}
