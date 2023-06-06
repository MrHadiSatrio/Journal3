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

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.event.RecordedEventSource
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.id.FakeTargetId
import com.hadisatrio.apps.kotlin.journal3.id.TargetId
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStory
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant
import kotlin.test.Test

class EditAStoryUseCaseTest {

    @Test
    fun `Updates the target story when target ID is valid`() {
        val targetId = FakeTargetId(uuid4())
        val story = FakeStory(targetId.asUuid(), mutableListOf())
        val stories = FakeStories(story)

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                TextInputEvent("title", "Foo"),
                TextInputEvent("synopsis", "Bar"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        story.title.shouldBe("Foo")
        story.synopsis.shouldBe(TokenableString("Bar"))
    }

    @Test
    fun `Updates a new story when target ID is invalid`() {
        val targetId = mockk<TargetId>()
        val stories = FakeStories()
        every { targetId.isValid() } returns false

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                TextInputEvent("title", "Foo"),
                TextInputEvent("synopsis", "Bar"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        stories.shouldHaveSize(1)
        stories.first().title.shouldBe("Foo")
        stories.first().synopsis.shouldBe(TokenableString("Bar"))
    }

    @Test
    fun `Prevents accidental cancellation by the user when a meaningful edit has been made`() {
        val targetId = mockk<TargetId>()
        val stories = FakeStories()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)
        every { targetId.isValid() } returns false

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("title", "Foo"),
                TextInputEvent("synopsis", "Bar"),
                CancellationEvent("user"),
                ModalDismissalEvent("edit_cancellation_confirmation"),
                TextInputEvent("title", "Fizz"),
                TextInputEvent("synopsis", "Buzz"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 1) { modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") }) }
        stories.shouldHaveSize(1)
        stories.first().title.shouldBe("Fizz")
        stories.first().synopsis.toString().shouldBe("Buzz")
    }

    @Test
    fun `Does not prevent accidental cancellation by the user when a meaningful edit has not been made`() {
        val targetId = mockk<TargetId>()
        val stories = FakeStories()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)
        every { targetId.isValid() } returns false

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("title", "Foo"),
                TextInputEvent("synopsis", "Bar"),
                TextInputEvent("title", ""),
                TextInputEvent("synopsis", ""),
                CancellationEvent("user")
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(inverse = true) {
            modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") })
        }
        stories.shouldBeEmpty()
    }

    @Test
    fun `Deletes the story-in-edit when it is a new one and the user cancels without editing`() {
        val targetId = mockk<TargetId>()
        val stories = FakeStories()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)
        every { targetId.isValid() } returns false

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(inverse = true) {
            modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") })
        }
        stories.shouldBeEmpty()
    }

    @Test
    fun `Does not delete the story-in-edit when it is an existing one even if the user cancels without editing`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)

        story.update("Fizz")

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(inverse = true) {
            modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") })
        }
        stories.shouldHaveSize(1)
        stories.first().title.shouldBe("Fizz")
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        val targetId = mockk<TargetId>()
        val stories = FakeStories()
        every { targetId.isValid() } returns false

        listOf(CancellationEvent("system")).forEach { event ->
            EditAStoryUseCase(
                targetId = targetId,
                stories = stories,
                presenter = mockk(relaxed = true),
                modalPresenter = mockk(relaxed = true),
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true)
            )()
        }
    }

    @Test(timeout = 5_000)
    fun `Ignores unknown events without repercussions`() {
        val targetId = mockk<TargetId>()
        val stories = FakeStories()
        every { targetId.isValid() } returns false

        EditAStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            modalPresenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                TextInputEvent("foo", "Bar"),
                SelectionEvent("fizz", Timestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("buzz", Sentiment(0.75F).toString()),
                ModalApprovalEvent("lorem"),
                CancellationEvent("system"),
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()
    }
}
