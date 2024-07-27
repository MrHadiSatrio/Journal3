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

package com.hadisatrio.apps.kotlin.journal3.forgettable

import com.hadisatrio.libs.kotlin.foundation.EventHandlingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

abstract class DeleteForgettableUseCase(
    private val presenter: Presenter<Modal>,
    eventSource: EventSource,
    eventSink: EventSink
) : EventHandlingUseCase(eventSource, eventSink) {

    override fun invokeInternal() {
        present()
    }

    abstract fun forgettable(): Forgettable?

    private fun present() {
        val modal = if (forgettable() == null) {
            BinaryConfirmationModal("forgettable_not_found_notification")
        } else {
            BinaryConfirmationModal("forgettable_deletion_confirmation")
        }
        presenter.present(modal)
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is ModalApprovalEvent -> handleApproval(event)
            is ModalDismissalEvent -> handleDismissal()
        }
    }

    private fun handleApproval(event: ModalApprovalEvent) {
        when (event.modalKind) {
            "forgettable_not_found_notification" -> {
                complete()
            }
            "forgettable_deletion_confirmation" -> {
                forgettable()!!.forget()
                complete()
            }
        }
    }

    private fun handleDismissal() {
        complete()
    }
}
