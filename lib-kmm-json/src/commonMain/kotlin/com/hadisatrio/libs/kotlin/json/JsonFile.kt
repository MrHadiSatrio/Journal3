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

@file:OptIn(ExperimentalSerializationApi::class)

package com.hadisatrio.libs.kotlin.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

/**
 * A JSON object persisted as a file on an Okio [FileSystem].
 *
 * The file is created on the first [put] call if it doesn't already exist. Each [put] overwrites
 * the entire file atomically with the updated JSON object.
 *
 * @param fileSystem File system used for reading and writing.
 * @param path Path to the backing JSON file.
 */
class JsonFile(
    private val fileSystem: FileSystem,
    private val path: Path
) {

    val name: String get() = path.name

    /**
     * Writes [element] under [key], replacing any previous value for that key.
     *
     * @param key Key under which to store the element.
     * @param element JSON value to store.
     */
    fun put(key: String, element: JsonElement) {
        val modifiedJson = JsonObject(jsonObject() + (key to element))
        val bufferedSink = fileSystem.sink(path).buffer()
        bufferedSink.use { Json.encodeToBufferedSink(modifiedJson, it) }
    }

    /**
     * Returns the [JsonElement] stored under [key], or `null` if the key is absent.
     *
     * @param key Key to look up.
     */
    fun get(key: String): JsonElement? {
        return jsonObject()[key]
    }

    /**
     * Returns the raw string content of the primitive stored under [key], or `null` if the
     * key is absent or the value is not a [JsonPrimitive].
     *
     * @param key Key to look up.
     */
    fun getRaw(key: String): String? {
        return (get(key) as? JsonPrimitive)?.content
    }

    /**
     * Returns `true` if the backing file exists on the file system.
     */
    fun exists(): Boolean {
        return fileSystem.exists(path)
    }

    /**
     * Deletes the backing file. Does nothing if the file does not exist.
     */
    fun delete() {
        fileSystem.delete(path = path, mustExist = false)
    }

    private fun jsonObject(): JsonObject {
        if (!fileSystem.exists(path)) {
            return Json.parseToJsonElement("{}").jsonObject
        }

        // There's a bug with kotlinx.serialization that prevented emojis from
        // being decoded correctly with Json#decodeToBufferedSink.
        // See https://github.com/Kotlin/kotlinx.serialization/issues/2030.
        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        val jsonElement = Json.parseToJsonElement(fileContent)
        return jsonElement.jsonObject
    }
}
