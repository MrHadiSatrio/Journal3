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
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStory
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class ShowStoryUseCaseTest {

    private val story = SelfPopulatingStory(1, FakeStory())

    @Test
    fun `Forwards story to the presenter`() {
        val presenter = mockk<Presenter<Story>>(relaxed = true)

        ShowStoryUseCase(
            story = story,
            presenter = presenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(story) }
    }

    @Test
    fun `Forwards story to the presenter again when refresh is requested`() {
        val presenter = mockk<Presenter<Story>>(relaxed = true)

        ShowStoryUseCase(
            story = story,
            presenter = presenter,
            eventSource = RecordedEventSource(
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 4) { presenter.present(story) }
    }

    @Test
    fun `Forwards to the sink when action 'add' is selected`() {
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            story = story,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("action", "add"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        verify(exactly = 1) {
            eventSink.sink(
                withArg { event ->
                    event["name"].shouldBe("Selection Event")
                    event["selection_kind"].shouldBe("action")
                    event["selected_id"].shouldBe("add_moment")
                    event["story_id"].shouldBe(story.id.toString())
                }
            )
        }
    }

    @Test
    fun `Forwards to the sink when action 'delete' is selected`() {
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            story = story,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("action", "delete"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        verify(exactly = 1) {
            eventSink.sink(
                withArg { event ->
                    event["name"].shouldBe("Selection Event")
                    event["selection_kind"].shouldBe("action")
                    event["selected_id"].shouldBe("delete_story")
                    event["story_id"].shouldBe(story.id.toString())
                }
            )
        }
    }

    @Test
    fun `Forwards to the sink when action 'edit' is selected`() {
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            story = story,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("action", "edit"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        verify(exactly = 1) {
            eventSink.sink(
                withArg { event ->
                    event["name"].shouldBe("Selection Event")
                    event["selection_kind"].shouldBe("action")
                    event["selected_id"].shouldBe("edit_story")
                    event["story_id"].shouldBe(story.id.toString())
                }
            )
        }
    }

    @Test
    fun `Forwards to the sink when action 'item_position' is selected`() {
        val moment = story.moments.first()
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            story = story,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("item_position", "0"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        verify(exactly = 1) {
            eventSink.sink(
                withArg { event ->
                    event["name"].shouldBe("Selection Event")
                    event["selection_kind"].shouldBe("action")
                    event["selected_id"].shouldBe("edit_moment")
                    event["moment_id"].shouldBe(moment.id.toString())
                    event["story_id"].shouldBe(story.id.toString())
                }
            )
        }
    }

    @Test
    fun `Completes when given an invalid story`() {
        val eventSink = FakeEventSink()

        ShowStoryUseCase(
            story = story,
            presenter = mockk<Presenter<Story>>(relaxed = true),
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = eventSink
        )()

        eventSink.hasSunk { it is CompletionEvent }.shouldBeTrue()
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        listOf(CancellationEvent("user"), CancellationEvent("system")).forEach { event ->
            ShowStoryUseCase(
                story = story,
                presenter = mockk(relaxed = true),
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true)
            )()
        }
    }

    @Test
    fun `Does nothing when given an unsupported event`() {
        ShowStoryUseCase(
            story = story,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("foo", "foo"),
                SelectionEvent("action", "foo"),
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()
    }
}
