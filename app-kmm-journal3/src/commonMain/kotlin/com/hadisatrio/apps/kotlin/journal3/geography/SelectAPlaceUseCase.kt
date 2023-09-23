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

package com.hadisatrio.apps.kotlin.journal3.geography

import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Places
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

class SelectAPlaceUseCase(
    private val places: Places,
    private val presenter: Presenter<Places>,
    private val modalPresenter: Presenter<Modal>,
    private val eventSource: EventSource,
    private val eventSink: EventSink,
) : UseCase {

    private val completionEvents by lazy { MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 1) }

    override fun invoke() {
        presentState()
        observeEvents()
    }

    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private fun presentState() {
        try {
            presenter.present(places)
        } catch (e: Exception) {
            val modal = BinaryConfirmationModal("presentation_retrial_confirmation")
            modalPresenter.present(modal)
        }
    }

    private fun observeEvents() = runBlocking {
        merge(eventSource.events(), completionEvents)
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent) == null }
            .collect { event -> handleEvent(event) }
    }

    private suspend fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelection(event)
            is ModalApprovalEvent -> handleModalApproval(event)
            is ModalDismissalEvent -> handleModalDismissal(event)
            is CancellationEvent -> handleCancellation()
        }
    }

    private suspend fun handleSelection(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "item_position" -> {
                val position = identifier.toInt()
                val target = places.elementAt(position)
                eventSink.sink(SelectionEvent("place", target.id.toString()))
                completionEvents.emit(CompletionEvent())
            }
        }
    }

    private fun handleModalApproval(event: ModalApprovalEvent) {
        if (event.modalKind != "presentation_retrial_confirmation") return
        presentState()
    }

    private suspend fun handleModalDismissal(event: ModalDismissalEvent) {
        if (event.modalKind != "presentation_retrial_confirmation") return
        completionEvents.emit(CompletionEvent())
    }

    private suspend fun handleCancellation() {
        completionEvents.emit(CompletionEvent())
    }
}
