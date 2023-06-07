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
import com.hadisatrio.apps.kotlin.journal3.id.TargetId
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

class ShowStoryUseCase(
    private val targetId: TargetId,
    private val stories: Stories,
    private val presenter: Presenter<Story>,
    private val eventSource: EventSource,
    private val eventSink: EventSink
) : UseCase {

    private val completionEvents by lazy { MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 1) }

    override fun invoke() = runBlocking {
        presentState()
        observeEvents()
    }

    private suspend fun presentState() {
        val story = stories.findStory(targetId.asUuid()).firstOrNull()
        if (story == null) {
            completionEvents.emit(CompletionEvent())
        } else {
            presenter.present(story)
        }
    }

    private suspend fun observeEvents() {
        merge(eventSource.events(), completionEvents)
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent) == null }
            .collect { event -> handleEvent(event) }
    }

    private suspend fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelectionEvent(event)
            is RefreshRequestEvent -> presentState()
            is CancellationEvent -> handleCancellation()
        }
    }

    private fun handleSelectionEvent(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        val story = stories.findStory(targetId.asUuid()).first()
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

    private suspend fun handleCancellation() {
        completionEvents.emit(CompletionEvent())
    }
}
