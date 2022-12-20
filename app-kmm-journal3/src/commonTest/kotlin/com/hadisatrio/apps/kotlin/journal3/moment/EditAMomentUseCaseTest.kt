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

import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.event.RecordedEventSource
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.id.FakeTargetId
import com.hadisatrio.apps.kotlin.journal3.id.TargetId
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test

class EditAMomentUseCaseTest {

    @Test
    fun `Updates the target moment when target ID is valid`() {
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        val place = places.first()
        val story = stories.first()
        val moment = story.moments.first()

        EditAMomentUseCase(
            targetId = FakeTargetId(moment.id),
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = mockk(relaxed = true),
            modalPresenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                SelectionEvent("timestamp", Timestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                SelectionEvent("place", place.id.toString()),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        story.moments.shouldHaveSize(1)
        moment.description.toString().shouldBe("Foo")
        moment.timestamp.shouldBe(Timestamp(Instant.DISTANT_FUTURE))
        moment.sentiment.shouldBe(Sentiment(0.75F))
        moment.place.id.shouldBe(place.id)
    }

    @Test
    fun `Updates a new moment when target ID is invalid`() {
        val targetId = mockk<TargetId>()
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        every { targetId.isValid() } returns false

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = mockk(relaxed = true),
            modalPresenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                SelectionEvent("timestamp", Timestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        story.moments.shouldHaveSize(1)
        story.moments.first().description.toString().shouldBe("Foo")
        story.moments.first().timestamp.shouldBe(Timestamp(Instant.DISTANT_FUTURE))
        story.moments.first().sentiment.shouldBe(Sentiment(0.75F))
    }

    @Test
    fun `Prevents accidental cancellation by the user`() {
        val targetId = mockk<TargetId>()
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)
        every { targetId.isValid() } returns false

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                CancellationEvent("user"),
                ModalDismissalEvent("edit_cancellation_confirmation"),
                TextInputEvent("description", "Fizz"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        verify(exactly = 1) { modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") }) }
        story.moments.shouldHaveSize(1)
        story.moments.first().description.toString().shouldBe("Fizz")
    }

    @Test
    fun `Deletes the moment-in-edit when the user requests and confirms to cancel`() {
        val targetId = mockk<TargetId>()
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)
        every { targetId.isValid() } returns false

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        verify(exactly = 1) { modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") }) }
        story.moments.shouldBeEmpty()
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        listOf(CancellationEvent("system")).forEach { event ->
            EditAMomentUseCase(
                targetId = mockk(relaxed = true),
                storyId = FakeTargetId(story.id),
                stories = stories,
                places = mockk(relaxed = true),
                presenter = mockk(relaxed = true),
                modalPresenter = mockk(relaxed = true),
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true),
                clock = Clock.System
            )()
        }
    }

    @Test
    fun `Ignores unknown events without repercussions`() {
        val targetId = mockk<TargetId>()
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        every { targetId.isValid() } returns false

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
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
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()
    }
}
