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

package com.hadisatrio.apps.kotlin.journal3.geography

import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Places
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.test.Test

class SelectAPlaceUseCaseTest {

    private val presenter = mockk<Presenter<Places>>(relaxed = true)
    private val modalPresenter = mockk<Presenter<Modal>>(relaxed = true)

    private val places = SelfPopulatingPlaces(
        noOfPlaces = 10,
        origin = FakePlaces()
    )

    @Test
    fun `Forwards whole places to the presenter`() {
        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(withArg { it.shouldHaveSize(places.toList().size) }) }
    }

    @Test
    fun `Presents a modal in case an exception occurs while presenting`() {
        every { presenter.present(any()) } throws RuntimeException()

        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true)
        )()

        verify(
            exactly = 1
        ) { modalPresenter.present(withArg { it.kind.shouldBe("presentation_retrial_confirmation") }) }
    }

    @Test
    fun `Retries presentation upon receiving retrial confirmation`() {
        every { presenter.present(any()) } throws RuntimeException()

        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                ModalApprovalEvent("presentation_retrial_confirmation"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 2) { modalPresenter.present(any()) }
    }

    @Test(timeout = 5_000)
    fun `Completes upon receiving retrial dismissal`() {
        every { presenter.present(any()) } throws RuntimeException()

        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                ModalDismissalEvent("presentation_retrial_confirmation")
            ),
            eventSink = mockk(relaxed = true)
        )()
    }

    @Test
    fun `Forwards to the sink when a place is selected through its position`() {
        val eventSink = mockk<EventSink>(relaxed = true)
        val targetPosition = Random.nextInt(places.toList().lastIndex)
        val target = places.elementAt(targetPosition)

        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                SelectionEvent("item_position", targetPosition.toString())
            ),
            eventSink = eventSink
        )()

        verify(exactly = 1) {
            eventSink.sink(
                withArg { event ->
                    event.shouldBeInstanceOf<SelectionEvent>()
                    event.selectionKind.shouldBe("place")
                    event.selectedIdentifier.shouldBe(target.id.toString())
                }
            )
        }
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        listOf(CancellationEvent("user"), CancellationEvent("system")).forEach { event ->
            SelectAPlaceUseCase(
                places = places,
                presenter = presenter,
                modalPresenter = modalPresenter,
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true)
            )()
        }
    }

    @Test
    fun `Ignores unknown events without repercussions`() {
        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            modalPresenter = modalPresenter,
            eventSource = RecordedEventSource(
                TextInputEvent("foo", "Bar"),
                SelectionEvent("fizz", LiteralTimestamp(Instant.DISTANT_FUTURE).toString()),
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
