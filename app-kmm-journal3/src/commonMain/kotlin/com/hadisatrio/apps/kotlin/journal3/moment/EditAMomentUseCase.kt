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

import com.benasher44.uuid.uuidFrom
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.id.TargetId
import com.hadisatrio.apps.kotlin.journal3.moment.datetime.ClockRespectingMoments
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Places
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

@Suppress("LongParameterList")
class EditAMomentUseCase(
    private val targetId: TargetId,
    private val storyId: TargetId,
    private val stories: Stories,
    private val places: Places,
    private val presenter: Presenter<Moment>,
    private val modalPresenter: Presenter<Modal>,
    private val eventSource: EventSource,
    private val eventSink: EventSink,
    private val clock: Clock
) : UseCase {

    private val completionEvents by lazy { MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 1) }
    private lateinit var currentTarget: UpdateDeferringMoment
    private var isTargetNew: Boolean = false
    private var isEditCancelled: Boolean = false

    override operator fun invoke() {
        identifyTarget()
        presentInitialState()
        observeEvents()
    }

    private fun identifyTarget() {
        currentTarget = UpdateDeferringMoment(
            if (targetId.isValid()) {
                stories.findMoment(targetId.asUuid()).first()
            } else {
                val story = stories.findStory(storyId.asUuid()).first()
                val moments = ClockRespectingMoments(clock, story.moments)
                isTargetNew = true
                moments.new()
            }
        )
    }

    private fun presentInitialState() {
        presenter.present(currentTarget)
    }

    private fun observeEvents() = runBlocking {
        merge(eventSource.events(), completionEvents)
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent)?.also { handleCompletion() } == null }
            .collect { event -> handle(event) }
    }

    private suspend fun handle(event: Event) {
        when (event) {
            is TextInputEvent -> handleTextInput(event)
            is SelectionEvent -> handleSelection(event)
            is ModalApprovalEvent -> handleModalApproval(event)
            is CancellationEvent -> handleCancellation(event)
        }
    }

    private fun handleTextInput(event: TextInputEvent) {
        when (event.inputKind) {
            "description" -> currentTarget.update(TokenableString(event.inputValue))
        }

        presenter.present(currentTarget)
    }

    private fun handleSelection(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "timestamp" -> currentTarget.update(Timestamp(identifier))
            "sentiment" -> currentTarget.update(Sentiment(identifier))
            "place" -> currentTarget.update(places.findPlace(uuidFrom(identifier)).first())
        }
    }

    private suspend fun handleModalApproval(event: ModalApprovalEvent) {
        when (event.modalKind) {
            "edit_cancellation_confirmation" -> {
                isEditCancelled = true
                completionEvents.emit(CompletionEvent())
            }
        }
    }

    private fun handleCompletion() {
        if (isEditCancelled) {
            if (isTargetNew) currentTarget.forget()
        } else {
            currentTarget.commit()
        }
    }

    private suspend fun handleCancellation(event: CancellationEvent) {
        if (event.reason != "user") {
            completionEvents.emit(CompletionEvent())
            return
        }

        if (currentTarget.updatesMade()) {
            val modal = BinaryConfirmationModal("edit_cancellation_confirmation")
            modalPresenter.present(modal)
        } else {
            isEditCancelled = true
        }
    }
}
