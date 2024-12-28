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
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Speed
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class CaptureAMomentUseCaseTest {

    private val story = FakeStory()
    private val gambirStation = FakePlace(coordinates = LiteralCoordinates("-6.1765638,106.8299464"))
    private val gambirBusDepot = FakePlace(coordinates = LiteralCoordinates("-6.1766638,106.8300464"))
    private val places = FakePlaces(gambirStation, gambirBusDepot)
    private val coordinates = mockk<Coordinates>()
    private val speed = mockk<Speed>()
    private val arbitraryInstant = Instant.fromEpochMilliseconds(1735316550)
    private val clock = mockk<Clock>()

    private val useCase = CaptureAMomentUseCase(story, places, coordinates, speed, clock)

    @BeforeTest
    fun `Init mocks`() {
        every { speed.value } returns 0.0
        every { coordinates.accuracyInMeters } returns 50F
        every { clock.now() } returns arbitraryInstant
    }

    @Test
    fun `Captures a non-notable moment about the currently visited place`() {
        useCase()

        story.moments.shouldHaveSize(1)
        val captured = story.moments.first()
        captured.timestamp.value.shouldBeEqual(arbitraryInstant)
        captured.isNotable.shouldBeFalse()
        captured.place.id.shouldBeEqual(places.first().id)
    }

    @Test
    fun `Prioritizes a known nearby place whilst capturing`() {
        val placesList = mutableListOf<Place>(gambirStation, gambirBusDepot)
        val places = FakePlaces(placesList)
        val useCase = CaptureAMomentUseCase(story, places, coordinates, speed, clock)

        useCase() // …captures visit to Gambir Station.
        placesList.removeFirst() // …so that the next execution would pick up the bus depot.
        every { clock.now() } returns arbitraryInstant + 1.days + 1.seconds
        useCase() // …attempts to capture the bus depot.

        story.moments.shouldHaveSize(2)
        story.moments.distinctBy { it.place.id }.shouldHaveSize(1)
        story.moments.map { it.place.id }.first().shouldBeEqual(gambirStation.id)
    }

    @Test
    fun `Skips capturing if there is an existing moment about the place written today`() {
        // Places stay intact, so multiple execution would point to the same place to
        // be captured. Hence sufficient to simulate the scenario we want.
        repeat(10) { useCase() }

        story.moments.shouldHaveSize(1)
    }

    @Test
    fun `Skips capturing if the place, post correction, is already written for today`() {
        val placesList = mutableListOf<Place>(gambirStation, gambirBusDepot)
        val places = FakePlaces(placesList)
        val useCase = CaptureAMomentUseCase(story, places, coordinates, speed, clock)

        useCase() // …captures visit to Gambir Station.
        placesList.removeFirst() // …so that the next execution would pick up the bus depot.
        // Unlike the previous test case, we don't advance the time.
        useCase() // …attempts to capture the bus depot.

        story.moments.shouldHaveSize(1)
        story.moments.distinctBy { it.place.id }.shouldHaveSize(1)
        story.moments.map { it.place.id }.first().shouldBeEqual(gambirStation.id)
    }

    @Test
    fun `Skips capturing when coordinates accuracy is greater than 50 meters`() {
        every { coordinates.accuracyInMeters } returns 51F

        useCase()

        story.moments.shouldBeEmpty()
    }

    @Test
    fun `Skips capture if the user is moving`() {
        every { speed.value } returns 100.0

        useCase()

        story.moments.shouldBeEmpty()
    }
}
