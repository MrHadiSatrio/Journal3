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

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.geography.Coordinates
import com.hadisatrio.apps.kotlin.journal3.geography.Place
import com.hadisatrio.apps.kotlin.journal3.geography.Places
import io.ktor.client.HttpClient
import io.ktor.http.URLBuilder
import io.ktor.http.Url

class HereNearbyPlaces(
    private val coordinates: Coordinates,
    private val limit: Int,
    private val apiKey: String,
    private val httpClient: HttpClient
) : Places {

    private val endpointUrl: Url by lazy {
        val builder = URLBuilder("https://browse.search.hereapi.com/v1/browse")
        builder.parameters.append("at", coordinates.toString())
        builder.parameters.append("limit", limit.coerceIn(VALID_LIMIT_RANGE).toString())
        builder.parameters.append("apiKey", apiKey)
        builder.build()
    }

    override fun new(): Place {
        throw UnsupportedOperationException("This collection of places is read-only.")
    }

    override fun findPlace(id: Uuid): Iterable<Place> {
        return filter { it.id == id }
    }

    override fun iterator(): Iterator<Place> {
        return HerePlaceIterator(endpointUrl, httpClient)
    }

    companion object {
        private val VALID_LIMIT_RANGE = 1..100
    }
}
