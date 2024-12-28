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
import android.os.Build
import androidx.test.runner.AndroidJUnit4
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLocationManager
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class LocationManagerCoordinatesTest {

    @get:Rule
    val retryRule = RetryRule(retryCount = 5)

    private val application = RuntimeEnvironment.getApplication()
    private val locationManager = spyk(application.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    private val shadowLocationManager = Shadows.shadowOf(locationManager)
    private val gpsLocation = Location(LocationManager.GPS_PROVIDER)
    private val clock = TestClock()
    private val coordinates = LocationManagerCoordinates(locationManager, clock)

    @After
    fun `Resets shadows`() {
        ShadowLocationManager.reset()
    }

    @Test(timeout = 10_000)
    fun `Returns device location and its accuracy`() {
        var latlng = 0.0 to 0.0
        var accuracy = 0F
        val thread = Thread {
            latlng = coordinates.latlng
            accuracy = coordinates.accuracyInMeters
        }

        thread.start()
        shadowLocationManager.enqueueSimulateLocation(gpsLocation)
        thread.join()

        latlng.shouldBe(gpsLocation.latitude to gpsLocation.longitude)
        accuracy.shouldBe(gpsLocation.accuracy)
    }

    @Test(timeout = 10_000)
    fun `Prevents spamming the LocationManager on rapid requests`() {
        val thread = Thread {
            repeat(10) { coordinates.latlng }
            clock.advanceBy(11.seconds)
            repeat(10) { coordinates.latlng }
        }

        thread.start()
        shadowLocationManager.enqueueSimulateLocation(gpsLocation)
        shadowLocationManager.enqueueSimulateLocation(gpsLocation)
        thread.join()

        verify(exactly = 2) { locationManager.requestSingleUpdate(any<String>(), any(), any()) }
    }

    @Test(timeout = 10_000)
    fun `Prevents spamming the LocationManager on multi-threaded requests`() {
        val threads = mutableSetOf<Thread>()
        repeat(2) {
            threads.add(
                Thread {
                    coordinates.latlng
                }
            )
        }

        threads.forEach { it.start() }
        shadowLocationManager.enqueueSimulateLocation(gpsLocation)
        threads.forEach { it.join() }

        verify(exactly = 1) { locationManager.requestSingleUpdate(any<String>(), any(), any()) }
    }

    @Test(timeout = 10_000)
    fun `Throws IllegalStateException when no providers are available`() {
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, false)
        shadowLocationManager.setProviderEnabled(LocationManager.PASSIVE_PROVIDER, false)
        val thread = Thread {
            shouldThrow<IllegalStateException> { coordinates.latlng }
        }

        thread.start()
        shadowLocationManager.enqueueSimulateLocation(gpsLocation)
        thread.join()
    }

    private fun ShadowLocationManager.enqueueSimulateLocation(location: Location) {
        Thread.sleep(100L)
        simulateLocation(location)
    }
}
