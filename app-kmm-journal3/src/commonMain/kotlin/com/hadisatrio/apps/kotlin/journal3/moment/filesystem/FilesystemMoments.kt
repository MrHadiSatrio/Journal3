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
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.Moments
import okio.FileSystem
import okio.Path

class FilesystemMoments(
    private val fileSystem: FileSystem,
    private val path: Path
) : Moments {

    override fun new(): Moment {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return FilesystemMoment(fileSystem, path, uuid4())
    }

    override fun find(id: Uuid): Iterable<Moment> {
        val candidatePath = path / id.toString()
        if (fileSystem.exists(candidatePath)) {
            return listOf(FilesystemMoment(fileSystem, candidatePath))
        } else {
            return emptyList()
        }
    }

    override fun iterator(): Iterator<Moment> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path).map { path -> FilesystemMoment(fileSystem, path) }.iterator()
    }
}
