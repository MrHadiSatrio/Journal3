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

import com.benasher44.uuid.uuid4
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMoment
import com.hadisatrio.apps.kotlin.journal3.story.RegularPatterns
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.uri.IllegalUriException
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

    override fun findStory(uri: Uri): Iterable<Story> {
        val match = Regex(RegularPatterns.STORY_URI).find(uri.uriString)
            ?: throw IllegalUriException(uri, RegularPatterns.STORY_URI)

        val (idString) = match.destructured
        val candidatePath = path / idString
        return if (fileSystem.exists(candidatePath)) {
            setOf(FilesystemStory(fileSystem, candidatePath))
        } else {
            emptySet()
        }
    }

    override fun findMoments(uri: Uri): Iterable<Moment> {
        val match = Regex(RegularPatterns.MOMENT_URI).find(uri.uriString)
            ?: return findStory(uri).flatMap { it.moments }

        val (storyIdString, momentIdString) = match.destructured
        val candidatePath = path / storyIdString / "moments" / momentIdString
        return if (fileSystem.exists(candidatePath)) {
            setOf(FilesystemMoment(fileSystem, candidatePath))
        } else {
            emptySet()
        }
    }

    override fun iterator(): Iterator<Story> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path).map { path -> FilesystemStory(fileSystem, path) }.iterator()
    }
}
