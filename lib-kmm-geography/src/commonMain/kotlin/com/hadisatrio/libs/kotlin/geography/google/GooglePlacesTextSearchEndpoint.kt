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
import io.ktor.client.HttpClient
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class GooglePlacesTextSearchEndpoint(
    httpClient: HttpClient,
    apiKey: String,
    private val coordinates: Coordinates,
    private val radiusLimitInM: Int,
    private val textQuery: String,
    onResponse: (Iterable<GooglePlace>) -> Unit
) : GooglePlacesEndpoint(httpClient, apiKey, onResponse) {

    override val path: String = "places:searchText"
    override val supportsPagination: Boolean = true

    init {
        require(textQuery.isNotBlank()) { "Query must not be blank." }
    }

    override fun requestBody(pageToken: String, pageSize: Int): String {
        val request = buildJsonObject {
            put("rankPreference", "DISTANCE")
            put("maxResultCount", pageSize)
            put("locationBias", GooglePlacesAreaRequest(coordinates, radiusLimitInM).toJsonObject())
            put("textQuery", textQuery)
            if (pageToken.isNotBlank()) put("pageToken", pageToken)
        }
        return request.toString()
    }
}
