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
import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromParts
import com.hadisatrio.apps.kotlin.journal3.moment.MemorableFile
import com.hadisatrio.libs.kotlin.json.JsonFile
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import okio.FileSystem
import okio.Path

class FilesystemMemorableFile(
    private val fileSystem: FileSystem,
    private val path: Path
) : MemorableFile {

    private val attrFile: JsonFile by lazy {
        val name = ".attr_" + path.name
        val attrPath = path.parent!!.resolve(name)
        JsonFile(fileSystem, attrPath)
    }

    private val momentIds: JsonArray
        get() {
            return (attrFile.get("moment_ids") ?: JsonArray(emptyList())).jsonArray
        }

    override val id: Uuid
        get() {
            return uuidFrom(path.name)
        }

    override val uri: Uri
        get() {
            return Uri.fromParts(scheme = "file", path = path.toString())
        }

    override fun link(momentId: Uuid) {
        val idPrimitive = JsonPrimitive(momentId.toString())
        val newIds = buildJsonArray { momentIds.forEach(::add); add(idPrimitive) }
        attrFile.put("moment_ids", newIds)
    }

    override fun unlink(momentId: Uuid) {
        val idPrimitive = JsonPrimitive(momentId.toString())
        val newIds = buildJsonArray { momentIds.filterNot { it == idPrimitive }.forEach(::add) }
        if (newIds.isEmpty()) delete() else attrFile.put("moment_ids", newIds)
    }

    override fun relevantTo(momentId: Uuid): Boolean {
        val idPrimitive = JsonPrimitive(momentId.toString())
        return momentIds.contains(idPrimitive)
    }

    private fun delete() {
        attrFile.delete()
        fileSystem.delete(path)
    }
}
