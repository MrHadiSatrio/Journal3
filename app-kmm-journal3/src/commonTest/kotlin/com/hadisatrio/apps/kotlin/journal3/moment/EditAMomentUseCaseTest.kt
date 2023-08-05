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

import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.id.FakeTargetId
import com.hadisatrio.apps.kotlin.journal3.id.InvalidTargetId
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.fake.FakePresenter
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test

class EditAMomentUseCaseTest {

    private val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
    private val place = places.first()
    private val presenter = FakePresenter<Moment>()
    private val modalPresenter = FakePresenter<Modal>()
    private val eventSink = FakeEventSink()

    @Test
    fun `Updates the target moment when target ID is valid`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        val story = stories.first()
        val moment = story.moments.first()

        EditAMomentUseCase(
            targetId = FakeTargetId(moment.id),
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                SelectionEvent("timestamp", LiteralTimestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                SelectionEvent("place", place.id.toString()),
                SelectionEvent("attachments", "content://foo,content://bar"),
                CompletionEvent()
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        story.moments.shouldHaveSize(1)
        moment.description.toString().shouldBe("Foo")
        moment.timestamp.shouldBe(LiteralTimestamp(Instant.DISTANT_FUTURE))
        moment.sentiment.shouldBe(Sentiment(0.75F))
        moment.place.id.shouldBe(place.id)
        moment.attachments.shouldHaveSize(2)
    }

    @Test
    fun `Updates a new moment when target ID is invalid`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                SelectionEvent("timestamp", LiteralTimestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                CompletionEvent()
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        story.moments.shouldHaveSize(1)
        story.moments.first().description.toString().shouldBe("Foo")
        story.moments.first().timestamp.shouldBe(LiteralTimestamp(Instant.DISTANT_FUTURE))
        story.moments.first().sentiment.shouldBe(Sentiment(0.75F))
    }

    @Test
    fun `Prevents accidental cancellation by the user when a meaningful edit has been made`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = presenter,
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
            eventSink = eventSink,
            clock = Clock.System
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeTrue()
        story.moments.shouldHaveSize(1)
        story.moments.first().description.toString().shouldBe("Fizz")
    }

    @Test
    fun `Does not prevent accidental cancellation by the user when a meaningful edit has not been made`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Fizz"),
                TextInputEvent("description", ""),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                SelectionEvent("sentiment", Sentiment.DEFAULT.toString()),
                CancellationEvent("user")
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeFalse()
        story.moments.shouldBeEmpty()
    }

    @Test
    fun `Deletes the moment-in-edit when it is a new one and the user cancels without editing`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeFalse()
        story.moments.shouldBeEmpty()
    }

    @Test
    fun `Does not delete the moment-in-edit when it is an existing one even if the user cancels without editing`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        val story = stories.first()
        val moment = story.moments.first()
        val targetId = FakeTargetId(moment.id)

        moment.update(TokenableString("Fizz"))

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeFalse()
        story.moments.shouldHaveSize(1)
        story.moments.first().description.toString().shouldBe("Fizz")
    }

    @Test
    fun `Forwards the moment to the presenter again when refresh is requested`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                CompletionEvent()
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        presenter.presentedCount().shouldBe(4)
    }

    @Test
    fun `Forwards to the sink when action 'delete' is selected`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        val story = stories.first()
        val moment = story.moments.first()
        val targetId = FakeTargetId(moment.id)

        EditAMomentUseCase(
            targetId = targetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                SelectionEvent("action", "delete"),
                CompletionEvent()
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()

        eventSink.hasSunk { event ->
            event["name"] == "Selection Event" &&
                event["selection_kind"] == "action" &&
                event["selected_id"] == "delete_moment" &&
                event["moment_id"] == moment.id.toString()
        }.shouldBeTrue()
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        listOf(CancellationEvent("system")).forEach { event ->
            EditAMomentUseCase(
                targetId = InvalidTargetId,
                storyId = FakeTargetId(story.id),
                stories = stories,
                places = places,
                presenter = presenter,
                modalPresenter = modalPresenter,
                eventSource = RecordedEventSource(event),
                eventSink = eventSink,
                clock = Clock.System
            )()
        }
    }

    @Test
    fun `Ignores unknown events without repercussions`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 0, origin = FakeStories())
        val story = stories.first()

        EditAMomentUseCase(
            targetId = InvalidTargetId,
            storyId = FakeTargetId(story.id),
            stories = stories,
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("foo", "Bar"),
                SelectionEvent("action", "foo"),
                SelectionEvent("fizz", LiteralTimestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("buzz", Sentiment(0.75F).toString()),
                ModalApprovalEvent("lorem"),
                CancellationEvent("system"),
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = eventSink,
            clock = Clock.System
        )()
    }
}
