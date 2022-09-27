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

import com.hadisatrio.apps.kotlin.journal3.Router
import com.hadisatrio.apps.kotlin.journal3.id.TargetId
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

class ShowStoryUseCase(
    private val targetId: TargetId,
    private val stories: Stories,
    private val presenter: Presenter<Story>,
    private val eventSource: EventSource,
    private val eventSink: EventSink,
    private val router: Router
) : UseCase {

    override fun invoke() {
        presentState()
        observeEvents()
    }

    private fun presentState() {
        val story = stories.findStory(targetId.asUuid()).first()
        presenter.present(story)
    }

    private fun observeEvents() = runBlocking {
        eventSource.events()
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent)?.also { handleCompletion() } == null }
            .collect { event -> handleEvent(event) }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is SelectionEvent -> handleSelectionEvent(event)
        }
    }

    private fun handleSelectionEvent(event: SelectionEvent) {
        val kind = event.selectionKind
        val identifier = event.selectedIdentifier
        when (kind) {
            "action" -> when (identifier) {
                "edit" -> {
                    val story = stories.findStory(targetId.asUuid()).first()
                    router.toStoryEditor(story.id)
                }
                "add" -> {
                    router.toMomentEditor()
                }
            }
        }
    }

    private fun handleCompletion() {
        router.toPrevious()
    }
}
