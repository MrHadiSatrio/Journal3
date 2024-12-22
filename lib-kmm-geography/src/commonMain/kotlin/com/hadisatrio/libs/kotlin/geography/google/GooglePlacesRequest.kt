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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class GooglePlacesRequest(
    private val coordinates: Coordinates,
    private val radiusLimit: Int,
    private val countLimit: Int,
    private val textQuery: String
) {

    constructor(
        coordinates: Coordinates,
        radiusLimit: Int,
        countLimit: Int
    ) : this(coordinates, radiusLimit, countLimit, "")

    override fun toString(): String {
        val (latitude, longitude) = coordinates.latlng

        val restriction = buildJsonObject {
            putJsonObject("circle") {
                put("radius", radiusLimit)
                putJsonObject("center") {
                    put("latitude", latitude)
                    put("longitude", longitude)
                }
            }
        }
        val request = buildJsonObject {
            put("rankPreference", "DISTANCE")
            put("maxResultCount", countLimit.coerceIn(VALID_LIMIT_RANGE))
            if (textQuery.isNotBlank()) {
                // Means we're dealing with a textual search request, which
                // does not support circular location restriction. Hence we
                // are using bias here.
                put("locationBias", restriction)
                put("textQuery", textQuery)
            } else {
                put("locationRestriction", restriction)
            }
        }

        return request.toString()
    }

    companion object {
        private val VALID_LIMIT_RANGE = 1..20
    }
}
