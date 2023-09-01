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

package com.hadisatrio.apps.kotlin.journal3.story

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

@Suppress("LongParameterList")
class EditAStoryUseCase(
    private val story: StoryInEdit,
    private val stories: Stories,
    private val presenter: Presenter<Story>,
    private val modalPresenter: Presenter<Modal>,
    private val eventSource: EventSource,
    private val eventSink: EventSink
) : UseCase {

    private val completionEvents by lazy { MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 1) }
    private val targetId: Uuid by lazy { story.id }
    private val isTargetNew: Boolean by lazy { story.isNewlyCreated() }
    private var isEditCancelled: Boolean = false

    override operator fun invoke() = runBlocking {
        present()
        observeEvents()
    }

    private suspend fun present() {
        if (!isTargetNew && !stories.containsStory(targetId)) {
            isEditCancelled = true
            completionEvents.emit(CompletionEvent())
        } else {
            presenter.present(story)
        }
    }

    private suspend fun observeEvents() {
        merge(eventSource.events(), completionEvents)
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent)?.also { handleCompletion() } == null }
            .collect { event -> handle(event) }
    }

    private suspend fun handle(event: Event) {
        when (event) {
            is TextInputEvent -> handleTextInput(event)
            is SelectionEvent -> handleSelection()
            is ModalApprovalEvent -> handleModalApproval(event)
            is CancellationEvent -> handleCancellation(event)
            is RefreshRequestEvent -> present()
        }
    }

    private fun handleTextInput(event: TextInputEvent) {
        when (event.inputKind) {
            "title" -> story.update(event.inputValue)
            "synopsis" -> story.update(TokenableString(event.inputValue))
        }

        presenter.present(story)
    }

    private fun handleSelection() {
        eventSink.sink(
            SelectionEvent(
                "action",
                "delete_story",
                "story_id" to story.id.toString()
            )
        )
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
            if (isTargetNew) story.forget()
        } else {
            story.commit()
        }
    }

    private suspend fun handleCancellation(event: CancellationEvent) {
        if (event.reason != "user") {
            completionEvents.emit(CompletionEvent())
            return
        }

        if (story.updatesMade()) {
            val modal = BinaryConfirmationModal("edit_cancellation_confirmation")
            modalPresenter.present(modal)
        } else {
            isEditCancelled = true
            completionEvents.emit(CompletionEvent())
        }
    }
}
