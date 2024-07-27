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

package com.hadisatrio.apps.kotlin.journal3.alert

import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.libs.kotlin.foundation.EventHandlingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlinx.datetime.Clock
import kotlin.time.Duration

class AlertInactivityUseCase(
    private val threshold: Duration,
    private val stories: Stories,
    private val presenter: Presenter<Modal>,
    eventSource: EventSource,
    eventSink: EventSink
) : EventHandlingUseCase(eventSource, eventSink) {

    override fun invokeInternal() {
        if (!isAlertNecessary()) return
        presenter.present(BinaryConfirmationModal("inactivity_alert"))
    }

    private fun isAlertNecessary(): Boolean {
        if (!stories.hasMoments()) return true
        val currentTimestamp = LiteralTimestamp(Clock.System.now())
        val mostRecentTimestamp = stories.mostRecentMoment().timestamp
        return currentTimestamp.difference(mostRecentTimestamp) > threshold
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is ModalApprovalEvent -> handleModalApproval(event)
            is CancellationEvent -> handleCancellation()
        }
    }

    private fun handleModalApproval(event: ModalApprovalEvent) {
        if (event.modalKind != "inactivity_alert") return
        eventSink.sink(
            SelectionEvent(
                selectionKind = "action",
                selectedIdentifier = "add_moment",
                "story_id" to stories.first().id.toString()
            )
        )
        complete()
    }

    private fun handleCancellation() {
        complete()
    }
}
