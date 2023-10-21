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
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Places
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class HereNearbyPlaces(
    private val coordinates: Coordinates,
    private val limit: Int,
    private val apiKey: String,
    private val httpClient: HttpClient
) : Places {

    private val urlBuilder: URLBuilder by lazy {
        val builder = URLBuilder("https://browse.search.hereapi.com/v1/browse")
        builder.parameters.append("limit", limit.coerceIn(VALID_LIMIT_RANGE).toString())
        builder.parameters.append("apiKey", apiKey)
        builder
    }

    private val pastResponses: MutableMap<Coordinates, HttpResponse> by lazy {
        mutableMapOf()
    }

    override fun new(): Place {
        throw UnsupportedOperationException("This collection of places is read-only.")
    }

    override fun findPlace(id: Uuid): Iterable<Place> {
        return filter { it.id == id }
    }

    override fun findPlace(name: String): Iterable<Place> {
        return filter { it.name.contains(name, ignoreCase = true) }
    }

    override fun iterator(): Iterator<Place> = runBlocking {
        val coordinates = LiteralCoordinates(coordinates.toString())
        return@runBlocking iteratorFromPastResponses(coordinates) ?: iteratorFromHttp(coordinates)
    }

    private suspend fun iteratorFromPastResponses(coordinates: Coordinates): Iterator<HerePlace>? {
        return pastResponses.entries
            .firstOrNull { (key, _) -> key.distanceTo(coordinates).value <= DISTANCE_THRESHOLD_METERS }
            ?.let { (_, value) -> iteratorFromResponse(value) }
    }

    private suspend fun iteratorFromHttp(coordinates: Coordinates): Iterator<HerePlace> {
        val url = urlBuilder.apply { parameters.append("at", coordinates.toString()) }.build()
        val response = httpClient.get(url)
        if (!response.status.isSuccess()) throw IOException("HTTP ${response.status}: ${response.body<String>()}.")
        pastResponses[coordinates] = response
        return iteratorFromResponse(response)
    }

    private suspend fun iteratorFromResponse(response: HttpResponse): Iterator<HerePlace> {
        val responseBody = response.body<String>()
        val responseObject = Json.parseToJsonElement(responseBody).jsonObject
        val responseArray = responseObject["items"]!!.jsonArray
        return responseArray.asSequence().map { HerePlace(it) }.iterator()
    }

    companion object {
        private val VALID_LIMIT_RANGE = 1..100
        private const val DISTANCE_THRESHOLD_METERS = 100
    }
}
