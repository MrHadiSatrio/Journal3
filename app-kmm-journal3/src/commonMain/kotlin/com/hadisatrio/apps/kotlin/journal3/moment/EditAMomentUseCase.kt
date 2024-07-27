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

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromString
import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.Stories
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
import com.hadisatrio.libs.kotlin.geography.Places
import com.hadisatrio.libs.kotlin.paraphrase.Paraphraser

@Suppress(
    "LongParameterList",
    "TooManyFunctions"
)
class EditAMomentUseCase(
    private val moment: MomentInEdit,
    private val stories: Stories,
    private val places: Places,
    private val presenter: Presenter<Moment>,
    private val modalPresenter: Presenter<Modal>,
    eventSource: EventSource,
    eventSink: EventSink,
    private val paraphraser: Paraphraser,
) : EventHandlingUseCase(eventSource, eventSink) {

    private val targetId: Uuid by lazy { moment.id }
    private val isTargetNew: Boolean by lazy { moment.isNewlyCreated() }
    private var isEditCancelled: Boolean = false
    private var isParaphrasingEnabled: Boolean = false

    override fun invokeInternal() {
        present()
    }

    private fun present() {
        if (!isTargetNew && !stories.containsMoment(targetId)) {
            isEditCancelled = true
            complete()
        } else {
            presenter.present(moment)
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is TextInputEvent -> handleTextInput(event)
            is SelectionEvent -> handleSelection(event)
            is RefreshRequestEvent -> present()
            is ModalApprovalEvent -> handleModalApproval(event)
            is CancellationEvent -> handleCancellation(event)
        }
    }

    private fun handleTextInput(event: TextInputEvent) {
        if (event.inputKind != "description") return
        moment.update(TokenableString(event.inputValue))
        presenter.present(moment)
    }

    private fun handleSelection(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "timestamp" -> moment.update(LiteralTimestamp(identifier))
            "sentiment" -> moment.update(Sentiment(identifier))
            "place" -> moment.update(places.findPlace(uuidFrom(identifier)).first())
            "attachments" -> moment.update(identifier.split(',').map { Uri.fromString(it) })
            "action" -> handleActionSelection(event)
        }
    }

    private fun handleActionSelection(event: SelectionEvent) {
        when (event.selectedIdentifier) {
            "commit" -> handleCommitActionSelection()
            "delete" -> handleDeleteActionSelection()
            "enable_paraphrasing" -> isParaphrasingEnabled = true
            "disable_paraphrasing" -> isParaphrasingEnabled = false
        }
    }

    private fun handleCommitActionSelection() {
        if (isParaphrasingEnabled) {
            DescriptionParaphrasingMoment(paraphraser, moment).commit()
        } else {
            moment.commit()
        }
        complete()
    }

    private fun handleDeleteActionSelection() {
        eventSink.sink(
            SelectionEvent(
                selectionKind = "action",
                selectedIdentifier = "delete_moment",
                "moment_id" to targetId.toString()
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

        if (moment.updatesMade()) {
            val modal = BinaryConfirmationModal("edit_cancellation_confirmation")
            modalPresenter.present(modal)
        } else {
            isEditCancelled = true
            complete()
        }
    }

    override fun onComplete() {
        if (isEditCancelled && isTargetNew) moment.forget()
    }
}
