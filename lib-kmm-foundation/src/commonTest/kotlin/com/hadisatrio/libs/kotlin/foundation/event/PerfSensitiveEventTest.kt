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

package com.hadisatrio.libs.kotlin.foundation.event

import io.kotest.matchers.maps.shouldContain
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test

class PerfSensitiveEventTest {

    @Test
    fun `Describes itself`() {
        val clock = mockk<Clock>()
        every { clock.now() } returns Instant.fromEpochMilliseconds(1)

        val event = PerfSensitiveEvent("Foo", clock)
        val description = event.describe()

        description.shouldContain("tag" to "Foo")
        description.shouldContain("epoch_time" to "1")
    }
}
