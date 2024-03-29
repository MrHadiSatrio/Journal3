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
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class ShowStoriesUseCaseTest {

    private val stories = SelfPopulatingStories(
        noOfStories = 10,
        noOfMoments = 10,
        origin = FakeStories()
    )

    @Test
    fun `Forwards whole stories to the presenter`() {
        val presenter = mockk<Presenter<Stories>>(relaxed = true)

        ShowStoriesUseCase(
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(withArg { it.shouldHaveSize(10) }) }
    }

    @Test
    fun `Forwards stories to the presenter again when refresh is requested`() {
        val presenter = mockk<Presenter<Stories>>(relaxed = true)

        ShowStoriesUseCase(
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 4) { presenter.present(withArg { it.shouldHaveSize(10) }) }
    }

    @Test
    fun `Forwards to the sink when a valid story is selected through its position`() {
        val eventSink = mockk<EventSink>(relaxed = true)
        val story = stories.toList().random()
        val storyIndex = stories.indexOf(story)

        ShowStoriesUseCase(
            stories = stories,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("item_position", storyIndex.toString()),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        verify(exactly = 1) {
            eventSink.sink(
                withArg { event ->
                    event["name"].shouldBe("Selection Event")
                    event["selection_kind"].shouldBe("action")
                    event["selected_id"].shouldBe("view_story")
                    event["story_id"].shouldBe(story.id.toString())
                }
            )
        }
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        listOf(CancellationEvent("user"), CancellationEvent("system")).forEach { event ->
            ShowStoriesUseCase(
                stories = stories,
                presenter = mockk(relaxed = true),
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true)
            )()
        }
    }
}
