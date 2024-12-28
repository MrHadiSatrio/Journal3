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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

internal class GooglePlacesAreaRequest(
    private val coordinates: Coordinates,
    private val radiusLimitInM: Int,
) {

    fun toJsonObject(): JsonObject {
        val (latitude, longitude) = coordinates.latlng
        return buildJsonObject {
            putJsonObject("circle") {
                put("radius", radiusLimitInM)
                putJsonObject("center") {
                    put("latitude", latitude)
                    put("longitude", longitude)
                }
            }
        }
    }
}
