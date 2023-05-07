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

package com.hadisatrio.apps.kotlin.journal3.moment.filesystem

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.hadisatrio.apps.kotlin.journal3.moment.MemorablePlace
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.json.JsonFile
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import okio.FileSystem
import okio.Path

class FilesystemMemorablePlace(
    private val file: JsonFile
) : MemorablePlace {

    override val id: Uuid get() {
        return uuidFrom(file.name)
    }

    override val label: String get() {
        return file.getRaw("label") ?: name
    }

    override val name: String get() {
        return file.getRaw("name")!!
    }

    override val address: String get() {
        return file.getRaw("address")!!
    }

    override val coordinates: Coordinates get() {
        return LiteralCoordinates(file.getRaw("coordinates")!!)
    }

    private val momentIds: JsonArray get() {
        return (file.get("moment_ids") ?: JsonArray(emptyList())).jsonArray
    }

    constructor(filesystem: FileSystem, path: Path) : this(JsonFile(filesystem, path))

    override fun updateLabel(label: String) {
        file.put("label", JsonPrimitive(label))
    }

    override fun updateName(name: String) {
        file.put("name", JsonPrimitive(name))
    }

    override fun updateAddress(address: String) {
        file.put("address", JsonPrimitive(address))
    }

    override fun update(coordinates: Coordinates) {
        file.put("coordinates", JsonPrimitive(coordinates.toString()))
    }

    override fun link(momentId: Uuid) {
        val idPrimitive = JsonPrimitive(momentId.toString())
        val newIds = buildJsonArray { momentIds.forEach(::add); add(idPrimitive) }
        file.put("moment_ids", newIds)
    }

    override fun unlink(momentId: Uuid) {
        val idPrimitive = JsonPrimitive(momentId.toString())
        val newIds = buildJsonArray { momentIds.filterNot { it == idPrimitive }.forEach(::add) }
        file.put("moment_ids", newIds)
    }

    override fun relevantTo(momentId: Uuid): Boolean {
        val idPrimitive = JsonPrimitive(momentId.toString())
        return momentIds.contains(idPrimitive)
    }
}
