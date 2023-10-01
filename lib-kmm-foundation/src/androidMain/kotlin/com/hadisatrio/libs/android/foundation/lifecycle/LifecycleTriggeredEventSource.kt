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

package com.hadisatrio.libs.android.foundation.lifecycle

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource

class LifecycleTriggeredEventSource(
    private val lifecycleOwner: LifecycleOwner,
    private val lifecycleEvent: Lifecycle.Event,
    private val eventFactory: Event.Factory
) : EventSource {

    private val events: Observable<Event> by lazy {
        observable { emitter ->
            val handler = Handler(Looper.getMainLooper())
            val observer = LifecycleEventObserver { _, event ->
                if (event != lifecycleEvent) return@LifecycleEventObserver
                emitter.onNext(eventFactory.create())
            }

            handler.post { lifecycleOwner.lifecycle.addObserver(observer) }
            emitter.setCancellable {
                handler.post { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
        }
    }

    override fun events(): Observable<Event> {
        return events
    }
}
