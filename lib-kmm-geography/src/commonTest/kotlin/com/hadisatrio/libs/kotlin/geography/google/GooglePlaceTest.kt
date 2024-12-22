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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test

class GooglePlaceTest {

    private val tagHeuerJson = Json.decodeFromString<JsonObject>(
        """
        {
          "id": "ChIJK_GvElHxaS4R5s2SC8XgNs8",
          "location": {
            "latitude": -6.2245194999999995,
            "longitude": 106.8101801
          },
          "displayName": {
            "text": "TAG Heuer",
            "languageCode": "en"
          },
          "shortFormattedAddress": "Jl. Jenderal Sudirman No.52-53 Unit G - 16A, RT.5/RW.3, Senayan, Kota Jakarta Selatan"
        }
        """.trimIndent()
    )
    private val montblancJson = Json.decodeFromString<JsonObject>(
        """
        {
          "id": "ChIJbe4KGlHxaS4RM7aVbyc15cI",
          "location": {
            "latitude": -6.2245327,
            "longitude": 106.8100817
          },
          "displayName": {
            "text": "Montblanc",
            "languageCode": "en"
          },
          "shortFormattedAddress": "Pacific Place Ground Floor #27-28, Jl. Jenderal Sudirman No.52-53, RT.5/RW.3, Senayan, South Jakarta City"
        }
        """.trimIndent()
    )
    private val tagHeuer = GooglePlace(tagHeuerJson)
    private val otherTagHeuer = GooglePlace(tagHeuerJson)
    private val montblanc = GooglePlace(montblancJson)

    @Test
    fun `Infers ID from the Google ID consistently`() {
        val tageHeuerJsonId = tagHeuerJson.getValue("id").jsonPrimitive.content
        tagHeuer.id.shouldBe(Uuid.nameUUIDFromBytes(tageHeuerJsonId.toByteArray()))
        tagHeuer.id.shouldBe(tagHeuer.id)
        tagHeuer.id.shouldBe(otherTagHeuer.id)
    }

    @Test
    fun `Infers name from displayName`() {
        val tagHeuerJsonName = tagHeuerJson.getValue("displayName").jsonObject.getValue("text").jsonPrimitive.content
        val montblancJsonName = montblancJson.getValue("displayName").jsonObject.getValue("text").jsonPrimitive.content
        tagHeuer.name.shouldBe(tagHeuerJsonName)
        montblanc.name.shouldBe(montblancJsonName)
    }

    @Test
    fun `Infers address from shortFormattedAddress`() {
        tagHeuer.address.shouldBe(tagHeuerJson.getValue("shortFormattedAddress").jsonPrimitive.content)
        montblanc.address.shouldBe(montblancJson.getValue("shortFormattedAddress").jsonPrimitive.content)
    }

    @Test
    fun `Checks for equality based on the Google ID`() {
        tagHeuer.shouldBe(tagHeuer)
        tagHeuer.shouldBe(otherTagHeuer)
        tagHeuer.shouldNotBe(tagHeuerJson)
        tagHeuer.shouldNotBe(montblanc)
        tagHeuer.hashCode().shouldBe(tagHeuer.hashCode())
        tagHeuer.hashCode().shouldBe(otherTagHeuer.hashCode())
        tagHeuer.hashCode().shouldNotBe(montblanc.hashCode())
    }
}
