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

package com.hadisatrio.libs.android.foundation.widget

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource

class BackButtonCancellationEventSource(
    private val dispatcher: OnBackPressedDispatcher
) : EventSource {

    private val events: Observable<CancellationEvent> by lazy {
        observable { emitter ->
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    emitter.onNext(CancellationEvent("user"))
                }
            }
            dispatcher.addCallback(callback)
            emitter.setCancellable { callback.remove() }
        }
    }

    constructor(activity: ComponentActivity) : this(activity.onBackPressedDispatcher)

    override fun events(): Observable<Event> {
        return events
    }
}
