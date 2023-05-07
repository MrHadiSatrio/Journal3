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
import com.hadisatrio.apps.kotlin.journal3.moment.Memorable
import com.hadisatrio.apps.kotlin.journal3.moment.MemorablePlace
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables
import com.hadisatrio.libs.kotlin.geography.NullIsland
import com.hadisatrio.libs.kotlin.geography.Place
import okio.FileSystem
import okio.Path
import java.util.UUID

class FilesystemMemorablePlaces(
    private val fileSystem: FileSystem,
    private val path: Path
) : Memorables {

    override fun relate(momentId: Uuid, thing: Any) {
        require(thing is Place) {
            "Can not establish relation to ${thing::class.qualifiedName}"
        }

        val old = relevantTo(momentId).firstOrNull()
        old?.unlink(momentId)
        if (thing == NullIsland) return
        val new = find(thing.id).firstOrNull() ?: remember(thing)
        new.link(momentId)
    }

    override fun find(id: Uuid): Iterable<Memorable> {
        val candidatePath = path / id.toString()
        if (fileSystem.exists(candidatePath)) {
            return listOf(FilesystemMemorablePlace(fileSystem, candidatePath))
        } else {
            return emptyList()
        }
    }

    override fun relevantTo(momentId: UUID): Iterable<Memorable> {
        return filter { it.relevantTo(momentId) }
    }

    override fun iterator(): Iterator<Memorable> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path).map { path -> FilesystemMemorablePlace(fileSystem, path) }.iterator()
    }

    internal fun remember(place: Place): MemorablePlace {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        val candidatePath = path / place.id.toString()
        val candidate = FilesystemMemorablePlace(fileSystem, candidatePath)
        if (!fileSystem.exists(candidatePath)) {
            candidate.updateName(place.name)
            candidate.updateAddress(place.address)
            candidate.update(place.coordinates)
        }
        return candidate
    }
}
