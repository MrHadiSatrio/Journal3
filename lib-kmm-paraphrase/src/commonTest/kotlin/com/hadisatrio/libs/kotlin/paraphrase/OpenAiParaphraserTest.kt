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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import kotlin.test.Test

class OpenAiParaphraserTest {

    private val prompt = "You're a helpful assistant."
    private val apiKey = "wCHWXm4g%Nkg@79z8bMzkg@79z8bMz"
    private val httpClientEngine = clientEngine(shouldFail = false)
    private val paraphraser = OpenAiParaphraser(
        prompt = prompt,
        apiKey = apiKey,
        httpClient = HttpClient(httpClientEngine)
    )

    @Test
    fun `Paraphrases the given text according to the server response`() {
        paraphraser.paraphrase("foo").shouldBe("Bar.")
    }

    @Test
    fun `Throws IOException when server is unable to respond`() {
        val paraphraser = OpenAiParaphraser(
            prompt = prompt,
            apiKey = apiKey,
            httpClient = HttpClient(clientEngine(shouldFail = true))
        )

        shouldThrow<IOException> { paraphraser.paraphrase("foo") }
    }

    private fun clientEngine(shouldFail: Boolean): MockEngine {
        return MockEngine { request ->
            val url = request.url
            url.toString().shouldStartWith("https://api.openai.com/v1/chat/completions")
            request.headers["Authorization"].shouldBe("Bearer $apiKey")
            request.body.contentType.shouldBe(ContentType.Application.Json)

            if (shouldFail) {
                respondError(HttpStatusCode.InternalServerError)
            } else {
                respondOk(
                    """
                      {
                        "id": "chatcmpl-7qlXeGmXtKxrB9VqQ9l6hihZNjvfB",
                        "object": "chat.completion",
                        "created": 1692810162,
                        "model": "gpt-3.5-turbo-0613",
                        "choices": [
                          {
                            "index": 0,
                            "message": {
                              "role": "assistant",
                              "content": "Bar."
                            },
                            "finish_reason": "stop"
                          }
                        ],
                        "usage": {
                          "prompt_tokens": 80,
                          "completion_tokens": 44,
                          "total_tokens": 124
                        }
                      }
                    """.trimIndent()
                )
            }
        }
    }
}
