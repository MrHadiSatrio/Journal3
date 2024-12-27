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

package com.hadisatrio.libs.android.geography

import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.test.runner.AndroidJUnit4
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class GmsCoordinatesTest {

    private val gpsLocation = Location(LocationManager.GPS_PROVIDER)
    private val client = mockk<FusedLocationProviderClient>()
    private val clock = TestClock()
    private val coordinates = GmsCoordinates(client, clock)

    @BeforeTest
    fun `Init mocks`() {
        gpsLocation.latitude = -6.2244727
        gpsLocation.longitude = 106.8101278
        gpsLocation.speed = 3F
        gpsLocation.accuracy = 10F
        every { client.getCurrentLocation(any<Int>(), anyNullable()) } returns Tasks.forResult(
            gpsLocation
        )
    }

    @Test
    fun `Returns device location and its accuracy`() {
        var latlng = 0.0 to 0.0
        var accuracy = 0F
        val thread = Thread {
            latlng = coordinates.latlng
            accuracy = coordinates.accuracyInMeters
        }

        thread.start()
        thread.join()

        latlng.shouldBe(gpsLocation.latitude to gpsLocation.longitude)
        accuracy.shouldBe(gpsLocation.accuracy)
    }

    @Test
    fun `Returns device speed`() {
        var speed = 0.0
        val thread = Thread {
            speed = coordinates.value
        }

        thread.start()
        thread.join()

        speed.shouldBe(gpsLocation.speed)
    }

    @Test
    fun `Prevents spamming the client on rapid requests`() {
        val thread = Thread {
            repeat(10) { coordinates.latlng }
            repeat(10) { coordinates.value }
            clock.advanceBy(11.minutes)
            repeat(10) { coordinates.latlng }
            repeat(10) { coordinates.value }
        }

        thread.start()
        thread.join()

        verify(exactly = 2) { client.getCurrentLocation(any<Int>(), anyNullable()) }
    }

    @Test
    fun `Prevents spamming the LocationManager on multi-threaded requests`() {
        val threads = mutableSetOf<Thread>()
        repeat(2) {
            val thread = Thread { coordinates.latlng }
            threads.add(thread)
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        verify(exactly = 1) { client.getCurrentLocation(any<Int>(), anyNullable()) }
    }
}
