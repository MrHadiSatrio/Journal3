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

package com.hadisatrio.apps.kotlin.journal3.geography.here

import com.hadisatrio.apps.kotlin.journal3.geography.LiteralCoordinates
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import kotlin.test.Test

class HereNearbyPlacesTest {

    private val coordinates = LiteralCoordinates(37.5323749, -122.4151854)
    private val apiKey = "wCHWXm4g%Nkg@79z8bMzkg@79z8bMz"

    @Test
    fun `Lists down places in vicinity (less than 250 KMs) of the given coordinates`() {
        val places = HereNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = apiKey,
            httpClient = HttpClient(clientEngine(false))
        )

        val distances = places.map { it.distanceTo(coordinates) }
        distances.forEach { it.value.shouldBeBetween(0.0, 250_000.0, 0.0) }
    }

    @Test
    fun `Throws NoSuchElementException when iterating outside of valid bound`() {
        val places = HereNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = apiKey,
            httpClient = HttpClient(clientEngine(false))
        )
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
        val places = HereNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = apiKey,
            httpClient = HttpClient(clientEngine(false))
        )

        shouldThrow<UnsupportedOperationException> { places.new() }
    }

    private fun clientEngine(shouldFail: Boolean): HttpClientEngine {
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
