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

package com.hadisatrio.apps.kotlin.journal3.moment.frontmatter

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoments
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import okio.FileSystem
import okio.Path

class FrontmatterMoments(
    private val fileSystem: FileSystem,
    private val path: Path,
    private val memorables: Memorables
) : EditableMoments {

    override fun new(): EditableMoment {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return FrontmatterMoment(fileSystem, path, uuid4(), memorables)
    }

    override fun count(): Int {
        return if (fileSystem.exists(path).not()) 0 else fileSystem.list(path).size
    }

    override fun find(id: Uuid): Iterable<Moment> {
        val candidatePath = path / "${id}.md"
        return if (fileSystem.exists(candidatePath)) {
            listOf(FrontmatterMoment(fileSystem, candidatePath, memorables))
        } else {
            emptyList()
        }
    }

    override fun mostRecent(): Moment {
        return maxBy { it.timestamp }
    }

    override fun iterator(): Iterator<Moment> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        val paths = fileSystem.list(path).asSequence()
        val moments = paths.map { path -> FrontmatterMoment(fileSystem, path, memorables) }
        val sorted = moments.sortedDescending()
        return sorted.iterator()
    }
}
