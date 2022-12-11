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

package com.hadisatrio.libs.kotlin.geography.here

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
import io.ktor.utils.io.errors.IOException
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class HereNearbyPlacesTest {

    private val coordinates = LiteralCoordinates(37.5323749, -122.4151854)
    private val apiKey = "wCHWXm4g%Nkg@79z8bMzkg@79z8bMz"
    private val httpClientEngine = clientEngine(false)
    private val places = HereNearbyPlaces(
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
        val places = HereNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = apiKey,
            httpClient = HttpClient(httpClientEngine)
        )

        every { coordinates.toString() }.returns("37.5323749,-122.4151854")
        repeat(10) { places.toList() }
        httpClientEngine.requestHistory.size.shouldBe(1)
        httpClientEngine.responseHistory.size.shouldBe(1)

        every { coordinates.toString() }.returns("38.5323749,-123.4151854")
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
    fun `Throws NoSuchElementException when iterating outside of valid bound`() {
        val iterator = places.iterator()
        shouldThrow<NoSuchElementException> { repeat(101) { iterator.next() } }
    }

    @Test
    fun `Throws IOException when server is unable to respond`() {
        val places = HereNearbyPlaces(
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
            val parameters = url.parameters
            url.toString().shouldStartWith("https://browse.search.hereapi.com/v1/browse")
            parameters.contains("apiKey", apiKey).shouldBeTrue()
            parameters.contains("at", coordinates.toString()).shouldBeTrue()

            if (shouldFail) {
                respondError(HttpStatusCode.InternalServerError)
            } else {
                respondOk(
                    """
                        {
                          "items": [
                            {
                              "title": "Lifemark Rd, Redwood City, CA 94062, United States",
                              "id": "here:af:street:fnY9KMvsOadjmoSObWB8oB",
                              "language": "en",
                              "resultType": "street",
                              "address": { "label": "Lifemark Rd, Redwood City, CA 94062, United States" },
                              "position": { "lat": 37.53019, "lng": -122.39263 }
                            },
                            {
                              "title": "Anglers",
                              "id": "here:pds:place:8409q8vt-e7476fd27a0e4238b0850c2823a6d415",
                              "language": "en",
                              "resultType": "place",
                              "address": { "label": "Anglers, Granada Blvd, Moon Bay, CA 94019, United States" },
                              "position": { "lat": 37.53873, "lng": -122.44575 }
                            }
                          ]
                        }
                    """.trimIndent()
                )
            }
        }
    }
}
