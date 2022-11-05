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

import com.hadisatrio.apps.kotlin.journal3.geography.Place
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.Url
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.util.concurrent.atomic.AtomicInteger

class HerePlaceIterator(
    private val url: Url,
    private val httpClient: HttpClient
) : Iterator<Place> {

    private val jsonArray: JsonArray by lazy {
        runBlocking {
            val response = httpClient.get(url)
            if (!response.status.isSuccess()) throw IOException(response.status.toString())
            val responseObject = Json.parseToJsonElement(response.body()).jsonObject
            responseObject["items"]!!.jsonArray
        }
    }

    private val currIndex = AtomicInteger(0)

    override fun hasNext(): Boolean {
        return currIndex.get() <= jsonArray.lastIndex
    }

    override fun next(): Place {
        if (!hasNext()) throw NoSuchElementException()
        val jsonElement = jsonArray[currIndex.getAndIncrement()]
        return HerePlace(jsonElement)
    }
}
