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

import com.hadisatrio.libs.kotlin.collection.PagingIterator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class GooglePlacesPage(
    private val jsonObject: JsonObject
) : PagingIterator.Page<GooglePlace> {

    override val items: List<GooglePlace> by lazy {
        val responseArray = jsonObject.getValue("places").jsonArray
        responseArray.map { GooglePlace(it) }
    }

    override val hasNext: Boolean by lazy {
        nextPageToken.isNotBlank()
    }

    internal val nextPageToken: String by lazy {
        jsonObject.getOrDefault("nextPageToken", JsonPrimitive("")).jsonPrimitive.content
    }

    constructor(json: String) : this(Json.parseToJsonElement(json).jsonObject)
}
