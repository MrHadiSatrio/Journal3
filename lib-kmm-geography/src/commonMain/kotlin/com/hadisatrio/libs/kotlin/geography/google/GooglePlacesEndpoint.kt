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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import java.io.IOException

internal abstract class GooglePlacesEndpoint(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val onResponse: (Iterable<GooglePlace>) -> Unit
) : PagingIterator.Source<GooglePlace> {

    private val urlBuilder: URLBuilder by lazy {
        URLBuilder("https://places.googleapis.com/v1")
            .appendEncodedPathSegments(path)
    }

    internal abstract val path: String
    internal abstract val supportsPagination: Boolean
    internal abstract fun requestBody(pageToken: String, pageSize: Int): String

    private var nextPageToken: String = ""

    override fun obtain(page: Int, pageSize: Int): PagingIterator.Page<GooglePlace> = runBlocking {
        val request = requestBody(nextPageToken, pageSize)
        val response = urlBuilder.buildAndCall(request).body() as String
        val page = GooglePlacesPage(response)
        nextPageToken = page.nextPageToken
        onResponse(page.items)
        return@runBlocking page
    }

    private suspend fun URLBuilder.buildAndCall(body: String): HttpResponse {
        val url = this.build()
        val fieldMasks = FIELD_MASKS.also { if (supportsPagination) it.plus("nextPageToken") }
        val response = httpClient.post(url) {
            header("X-Goog-Api-Key", apiKey)
            header("X-Goog-FieldMask", fieldMasks.joinToString(","))
            setBody(body)
        }
        if (!response.status.isSuccess()) {
            throw IOException("HTTP ${response.status}: ${response.body<String>()}.")
        }
        return response
    }

    companion object {
        val FIELD_MASKS = setOf(
            "places.id",
            "places.displayName",
            "places.shortFormattedAddress",
            "places.location"
        )
    }
}
