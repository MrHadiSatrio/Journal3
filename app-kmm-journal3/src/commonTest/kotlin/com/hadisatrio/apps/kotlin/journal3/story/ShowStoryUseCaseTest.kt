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

import com.hadisatrio.apps.kotlin.journal3.event.RecordedEventSource
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.id.FakeTargetId
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ShowStoryUseCaseTest {

    private val stories = SelfPopulatingStories(
        noOfStories = 1,
        noOfMoments = 1,
        origin = FakeStories()
    )

    @Test
    fun `Forwards story to the presenter`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val presenter = mockk<Presenter<Story>>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(story) }
    }

    @Test
    fun `Forwards story to the presenter again when refresh is requested`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val presenter = mockk<Presenter<Story>>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
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

        verify(exactly = 4) { presenter.present(story) }
    }

    @Test
    fun `Forwards to the sink when action 'add' is selected`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
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
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
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
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
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
        val story = stories.first()
        val moment = story.moments.first()
        val targetId = FakeTargetId(story.id)
        val eventSink = mockk<EventSink>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
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

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)

        listOf(CancellationEvent("user"), CancellationEvent("system")).forEach { event ->
            ShowStoryUseCase(
                targetId = targetId,
                stories = stories,
                presenter = mockk(relaxed = true),
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true)
            )()
        }
    }

    @Test
    fun `Does nothing when given an unsupported event`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
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
