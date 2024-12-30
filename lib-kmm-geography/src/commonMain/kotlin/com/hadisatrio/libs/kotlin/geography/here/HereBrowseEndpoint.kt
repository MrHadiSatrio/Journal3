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

import com.hadisatrio.libs.kotlin.collection.PagingIterator
import com.hadisatrio.libs.kotlin.geography.Coordinates
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.clone
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException

internal class HereBrowseEndpoint(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val coordinates: Coordinates,
    private val textQuery: String = "",
    private val onResponse: (Iterable<HerePlace>) -> Unit
) : PagingIterator.Source<HerePlace> {

    private val urlBuilder: URLBuilder by lazy {
        val builder = URLBuilder("https://browse.search.hereapi.com/v1/browse")
        builder.parameters.append("apiKey", apiKey)
        builder.parameters.append("at", coordinates.toString())
        if (textQuery.isNotBlank()) builder.parameters.append("name", textQuery)
        builder
    }

    override fun obtain(page: Int, pageSize: Int): PagingIterator.Page<HerePlace> = runBlocking {
        val urlBuilder = urlBuilder.clone()
        urlBuilder.parameters.append("limit", pageSize.toString())
        urlBuilder.parameters.append("offset", (page * pageSize).toString())
        val response = urlBuilder.buildAndCall().body() as String
        val page = HerePage(response)
        onResponse(page.items)
        return@runBlocking page
    }

    private suspend fun URLBuilder.buildAndCall(): HttpResponse {
        val url = this.build()
        val response = httpClient.get(url)
        if (!response.status.isSuccess()) throw IOException("HTTP ${response.status}: ${response.body<String>()}.")
        return response
    }
}
