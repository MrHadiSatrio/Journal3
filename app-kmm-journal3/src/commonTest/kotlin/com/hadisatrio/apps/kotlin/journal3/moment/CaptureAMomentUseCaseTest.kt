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

import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStory
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test

class CaptureAMomentUseCaseTest {

    private val story = FakeStory()
    private val places = SelfPopulatingPlaces(1, FakePlaces())
    private val clock = mockk<Clock>()

    private val useCase = CaptureAMomentUseCase(story, places, clock)

    @Test
    fun `Captures a non-notable moment about the currently visited place`() {
        every { clock.now() } returns Instant.DISTANT_FUTURE

        useCase()

        story.moments.shouldHaveSize(1)
        val captured = story.moments.first()
        captured.timestamp.value.shouldBeEqual(Instant.DISTANT_FUTURE)
        captured.isNotable.shouldBeFalse()
        captured.place.id.shouldBeEqual(places.first().id)
    }

    @Test
    fun `Prioritizes a known nearby place whilst capturing`() {
        every { clock.now() } returns Instant.DISTANT_FUTURE
        val onePlace = FakePlace(coordinates = LiteralCoordinates("-6.275489,107.050648"))
        val another10mAway = FakePlace(coordinates = LiteralCoordinates("-6.275500,107.050740"))
        val placesList = mutableListOf<Place>(onePlace, another10mAway)
        val places = FakePlaces(placesList)
        val useCase = CaptureAMomentUseCase(story, places, clock)

        useCase()
        placesList.removeFirst() // …so that the next execution would pick up the 2nd place.
        useCase()

        story.moments.shouldHaveSize(2)
        story.moments.distinctBy { it.place.id }.shouldHaveSize(1)
        story.moments.map { it.place.id }.first().shouldBeEqual(onePlace.id)
    }

    @Test
    fun `Skips capturing if there is an existing moment about the place written today`() {
        every { clock.now() } returns Instant.DISTANT_FUTURE

        // Places stay intact, so multiple execution would point to the same place to
        // be captured. Hence sufficient to simulate the scenario we want.
        repeat(10) { useCase() }

        story.moments.shouldHaveSize(1)
    }
}
