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

package com.hadisatrio.libs.kotlin.paraphrase

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.IOException

class OpenAiParaphraser(
    private val prompt: String,
    private val apiKey: String,
    private val httpClient: HttpClient
) : Paraphraser {

    private val urlBuilder: URLBuilder by lazy {
        URLBuilder("https://api.openai.com/v1/chat/completions")
    }

    override fun paraphrase(text: String): String {
        val messages = mutableListOf<JsonElement>()
        messages.add(message("system", prompt))
        messages.add(message("user", text))

        val body = mutableMapOf<String, JsonElement>()
        body["model"] = JsonPrimitive("gpt-3.5-turbo")
        body["messages"] = JsonArray(messages)

        val requestBuilder = HttpRequestBuilder()
        requestBuilder.url(urlBuilder.build())
        requestBuilder.contentType(ContentType.Application.Json)
        requestBuilder.bearerAuth(apiKey)
        requestBuilder.setBody(JsonObject(body).toString())

        val response = runBlocking { httpClient.post(requestBuilder) }
        if (!response.status.isSuccess()) throw IOException(response.status.toString())
        val responseBody = runBlocking { response.body<String>() }
        val responseObject = Json.parseToJsonElement(responseBody).jsonObject
        val choices = responseObject["choices"]!!.jsonArray
        val message = choices.first().jsonObject["message"]!!.jsonObject

        return message["content"]!!.jsonPrimitive.content
    }

    private fun message(role: String, content: String): JsonObject {
        val message = mutableMapOf<String, JsonElement>()
        message["role"] = JsonPrimitive(role)
        message["content"] = JsonPrimitive(content)
        return JsonObject(message)
    }
}
