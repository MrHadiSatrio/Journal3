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

import com.hadisatrio.apps.kotlin.journal3.id.TargetId
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

class DeleteStoryUseCase(
    private val targetId: TargetId,
    private val stories: Stories,
    private val presenter: Presenter<Modal>,
    private val eventSource: EventSource,
    private val eventSink: EventSink
) : UseCase {

    private val completionEvents by lazy { MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 1) }

    override fun invoke() = runBlocking {
        present()
        observeEvents()
    }

    private fun present() {
        val modal = if (stories.containsStory(targetId.asUuid())) {
            BinaryConfirmationModal("story_deletion_confirmation")
        } else {
            BinaryConfirmationModal("story_not_found_notification")
        }
        presenter.present(modal)
    }

    private suspend fun observeEvents() {
        merge(eventSource.events(), completionEvents)
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent) == null }
            .collect { event -> handle(event) }
    }

    private suspend fun handle(event: Event) {
        when (event) {
            is ModalApprovalEvent -> handleApproval(event)
            is ModalDismissalEvent -> handleDismissal()
        }
    }

    private suspend fun handleApproval(event: ModalApprovalEvent) {
        when (event.modalKind) {
            "story_not_found_notification" -> {
                completionEvents.emit(CompletionEvent())
            }
            "story_deletion_confirmation" -> {
                stories.findStory(targetId.asUuid()).first().forget()
                completionEvents.emit(CompletionEvent())
            }
        }
    }

    private suspend fun handleDismissal() {
        completionEvents.emit(CompletionEvent())
    }
}
