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

package com.hadisatrio.apps.kotlin.journal3.moment.datetime

import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.moment.FakeMoments
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.time.Duration

class ClockRespectingMomentsTest {

    @Test
    fun `Ensures newly-created moments are updated with current timestamp`() {
        val clock = mockk<Clock>()
        val origin = FakeMoments()
        val moments = ClockRespectingMoments(clock, origin)
        val current = Clock.System.now()
        val currentTimestamp = Timestamp(current)
        every { clock.now() } returns current

        val moment = moments.new()

        moment.timestamp.difference(currentTimestamp).shouldBe(Duration.ZERO)
    }
}
