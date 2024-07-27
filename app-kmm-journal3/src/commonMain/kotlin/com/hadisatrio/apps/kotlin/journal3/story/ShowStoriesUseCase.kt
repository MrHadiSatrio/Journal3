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

import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.libs.kotlin.foundation.EventHandlingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class ShowStoriesUseCase(
    private val stories: Stories,
    private val presenter: Presenter<Stories>,
    eventSource: EventSource,
    eventSink: EventSink
) : EventHandlingUseCase(eventSource, eventSink) {

    override fun invokeInternal() {
        presentState()
    }

    private fun presentState() {
        presenter.present(stories)
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelection(event)
            is RefreshRequestEvent -> presentState()
            is CancellationEvent -> handleCancellation()
        }
    }

    private fun handleSelection(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "item_position" -> {
                val position = identifier.toInt()
                val storyId = stories.elementAt(position).id
                eventSink.sink(
                    SelectionEvent(
                        selectionKind = "action",
                        selectedIdentifier = "view_story",
                        "story_id" to storyId.toString()
                    )
                )
            }
        }
    }

    private fun handleCancellation() {
        complete()
    }
}
