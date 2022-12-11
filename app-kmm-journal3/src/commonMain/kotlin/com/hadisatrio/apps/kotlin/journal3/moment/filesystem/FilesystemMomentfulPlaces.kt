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
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.MomentfulPlace
import com.hadisatrio.apps.kotlin.journal3.moment.MomentfulPlaces
import com.hadisatrio.libs.kotlin.geography.Place
import okio.FileSystem
import okio.Path

class FilesystemMomentfulPlaces(
    private val fileSystem: FileSystem,
    private val path: Path
) : MomentfulPlaces {

    override fun remember(place: Place): MomentfulPlace {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        val candidatePath = path / place.id.toString()
        val candidate = FilesystemMomentfulPlace(fileSystem, candidatePath)
        if (!fileSystem.exists(candidatePath)) {
            candidate.updateName(place.name)
            candidate.updateAddress(place.address)
            candidate.update(place.coordinates)
        }
        return candidate
    }

    override fun find(id: Uuid): Iterable<MomentfulPlace> {
        val candidatePath = path / id.toString()
        if (fileSystem.exists(candidatePath)) {
            return listOf(FilesystemMomentfulPlace(fileSystem, candidatePath))
        } else {
            return emptyList()
        }
    }

    override fun relevantTo(moment: Moment): Iterable<MomentfulPlace> {
        return filter { it.relevantTo(moment) }
    }

    override fun iterator(): Iterator<MomentfulPlace> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path).map { path -> FilesystemMomentfulPlace(fileSystem, path) }.iterator()
    }
}
