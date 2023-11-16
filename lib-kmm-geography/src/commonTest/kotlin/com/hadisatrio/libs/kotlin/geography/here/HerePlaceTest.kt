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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test

class HerePlaceTest {

    private val lifemarkStreetJson = Json.decodeFromString<JsonObject>(
        """
            {
              "title": "Lifemark Rd, Redwood City, CA 94062, United States",
              "id": "here:af:street:fnY9KMvsOadjmoSObWB8oB",
              "language": "en",
              "resultType": "street",
              "address": { "label": "Lifemark Rd, Redwood City, CA 94062, United States" },
              "position": { "lat": 37.53019, "lng": -122.39263 }
            }
        """.trimIndent()
    )
    private val anglersPlaceJson = Json.decodeFromString<JsonObject>(
        """
            {
              "title": "Anglers",
              "id": "here:pds:place:8409q8vt-e7476fd27a0e4238b0850c2823a6d415",
              "language": "en",
              "resultType": "place",
              "address": { "label": "Anglers, Granada Blvd, Moon Bay, CA 94019, United States" },
              "position": { "lat": 37.53873, "lng": -122.44575 }
            }
        """.trimIndent()
    )
    private val lifemarkStreet = HerePlace(lifemarkStreetJson)
    private val otherLifemarkStreet = HerePlace(lifemarkStreetJson)
    private val anglersPlace = HerePlace(anglersPlaceJson)

    @Test
    fun `Infers ID from the Here ID consistently`() {
        val lifemarkStreetJsonId = lifemarkStreetJson["id"]!!.jsonPrimitive.content
        lifemarkStreet.id.shouldBe(Uuid.nameUUIDFromBytes(lifemarkStreetJsonId.toByteArray()))
        lifemarkStreet.id.shouldBe(lifemarkStreet.id)
        lifemarkStreet.id.shouldBe(otherLifemarkStreet.id)
    }

    @Test
    fun `Infers name from the title`() {
        val lifemarkStreetJsonTitle = lifemarkStreetJson["title"]!!.jsonPrimitive.content
        val anglersPlaceJsonTitle = anglersPlaceJson["title"]!!.jsonPrimitive.content
        lifemarkStreet.name.shouldBe(lifemarkStreetJsonTitle)
        anglersPlace.name.shouldBe(anglersPlaceJsonTitle)
    }

    @Test
    fun `Infers address from the address label`() {
        lifemarkStreet.address.shouldBe(lifemarkStreetJson["address"]!!.jsonObject["label"]!!.jsonPrimitive.content)
        anglersPlace.address.shouldBe(anglersPlaceJson["address"]!!.jsonObject["label"]!!.jsonPrimitive.content)
    }

    @Test
    fun `Checks for equality based on the Here ID`() {
        lifemarkStreet.shouldBe(lifemarkStreet)
        lifemarkStreet.shouldBe(otherLifemarkStreet)
        lifemarkStreet.shouldNotBe(lifemarkStreetJson)
        lifemarkStreet.shouldNotBe(anglersPlace)
        lifemarkStreet.hashCode().shouldBe(lifemarkStreet.hashCode())
        lifemarkStreet.hashCode().shouldBe(otherLifemarkStreet.hashCode())
        lifemarkStreet.hashCode().shouldNotBe(anglersPlace.hashCode())
    }
}
