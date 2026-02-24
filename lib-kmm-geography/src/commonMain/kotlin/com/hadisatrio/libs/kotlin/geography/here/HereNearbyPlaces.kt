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

import com.benasher44.uuid.Uuid
import com.hadisatrio.libs.kotlin.collection.IteratorIterable
import com.hadisatrio.libs.kotlin.collection.PagingIterator
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Places
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking

/**
 * A read-only [Places] collection backed by the HERE Browse API, returning places within
 * 100 metres of [coordinates].
 *
 * Throws [UnsupportedOperationException] when [new] is called. [findPlace] by ID searches
 * only previously fetched results; iteration performs network requests. [limit] is clamped to
 * 1..100.
 *
 * @param coordinates Centre point around which to search for places.
 * @param limit Maximum number of places to return per request.
 * @param apiKey HERE Maps API key.
 * @param httpClient Ktor HTTP client used to perform API calls.
 */
class HereNearbyPlaces(
    private val coordinates: Coordinates,
    private val limit: Int,
    private val apiKey: String,
    private val httpClient: HttpClient
) : Places {

    private val pastResponses: MutableMap<Coordinates, Set<HerePlace>> by lazy { mutableMapOf() }
    private val pastSearchResults: MutableSet<HerePlace> by lazy { mutableSetOf() }

    override fun new(): Place {
        throw UnsupportedOperationException("This collection of places is read-only.")
    }

    override fun findPlace(id: Uuid): Iterable<Place> {
        // I don't really like this very much. Ideally, we should be able to perform
        // an ID-based query to HERE's services. However, the fact that they are not
        // using UUIDs prevents us from doing so. On the other hand, we have enforced the
        // usage of UUIDs through `Uuid#nameUUIDFromBytes()`, which internally uses
        // MD5, making it nearly impossible to retrieve the original ID.
        // The best we can do then is a simple filter operation on the data we have
        // obtained so far from HERE, either through `findPlace(String)` or `iterator()`.
        return pastSearchResults.filter { it.id == id } + this.filter { it.id == id }
    }

    override fun findPlace(name: String): Iterable<Place> {
        val source = HereBrowseEndpoint(httpClient, apiKey, coordinates, name) {
            pastSearchResults.addAll(it.toSet())
        }
        return IteratorIterable { PagingIterator(source, limit.coerceIn(VALID_LIMIT_RANGE)) }
    }

    override fun iterator(): Iterator<Place> = runBlocking {
        val coordinates = LiteralCoordinates(coordinates.toString())
        return@runBlocking (cachedPlaces(coordinates) ?: httpPlaces(coordinates)).iterator()
    }

    private fun cachedPlaces(coordinates: Coordinates): Iterable<HerePlace>? {
        return pastResponses.entries
            .firstOrNull { (key, _) -> key.distanceTo(coordinates).value <= DISTANCE_THRESHOLD_METERS }
            ?.value
    }

    private fun httpPlaces(coordinates: Coordinates): Iterable<HerePlace> {
        val source = HereBrowseEndpoint(httpClient, apiKey, coordinates) {
            pastResponses[coordinates] = it.toSet()
        }
        return IteratorIterable { PagingIterator(source, limit.coerceIn(VALID_LIMIT_RANGE)) }
    }

    companion object {
        private val VALID_LIMIT_RANGE = 1..100
        private const val DISTANCE_THRESHOLD_METERS = 100
    }
}
