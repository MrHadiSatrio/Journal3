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
import com.hadisatrio.apps.kotlin.journal3.id.InvalidTargetId
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
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
                SelectionEvent("attachments", "content://foo,content://bar"),
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
        moment.attachments.shouldHaveSize(2)
    }

    @Test
    fun `Updates a new moment when target ID is invalid`() {
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
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
    fun `Prevents accidental cancellation by the user when a meaningful edit has been made`() {
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val place = places.first()
        val story = stories.first()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Fizz"),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                SelectionEvent("place", place.id.toString()),
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
    fun `Does not prevent accidental cancellation by the user when a meaningful edit has not been made`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Fizz"),
                TextInputEvent("description", ""),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                SelectionEvent("sentiment", Sentiment.DEFAULT.toString()),
                CancellationEvent("user")
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        verify(inverse = true) {
            modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") })
        }
        story.moments.shouldBeEmpty()
    }

    @Test
    fun `Deletes the moment-in-edit when it is a new one and the user cancels without editing`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        verify(inverse = true) {
            modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") })
        }
        story.moments.shouldBeEmpty()
    }

    @Test
    fun `Does not delete the moment-in-edit when it is an existing one even if the user cancels without editing`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        val story = stories.first()
        val moment = story.moments.first()
        val targetId = FakeTargetId(moment.id)
        val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)

        moment.update(TokenableString("Fizz"))

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = mockk(relaxed = true),
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = mockk(relaxed = true),
            clock = Clock.System
        )()

        verify(inverse = true) {
            modalPresenter.present(withArg { it.kind.shouldBe("edit_cancellation_confirmation") })
        }
        story.moments.shouldHaveSize(1)
        story.moments.first().description.toString().shouldBe("Fizz")
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
        val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
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
