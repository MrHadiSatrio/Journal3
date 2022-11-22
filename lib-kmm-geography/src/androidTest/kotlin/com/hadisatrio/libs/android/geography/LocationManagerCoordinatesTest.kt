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

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.test.runner.AndroidJUnit4
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowLocationManager
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class LocationManagerCoordinatesTest {

    private val application = RuntimeEnvironment.getApplication()
    private val locationManager = spyk(application.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    private val shadowLocationManager = Shadows.shadowOf(locationManager)
    private val gpsLocation = Location(LocationManager.GPS_PROVIDER)
    private val clock = TestClock()

    @After
    fun `Resets shadows`() {
        ShadowLocationManager.reset()
    }

    @Test
    fun `Returns device location`() {
        var lat = 0.0
        var lng = 0.0
        val thread = Thread {
            val coordinates = LocationManagerCoordinates(locationManager, clock)
            lat = coordinates.latitude
            lng = coordinates.longitude
        }

        thread.start()
        shadowLocationManager.simulateLocation(gpsLocation)
        thread.join()

        lat.shouldBe(gpsLocation.latitude)
        lng.shouldBe(gpsLocation.longitude)
    }

    @Test
    fun `Prevents spamming the LocationManager on rapid requests`() {
        val thread = Thread {
            val coordinates = LocationManagerCoordinates(locationManager, clock)
            repeat(10) { coordinates.latitude }
            repeat(10) { coordinates.longitude }
            clock.advanceBy(11.seconds)
            repeat(10) { coordinates.latitude }
            repeat(10) { coordinates.longitude }
        }

        thread.start()
        shadowLocationManager.simulateLocation(gpsLocation)
        thread.join()

        verify(exactly = 2) { locationManager.getCurrentLocation(any(), any(), any(), any()) }
    }

    @Test
    fun `Prevents spamming the LocationManager on multi-threaded requests`() {
        val coordinates = LocationManagerCoordinates(locationManager, clock)
        val threads = mutableSetOf<Thread>()
        repeat(10) {
            threads.add(Thread { coordinates.latitude; coordinates.longitude })
        }

        threads.forEach { it.start() }
        shadowLocationManager.simulateLocation(gpsLocation)
        threads.forEach { it.join() }

        verify(exactly = 1) { locationManager.getCurrentLocation(any(), any(), any(), any()) }
    }

    @Test
    fun `Throws IllegalStateException when no providers are available`() {
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, false)
        shadowLocationManager.setProviderEnabled(LocationManager.PASSIVE_PROVIDER, false)
        val thread = Thread {
            val coordinates = LocationManagerCoordinates(locationManager, clock)
            shouldThrow<IllegalStateException> { coordinates.latitude }
            shouldThrow<IllegalStateException> { coordinates.longitude }
        }

        thread.start()
        shadowLocationManager.simulateLocation(gpsLocation)
        thread.join()
    }
}
