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
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
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

class ShowStoriesUseCase(
    private val stories: Stories,
    private val presenter: Presenter<Stories>,
    private val eventSource: EventSource,
    private val eventSink: EventSink,
    private val router: Router
) : UseCase {

    private val completionEvents by lazy { MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 1) }

    override fun invoke() {
        presentState()
        observeEvents()
    }

    private fun presentState() {
        presenter.present(stories)
    }

    private fun observeEvents() = runBlocking {
        merge(eventSource.events(), completionEvents)
            .onEach { eventSink.sink(it) }
            .takeWhile { event -> (event as? CompletionEvent)?.also { handleCompletion() } == null }
            .collect { event -> handleEvent(event) }
    }

    private suspend fun handleEvent(event: Event) {
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
                val story = stories.elementAt(position)
                router.toStoryDetail(story.id)
            }
            "action" -> when (identifier) {
                "add" -> {
                    router.toStoryEditor()
                }
            }
        }
    }

    private suspend fun handleCancellation() {
        completionEvents.emit(CompletionEvent())
    }

    private fun handleCompletion() {
        router.toPrevious()
    }
}
