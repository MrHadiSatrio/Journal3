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

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.merge
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.takeUntil
import com.badoo.reaktive.subject.replay.ReplaySubject
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.ExceptionalEvent
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Places

class SelectAPlaceUseCase(
    private val places: Places,
    private val presenter: Presenter<Iterable<Place>>,
    private val modalPresenter: Presenter<Modal>,
    private val eventSource: EventSource,
    private val eventSink: EventSink,
) : UseCase {

    private val completionEvents by lazy { ReplaySubject<CompletionEvent>(bufferSize = 1) }

    private var presentedPlaces: Iterable<Place> = emptyList()

    override fun invoke() {
        presentState(places)
        observeEvents()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun presentState(places: Iterable<Place>) {
        try {
            presenter.present(places)
            presentedPlaces = places
        } catch (e: Exception) {
            val modal = BinaryConfirmationModal("presentation_retrial_confirmation")
            modalPresenter.present(modal)
            eventSink.sink(ExceptionalEvent(e))
        }
    }

    private fun observeEvents() {
        merge(eventSource.events(), completionEvents)
            .doOnBeforeNext { event -> eventSink.sink(event) }
            .takeUntil { event -> event is CompletionEvent }
            .subscribe { event -> handleEvent(event) }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelection(event)
            is TextInputEvent -> handleTextInput(event)
            is ModalApprovalEvent -> handleModalApproval(event)
            is ModalDismissalEvent -> handleModalDismissal(event)
            is CancellationEvent -> handleCancellation()
        }
    }

    private fun handleSelection(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "item_position" -> {
                val position = identifier.toInt()
                val target = presentedPlaces.elementAt(position)
                eventSink.sink(SelectionEvent("place", target.id.toString()))
                completionEvents.onNext(CompletionEvent())
            }
        }
    }

    private fun handleTextInput(event: TextInputEvent) {
        if (event.inputKind != "query") return
        if (event.inputValue.isBlank()) {
            presentState(places)
        } else {
            presentState(places.findPlace(event.inputValue))
        }
    }

    private fun handleModalApproval(event: ModalApprovalEvent) {
        if (event.modalKind != "presentation_retrial_confirmation") return
        presentState(places)
    }

    private fun handleModalDismissal(event: ModalDismissalEvent) {
        if (event.modalKind != "presentation_retrial_confirmation") return
        completionEvents.onNext(CompletionEvent())
    }

    private fun handleCancellation() {
        completionEvents.onNext(CompletionEvent())
    }
}
