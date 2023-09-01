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
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class ClockRespectingMomentTest {

    private val clock = mockk<Clock>(relaxed = true)
    private val origin = mockk<MomentInEdit>(relaxed = true)
    private val current = Clock.System.now()
    private val currentTimestamp = LiteralTimestamp(current)

    @BeforeTest
    fun `Init mocks`() {
        every { clock.now() } returns current
    }

    @Test
    fun `Updates its own timestamp to the clock's`() {
        every { origin.isNewlyCreated() } returns true
        every { origin.updatesMade() } returns false

        val moment = ClockRespectingMoment(clock, origin)

        moment.timestamp.difference(currentTimestamp).shouldBe(Duration.ZERO)
    }

    @Test
    fun `Respects its origin timestamp if it is not newly created`() {
        val originTimestamp = LiteralTimestamp(current - 1.hours)
        every { origin.timestamp } returns originTimestamp
        every { origin.isNewlyCreated() } returns false
        every { origin.updatesMade() } returns false

        val moment = ClockRespectingMoment(clock, origin)

        moment.timestamp.shouldBe(originTimestamp)
    }

    @Test
    fun `Respects its origin timestamp if an update has been made`() {
        val originTimestamp = LiteralTimestamp(current - 1.hours)
        every { origin.timestamp } returns originTimestamp
        every { origin.isNewlyCreated() } returns false
        every { origin.updatesMade() } returns true

        val moment = ClockRespectingMoment(clock, origin)

        moment.timestamp.shouldBe(originTimestamp)
    }

    @Test
    fun `Respects its origin timestamp if an update has been made, even if it's newly created`() {
        val originTimestamp = LiteralTimestamp(current - 1.hours)
        every { origin.timestamp } returns originTimestamp
        every { origin.isNewlyCreated() } returns true
        every { origin.updatesMade() } returns true

        val moment = ClockRespectingMoment(clock, origin)

        moment.timestamp.shouldBe(originTimestamp)
    }
}
