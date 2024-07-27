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
import com.hadisatrio.libs.kotlin.foundation.EventHandlingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

@Suppress(
    "LongParameterList",
    "TooManyFunctions"
)
class EditAStoryUseCase(
    private val story: StoryInEdit,
    private val stories: Stories,
    private val presenter: Presenter<Story>,
    private val modalPresenter: Presenter<Modal>,
    eventSource: EventSource,
    eventSink: EventSink
) : EventHandlingUseCase(eventSource, eventSink) {

    private val targetId: Uuid by lazy { story.id }
    private val isTargetNew: Boolean by lazy { story.isNewlyCreated() }
    private var isEditCancelled: Boolean = false

    override fun invokeInternal() {
        present()
    }

    private fun present() {
        if (!isTargetNew && !stories.containsStory(targetId)) {
            isEditCancelled = true
            complete()
        } else {
            presenter.present(story)
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is TextInputEvent -> handleTextInput(event)
            is SelectionEvent -> handleSelection(event)
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

    private fun handleSelection(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        if (kind != "action") return
        when (identifier) {
            "commit" -> handleCommitActionSelection()
            "delete" -> handleDeleteActionSelection()
        }
    }

    private fun handleCommitActionSelection() {
        story.commit()
        complete()
    }

    private fun handleDeleteActionSelection() {
        eventSink.sink(
            SelectionEvent(
                "action",
                "delete_story",
                "story_id" to story.id.toString()
            )
        )
    }

    private fun handleModalApproval(event: ModalApprovalEvent) {
        when (event.modalKind) {
            "edit_cancellation_confirmation" -> {
                isEditCancelled = true
                complete()
            }
        }
    }

    private fun handleCancellation(event: CancellationEvent) {
        if (event.reason != "user") {
            complete()
            return
        }

        if (story.updatesMade()) {
            val modal = BinaryConfirmationModal("edit_cancellation_confirmation")
            modalPresenter.present(modal)
        } else {
            isEditCancelled = true
            complete()
        }
    }

    override fun onComplete() {
        if (isEditCancelled && isTargetNew) story.forget()
    }
}
