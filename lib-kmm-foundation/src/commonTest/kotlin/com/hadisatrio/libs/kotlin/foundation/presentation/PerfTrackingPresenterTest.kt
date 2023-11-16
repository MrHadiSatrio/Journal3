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

package com.hadisatrio.libs.kotlin.foundation.presentation

import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class PerfTrackingPresenterTest {

    @Test
    fun `Sinks the duration of which origin took to handle the call`() {
        val clock = mockk<Clock>()
        val eventSink = FakeEventSink()
        val origin = mockk<Presenter<Int>>(relaxed = true)
        val tracked = PerfTrackingPresenter(clock, eventSink, origin)
        val oneTime = Instant.fromEpochMilliseconds(1701908618816)
        val twoSecsLater = oneTime + 2.seconds
        every { clock.now() } returnsMany(listOf(oneTime, twoSecsLater))

        tracked.present(Int.MAX_VALUE)

        eventSink.hasSunk { capturedEvent ->
            capturedEvent.shouldNotBeNull()
            val eventDesc = capturedEvent.describe()
            eventDesc.shouldContain("name", "Perf Sensitive Event")
            eventDesc.shouldContain("duration", "2000")
            true
        }
    }
}
