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
import com.hadisatrio.libs.kotlin.foundation.EventHandlingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class ShowStoryUseCase(
    private val storyId: Uuid,
    private val stories: Stories,
    private val presenter: Presenter<Story>,
    eventSource: EventSource,
    eventSink: EventSink
) : EventHandlingUseCase(eventSource, eventSink) {

    override fun invokeInternal() {
        presentState()
    }

    private fun presentState() {
        val story = stories.findStory(storyId).firstOrNull()
        if (story == null) {
            complete()
        } else {
            presenter.present(story)
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelectionEvent(event)
            is RefreshRequestEvent -> presentState()
            is CancellationEvent -> handleCancellation()
        }
    }

    private fun handleSelectionEvent(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        val story = stories.findStory(storyId).first()
        when (kind) {
            "item_position" -> handleItemPositionSelectionEvent(story, identifier)
            "action" -> handleActionSelectionEvent(identifier, story)
        }
    }

    private fun handleActionSelectionEvent(identifier: String, story: Story) {
        val actionIdentifier = when (identifier) {
            "edit" -> "edit_story"
            "delete" -> "delete_story"
            "add" -> "add_moment"
            else -> return
        }
        eventSink.sink(
            SelectionEvent(
                selectionKind = "action",
                selectedIdentifier = actionIdentifier,
                "story_id" to story.id.toString()
            )
        )
    }

    private fun handleItemPositionSelectionEvent(story: Story, identifier: String) {
        val moment = story.moments.elementAt(identifier.toInt())
        eventSink.sink(
            SelectionEvent(
                selectionKind = "action",
                selectedIdentifier = "edit_moment",
                "moment_id" to moment.id.toString(),
                "story_id" to story.id.toString()
            )
        )
    }

    private fun handleCancellation() {
        complete()
    }
}
