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

package com.hadisatrio.libs.kotlin.geography

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class ResilientCoordinatesTest {

    private val delegate = mockk<Coordinates>()
    private val fallback = mockk<Coordinates>()

    private val coordinates = ResilientCoordinates(delegate, fallback)

    @Test
    fun `Source from the delegate if available`() {
        every { delegate.latlng } returns (-6.2244727 to 106.8101278)
        every { delegate.accuracyInMeters } returns 10F

        val latlng = coordinates.latlng
        val accuracy = coordinates.accuracyInMeters

        latlng.first.shouldBe(-6.2244727)
        latlng.second.shouldBe(106.8101278)
        accuracy.shouldBe(10F)
    }

    @Test
    fun `Source from the fallback if the delegate fails`() {
        every { delegate.latlng } throws CoordinatesUnavailable()
        every { delegate.accuracyInMeters } throws CoordinatesUnavailable()
        every { fallback.latlng } returns (-6.2244727 to 106.8101278)
        every { fallback.accuracyInMeters } returns 10F

        val latlng = coordinates.latlng
        val accuracy = coordinates.accuracyInMeters

        latlng.first.shouldBe(-6.2244727)
        latlng.second.shouldBe(106.8101278)
        accuracy.shouldBe(10F)
    }

    @Test
    fun `Throws when both sources fail`() {
        every { delegate.latlng } throws CoordinatesUnavailable()
        every { delegate.accuracyInMeters } throws CoordinatesUnavailable()
        every { fallback.latlng } throws CoordinatesUnavailable()
        every { fallback.accuracyInMeters } throws CoordinatesUnavailable()

        shouldThrow<CoordinatesUnavailable> { coordinates.latlng }
        shouldThrow<CoordinatesUnavailable> { coordinates.accuracyInMeters }
    }
}
