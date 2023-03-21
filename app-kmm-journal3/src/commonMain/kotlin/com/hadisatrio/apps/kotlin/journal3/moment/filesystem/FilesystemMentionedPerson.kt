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
import com.hadisatrio.apps.kotlin.journal3.moment.MentionedPerson
import com.hadisatrio.apps.kotlin.journal3.token.Token
import com.hadisatrio.libs.kotlin.json.JsonFile
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import okio.FileSystem
import okio.Path

class FilesystemMentionedPerson(
    private val file: JsonFile
) : MentionedPerson {

    override val id: Uuid get() {
        return uuidFrom(file.name)
    }

    override val slug: Token get() {
        return Token("@${file.getRaw("slug")!!}")
    }

    override val name: String get() {
        return file.getRaw("name") ?: slug.toString()
    }

    private val momentIds: JsonArray get() {
        return (file.get("moment_ids") ?: JsonArray(emptyList())).jsonArray
    }

    constructor(filesystem: FileSystem, path: Path) : this(JsonFile(filesystem, path))

    override fun update(name: String) {
        file.put("name", JsonPrimitive(name))
    }

    override fun link(momentId: Uuid) {
        val idPrimitive = JsonPrimitive(momentId.toString())
        val newIds = buildJsonArray { momentIds.forEach(::add); add(idPrimitive) }
        file.put("moment_ids", newIds)
    }

    override fun unlink(momentId: Uuid) {
        val idPrimitive = JsonPrimitive(momentId.toString())
        val newIds = buildJsonArray { momentIds.filterNot { it == idPrimitive }.forEach(::add) }
        if (newIds.isEmpty()) file.delete() else file.put("moment_ids", newIds)
    }

    override fun relevantTo(momentId: Uuid): Boolean {
        val idPrimitive = JsonPrimitive(momentId.toString())
        return momentIds.contains(idPrimitive)
    }

    internal fun update(slug: Token) {
        file.put("slug", JsonPrimitive(slug.toString().removePrefix("@")))
    }
}
