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

import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.event.RecordedEventSource
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Places
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.test.Test

class SelectAPlaceUseCaseTest {

    private val places = SelfPopulatingPlaces(
        noOfPlaces = 10,
        origin = FakePlaces()
    )

    @Test
    fun `Forwards whole places to the presenter`() {
        val presenter = mockk<Presenter<Places>>(relaxed = true)

        SelectAPlaceUseCase(
            places = places,
            presenter = presenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(withArg { it.shouldHaveSize(places.toList().size) }) }
    }

    @Test
    fun `Forwards to the sink when a place is selected through its position`() {
        val eventSink = mockk<EventSink>(relaxed = true)
        val targetPosition = Random.nextInt(places.toList().lastIndex)
        val target = places.elementAt(targetPosition)

        SelectAPlaceUseCase(
            places = places,
            presenter = mockk(relaxUnitFun = true),
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

    @Test
    fun `Ignores unknown events without repercussions`() {
        SelectAPlaceUseCase(
            places = places,
            presenter = mockk(relaxed = true),
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
