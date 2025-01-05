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
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoments
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
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
import com.hadisatrio.libs.kotlin.paraphrase.DumbParaphraser
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import kotlinx.datetime.Instant
import kotlin.test.Test

class EditAMomentUseCaseTest {

    private val places = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
    private val place = places.first()
    private val presenter = FakePresenter<Moment>()
    private val modalPresenter = FakePresenter<Modal>()
    private val eventSink = FakeEventSink()

    @Test
    fun `Updates the target moment-in-edit`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment as EditableMoment),
            moments = moments,
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Foo"),
                SelectionEvent("timestamp", LiteralTimestamp(Instant.DISTANT_FUTURE).toString()),
                SelectionEvent("sentiment", Sentiment(0.75F).toString()),
                SelectionEvent("place", place.id.toString()),
                SelectionEvent("attachments", "content://foo,content://bar"),
                SelectionEvent("action", "commit")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()

        moments.shouldHaveSize(1)
        moment.description.toString().shouldBe("Foo")
        moment.timestamp.shouldBe(LiteralTimestamp(Instant.DISTANT_FUTURE))
        moment.sentiment.shouldBe(Sentiment(0.75F))
        moment.place.id.shouldBe(place.id)
        moment.attachments.shouldHaveSize(2)
        moment.isNotable.shouldBeTrue()
    }

    @Test
    fun `Prevents accidental cancellation by the user when a meaningful edit has been made`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment as EditableMoment),
            moments = moments,
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
                SelectionEvent("action", "commit")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeTrue()
        moments.shouldHaveSize(1)
        moments.first().description.toString().shouldBe("Fizz")
    }

    @Test
    fun `Does not prevent accidental cancellation by the user when a meaningful edit has not been made`() {
        val moments = FakeMoments()
        val moment = moments.new()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment),
            moments = moments,
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
            paraphraser = DumbParaphraser
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeFalse()
        moments.shouldBeEmpty()
    }

    @Test
    fun `Deletes the moment-in-edit when it is a new one and the user cancels without editing`() {
        val moments = FakeMoments()
        val moment = moments.new()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment),
            moments = moments,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeFalse()
        moments.shouldBeEmpty()
    }

    @Test
    fun `Does not delete the moment-in-edit when it is an existing one even if the user cancels without editing`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first() as EditableMoment

        moment.update(TokenableString("Fizz"))
        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment),
            moments = moments,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                CancellationEvent("user"),
                ModalApprovalEvent("edit_cancellation_confirmation")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()

        modalPresenter.hasPresented { it.kind == "edit_cancellation_confirmation" }.shouldBeFalse()
        moments.shouldHaveSize(1)
        moments.first().description.toString().shouldBe("Fizz")
    }

    @Test
    fun `Forwards the moment to the presenter again when refresh is requested`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment as EditableMoment),
            moments = moments,
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                SelectionEvent("action", "commit")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()

        presenter.presentedCount().shouldBe(4)
    }

    @Test
    fun `Forwards to the sink when action 'delete' is selected`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment as EditableMoment),
            moments = moments,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                SelectionEvent("action", "delete"),
                SelectionEvent("action", "commit")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()

        eventSink.hasSunk { event ->
            event["name"] == "Selection Event" &&
                event["selection_kind"] == "action" &&
                event["selected_id"] == "delete_moment" &&
                event["moment_id"] == moment.id.toString()
        }.shouldBeTrue()
    }

    @Test
    fun `Consults the paraphraser upon request`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()
        val paraphraser = spyk(DumbParaphraser)

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment as EditableMoment),
            moments = moments,
            places = FakePlaces(),
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("description", "Fizz"),
                SelectionEvent("action", "enable_paraphrasing"),
                SelectionEvent("action", "disable_paraphrasing"),
                SelectionEvent("action", "enable_paraphrasing"),
                SelectionEvent("action", "commit")
            ),
            eventSink = eventSink,
            paraphraser = paraphraser
        )()

        verify { paraphraser.paraphrase("Fizz") }
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()

        listOf(CancellationEvent("system")).forEach { event ->
            EditAMomentUseCase(
                moment = UpdateDeferringMoment(moment as EditableMoment),
                moments = moments,
                places = places,
                presenter = presenter,
                modalPresenter = modalPresenter,
                eventSource = RecordedEventSource(event),
                eventSink = eventSink,
                paraphraser = DumbParaphraser
            )()
        }
    }

    @Test
    fun `Ignores unknown events without repercussions`() {
        val moments = SelfPopulatingMoments(1, FakeMoments())
        val moment = moments.first()

        EditAMomentUseCase(
            moment = UpdateDeferringMoment(moment as EditableMoment),
            moments = moments,
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
                SelectionEvent("action", "commit")
            ),
            eventSink = eventSink,
            paraphraser = DumbParaphraser
        )()
    }
}
