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

import android.widget.EditText
import com.badoo.reaktive.coroutinesinterop.asObservable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.skip
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import reactivecircus.flowbinding.android.widget.textChanges

/**
 * An [EventSource] that emits a [TextInputEvent] with [inputKind] on each text change in
 * an [EditText]. The initial value is skipped and only subsequent user changes are emitted.
 *
 * @param editText The [EditText] to observe.
 * @param inputKind Kind label propagated to each emitted [TextInputEvent].
 */
class EditTextInputEventSource(
    private val editText: EditText,
    private val inputKind: String
) : EventSource {

    override fun events(): Observable<Event> {
        return editText.textChanges().asObservable()
            .skip(1)
            .map { TextInputEvent(inputKind, it.toString()) }
    }
}
