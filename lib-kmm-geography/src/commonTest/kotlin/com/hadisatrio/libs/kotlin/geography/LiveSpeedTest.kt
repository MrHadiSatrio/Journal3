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

import com.hadisatrio.libs.kotlin.geography.time.Delay
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeZero
import io.mockk.every
import io.mockk.mockk
import kotlin.math.roundToInt
import kotlin.test.Test

class LiveSpeedTest {

    @Test
    fun `Tells the rate of movement when coordinates change`() {
        val coordinates = mockk<Coordinates>()
        val delay = Delay { /* Noop. */ }
        val speed = LiveSpeed(coordinates, delay)
        every { coordinates.toString() }.returnsMany(
            "-6.275489,107.050648",
            "-6.273470,107.046075"
        )

        val value = speed.value

        value.roundToInt().shouldBeEqual(553)
    }

    @Test
    fun `Tells the rate of movement when coordinates does not change`() {
        val coordinates = mockk<Coordinates>()
        val delay = Delay { /* Noop. */ }
        val speed = LiveSpeed(coordinates, delay)
        every { coordinates.toString() }.returns("-6.275489,107.050648")

        val value = speed.value

        value.roundToInt().shouldBeZero()
    }
}
