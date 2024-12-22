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

package com.hadisatrio.libs.kotlin.geography.google

import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import kotlinx.io.IOException
import kotlin.test.Test

class GoogleNearbyPlacesTest {

    private val coordinates = LiteralCoordinates(-6.2244727, 106.8101278)
    private val apiKey = "wCHWXm4g%Nkg@79z8bMzkg@79z8bMz"
    private val httpClientEngine = clientEngine(false)
    private val places = GoogleNearbyPlaces(
        coordinates = coordinates,
        limit = 100,
        apiKey = apiKey,
        httpClient = HttpClient(httpClientEngine)
    )

    @Test
    fun `Lists down places in vicinity (less than 250 KMs) of the given coordinates`() {
        val distances = places.map { it.distanceTo(coordinates) }
        distances.forEach { it.value.shouldBeBetween(0.0, 250_000.0, 0.0) }
    }

    @Test
    fun `Guards against multiple requests for request with nearby coordinates`() {
        val coordinates = mockk<Coordinates>()
        val places = GoogleNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = apiKey,
            httpClient = HttpClient(httpClientEngine)
        )

        every { coordinates.toString() }.returns("-6.2244727,106.8101278")
        repeat(10) { places.toList() }
        httpClientEngine.requestHistory.size.shouldBe(1)
        httpClientEngine.responseHistory.size.shouldBe(1)

        every { coordinates.toString() }.returns("-5.2244727,107.8101278")
        repeat(10) { places.toList() }
        httpClientEngine.requestHistory.size.shouldBe(2)
        httpClientEngine.responseHistory.size.shouldBe(2)
    }

    @Test
    fun `Finds places by their ID`() {
        val otherPlaces = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val otherPlace = otherPlaces.first()
        places.forEach { places.findPlace(it.id).shouldNotBeEmpty() }
        places.findPlace(otherPlace.id).shouldBeEmpty()
    }

    @Test
    fun `Finds places by their name`() {
        val otherPlaces = SelfPopulatingPlaces(noOfPlaces = 1, origin = FakePlaces())
        val otherPlace = otherPlaces.first()
        places.forEach { places.findPlace(it.name).shouldNotBeEmpty() }
    }

    @Test
    fun `Throws NoSuchElementException when iterating outside of valid bound`() {
        val iterator = places.iterator()
        shouldThrow<NoSuchElementException> { repeat(101) { iterator.next() } }
    }

    @Test
    fun `Throws IOException when server is unable to respond`() {
        val places = GoogleNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = apiKey,
            httpClient = HttpClient(clientEngine(true))
        )

        shouldThrow<IOException> { places.toList() }
    }

    @Test
    fun `Throws UnsupportedOperationException when asked to create new places`() {
        shouldThrow<UnsupportedOperationException> { places.new() }
    }

    private fun clientEngine(shouldFail: Boolean): MockEngine {
        return MockEngine { request ->
            val url = request.url
            val headers = request.headers
            url.toString().shouldStartWith("https://places.googleapis.com/v1/places")
            headers.contains("X-Goog-Api-Key", apiKey).shouldBeTrue()
            headers.contains("X-Goog-FieldMask").shouldBeTrue()

            if (shouldFail) {
                respondError(HttpStatusCode.InternalServerError)
            } else {
                respondOk(
                    """
                    {
                      "places": [
                        {
                          "id": "ChIJJUGQ2fHzaS4RVMXJKuiojVE",
                          "location": {
                            "latitude": -6.2244636999999994,
                            "longitude": 106.8101556
                          },
                          "displayName": {
                            "text": "Foster & Bridge Indonesia",
                            "languageCode": "en"
                          },
                          "shortFormattedAddress": "One Pacific Place 15th Floor, Jl. Jend. Sudirman kav 52-53, RT.5/RW.3, Senayan, South Jakarta City"
                        },
                        {
                          "id": "ChIJH73YdVHxaS4RZ0BOiLCGvR0",
                          "location": {
                            "latitude": -6.2244585999999993,
                            "longitude": 106.810166
                          },
                          "displayName": {
                            "text": "Rumours",
                            "languageCode": "id"
                          },
                          "shortFormattedAddress": "Pacific Place Lantai 1, Jl. Jenderal Sudirman No.52-53, RT.5/RW.3, Senayan, Kota Jakarta Selatan"
                        }
                      ]
                    }
                    """.trimIndent()
                )
            }
        }
    }
}
