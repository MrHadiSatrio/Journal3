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

import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoments
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import com.hadisatrio.libs.kotlin.foundation.presentation.fake.FakePresenter
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ShowMomentsUseCaseTest {

    private val moments = SelfPopulatingMoments(noOfMoments = 1, origin = FakeMoments())
    private val presenter = FakePresenter<Moments>()
    private val eventSink = FakeEventSink()

    @Test
    fun `Forwards moments to the presenter`() {
        ShowMomentsUseCase(
            moments = moments,
            presenter = presenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = eventSink
        )()

        presenter.presentedCount().shouldBe(1)
        presenter.hasPresented { it == moments }.shouldBeTrue()
    }

    @Test
    fun `Forwards moments to the presenter again when refresh is requested`() {
        ShowMomentsUseCase(
            moments = moments,
            presenter = presenter,
            eventSource = RecordedEventSource(
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                RefreshRequestEvent("test"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        presenter.presentedCount().shouldBe(4)
    }

    @Test
    fun `Forwards to the sink when action 'add' is selected`() {
        ShowMomentsUseCase(
            moments = moments,
            presenter = presenter,
            eventSource = RecordedEventSource(
                SelectionEvent("action", "add"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        eventSink.hasSunk { event ->
            event["name"] == "Selection Event" &&
                event["selection_kind"] == "action" &&
                event["selected_id"] == "add_moment"
        }.shouldBeTrue()
    }

    @Test
    fun `Forwards to the sink when action 'item_position' is selected`() {
        val moment = moments.first()

        ShowMomentsUseCase(
            moments = moments,
            presenter = presenter,
            eventSource = RecordedEventSource(
                SelectionEvent("item_position", "0"),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()

        eventSink.hasSunk { event ->
            event["name"] == "Selection Event" &&
                event["selection_kind"] == "action" &&
                event["selected_id"] == "edit_moment" &&
                event["moment_id"] == moment.id.toString()
        }.shouldBeTrue()
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        listOf(CancellationEvent("user"), CancellationEvent("system")).forEach { event ->
            ShowMomentsUseCase(
                moments = moments,
                presenter = presenter,
                eventSource = RecordedEventSource(event),
                eventSink = eventSink
            )()
        }
    }

    @Test
    fun `Does nothing when given an unsupported event`() {
        ShowMomentsUseCase(
            moments = moments,
            presenter = presenter,
            eventSource = RecordedEventSource(
                SelectionEvent("foo", "foo"),
                SelectionEvent("action", "foo"),
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()
    }
}
