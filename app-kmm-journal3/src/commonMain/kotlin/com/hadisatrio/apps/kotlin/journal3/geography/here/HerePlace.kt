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
import com.hadisatrio.apps.kotlin.journal3.geography.LiteralCoordinates
import com.hadisatrio.apps.kotlin.journal3.geography.Place
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class HerePlace(
    private val jsonObject: JsonObject
) : Place {

    override val id: Uuid by lazy {
        val hereId = jsonObject["id"]!!.jsonPrimitive.content // "here:af:street:fnY9KMvsOadjmoSObWB8oB"
        Uuid.nameUUIDFromBytes(hereId.toByteArray())
    }

    override val name: String by lazy {
        jsonObject["title"]!!.jsonPrimitive.content
    }

    override val address: String by lazy {
        val address = jsonObject["address"]!!.jsonObject
        address["label"]!!.jsonPrimitive.content
    }

    override val coordinates: Coordinates by lazy {
        val position = jsonObject["position"]!!.jsonObject
        LiteralCoordinates("${position["lat"]!!},${position["lng"]!!}")
    }

    constructor(jsonElement: JsonElement) : this(jsonElement.jsonObject)

    override fun equals(other: Any?): Boolean {
        if (other !is HerePlace) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
