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

package com.hadisatrio.libs.android.geography.google

import android.os.Build
import androidx.test.runner.AndroidJUnit4
import com.google.android.gms.common.internal.Preconditions
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place.Field.ADDRESS
import com.google.android.libraries.places.api.model.Place.Field.ID
import com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
import com.google.android.libraries.places.api.model.Place.Field.NAME
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchByTextResponse
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.libraries.places.api.net.SearchNearbyResponse
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class GoogleSdkNearbyPlacesTest {

    private val coordinates = LiteralCoordinates(-6.2244727, 106.8101278)
    private val limit = 100
    private val client = mockk<PlacesClient>(relaxed = true)
    private val places = GoogleSdkNearbyPlaces(coordinates, limit, client)

    @BeforeTest
    fun `Disable Google's thread preconditions`() {
        mockkStatic(Preconditions::class)
        every { Preconditions.checkNotMainThread() } returns Unit
        every { Preconditions.checkNotGoogleApiHandlerThread() } returns Unit
    }

    @Test
    fun `Forwards correctly nearby places requests to the client`() {
        val request = slot<SearchNearbyRequest>()
        val response = SearchNearbyResponse.newInstance(emptyList())
        every { client.searchNearby(capture(request)) } returns Tasks.forResult(response)

        places.iterator()

        request.isCaptured.shouldBeTrue()
        val capturedRequest = request.captured
        capturedRequest.placeFields.shouldContainOnly(ID, NAME, ADDRESS, LAT_LNG)
        val requestedBounds = capturedRequest.locationRestriction
        requestedBounds.shouldBeInstanceOf<CircularBounds>()
        requestedBounds.center.latitude.shouldBe(coordinates.latlng.first)
        requestedBounds.center.longitude.shouldBe(coordinates.latlng.second)
        requestedBounds.radius.shouldBe(100.0)
    }

    @Test
    fun `Forwards correctly text-based search requests to the client`() {
        val request = slot<SearchByTextRequest>()
        val response = SearchByTextResponse.newInstance(emptyList())
        every { client.searchByText(capture(request)) } returns Tasks.forResult(response)

        places.findPlace("Coffee shop")

        request.isCaptured.shouldBeTrue()
        val capturedRequest = request.captured
        capturedRequest.placeFields.shouldContainOnly(ID, NAME, ADDRESS, LAT_LNG)
    }

    @Test
    fun `Guards against multiple requests for request with nearby coordinates`() {
        val coordinates = mockk<Coordinates>()
        val request = slot<SearchNearbyRequest>()
        val response = SearchNearbyResponse.newInstance(emptyList())
        val places = GoogleSdkNearbyPlaces(coordinates, limit, client)
        every { client.searchNearby(capture(request)) } returns Tasks.forResult(response)

        every { coordinates.latlng } returns (-6.2244727 to 106.810127)
        every { coordinates.toString() } returns "-6.2244727,106.810127"
        repeat(10) { places.toList() }
        verify(exactly = 1) { client.searchNearby(any()) }

        every { coordinates.latlng } returns (-5.2244727 to 107.8101278)
        every { coordinates.toString() } returns "-5.2244727,107.8101278"
        repeat(10) { places.toList() }
        verify(exactly = 2) { client.searchNearby(any()) }
    }

    @Test
    fun `Throws NoSuchElementException when iterating outside of valid bound`() {
        val request = slot<SearchNearbyRequest>()
        val response = SearchNearbyResponse.newInstance(emptyList())
        every { client.searchNearby(capture(request)) } returns Tasks.forResult(response)

        val iterator = places.iterator()
        shouldThrow<NoSuchElementException> { repeat(101) { iterator.next() } }
    }

    @Test
    fun `Throws UnsupportedOperationException when asked to create new places`() {
        shouldThrow<UnsupportedOperationException> { places.new() }
    }
}
