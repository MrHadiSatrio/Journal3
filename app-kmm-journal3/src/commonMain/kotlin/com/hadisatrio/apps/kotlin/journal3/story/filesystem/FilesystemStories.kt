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

package com.hadisatrio.apps.kotlin.journal3.story.filesystem

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.Story
import okio.FileSystem
import okio.Path

class FilesystemStories(
    private val fileSystem: FileSystem,
    private val path: Path
) : Stories {

    override fun new(): Story {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return FilesystemStory(fileSystem, path, uuid4())
    }

    override fun findStory(id: Uuid): Iterable<Story> {
        val candidatePath = path / id.toString()
        if (fileSystem.exists(candidatePath)) {
            return listOf(FilesystemStory(fileSystem, candidatePath))
        } else {
            return emptyList()
        }
    }

    override fun findMoment(id: Uuid): Iterable<Moment> {
        return flatMap { it.moments.find(id) }
    }

    override fun mostRecentMoment(): Moment {
        val recentMoments = mutableListOf<Moment>()
        forEach { story ->
            if (!story.moments.iterator().hasNext()) return@forEach
            recentMoments.add(story.moments.mostRecent())
        }
        return recentMoments.maxBy { it.timestamp }
    }

    override fun iterator(): Iterator<Story> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path).map { path -> FilesystemStory(fileSystem, path) }.iterator()
    }
}
