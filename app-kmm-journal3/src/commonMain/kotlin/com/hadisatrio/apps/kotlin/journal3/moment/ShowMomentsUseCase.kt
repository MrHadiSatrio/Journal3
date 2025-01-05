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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.libs.kotlin.foundation.EventHandlingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class ShowMomentsUseCase(
    private val moments: Moments,
    private val presenter: Presenter<Moments>,
    eventSource: EventSource,
    eventSink: EventSink
) : EventHandlingUseCase(eventSource, eventSink) {

    override fun invokeInternal() {
        presentState()
    }

    private fun presentState() {
        presenter.present(moments)
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelectionEvent(event)
            is RefreshRequestEvent -> presentState()
            is CancellationEvent -> handleCancellation()
        }
    }

    private fun handleSelectionEvent(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "item_position" -> handleItemPositionSelectionEvent(moments, identifier)
            "action" -> handleActionSelectionEvent(identifier)
        }
    }

    private fun handleActionSelectionEvent(identifier: String) {
        if (identifier != "add") return
        val event = SelectionEvent("action", "add_moment")
        eventSink.sink(event)
    }

    private fun handleItemPositionSelectionEvent(moments: Moments, identifier: String) {
        val moment = moments.elementAt(identifier.toInt())
        val event = SelectionEvent(
            selectionKind = "action",
            selectedIdentifier = "edit_moment",
            "moment_id" to moment.id.toString()
        )
        eventSink.sink(event)
    }

    private fun handleCancellation() {
        complete()
    }
}
