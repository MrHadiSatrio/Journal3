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

import com.benasher44.uuid.Uuid
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class GooglePlace(
    private val jsonObject: JsonObject
) : Place {

    override val id: Uuid by lazy {
        val googleId = jsonObject.getValue("id").jsonPrimitive.content // "ChIJJUGQ2fHzaS4RVMXJKuiojVE"
        Uuid.nameUUIDFromBytes(googleId.toByteArray())
    }

    override val name: String by lazy {
        val displayName = jsonObject.getValue("displayName").jsonObject
        displayName.getValue("text").jsonPrimitive.content
    }

    override val address: String by lazy {
        jsonObject.getValue("shortFormattedAddress").jsonPrimitive.content
    }

    override val coordinates: Coordinates by lazy {
        val position = jsonObject.getValue("location").jsonObject
        LiteralCoordinates("${position.getValue("latitude")},${position.getValue("longitude")}")
    }

    constructor(jsonElement: JsonElement) : this(jsonElement.jsonObject)

    override fun equals(other: Any?): Boolean {
        if (other !is GooglePlace) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
