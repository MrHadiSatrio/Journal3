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
import com.benasher44.uuid.uuidFrom
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables
import com.hadisatrio.apps.kotlin.journal3.moment.Moments
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMoment
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMoments
import com.hadisatrio.apps.kotlin.journal3.story.EditableStory
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.json.JsonFile
import kotlinx.serialization.json.JsonPrimitive
import okio.FileSystem
import okio.Path

class FilesystemStory(
    private val fileSystem: FileSystem,
    private val directory: Path,
    private val detailsFile: JsonFile,
    private val momentsDirectory: Path,
    private val memorables: Memorables
) : EditableStory {

    override val id: Uuid get() {
        return uuidFrom(directory.name)
    }

    override val title: String get() {
        return detailsFile.getRaw("title") ?: ""
    }

    override val synopsis: TokenableString get() {
        return TokenableString(detailsFile.getRaw("synopsis") ?: return TokenableString.EMPTY)
    }

    override val moments: Moments get() {
        return FilesystemMoments(fileSystem, momentsDirectory, memorables)
    }

    constructor(fileSystem: FileSystem, parentDirectory: Path, id: Uuid, memorables: Memorables) : this(
        fileSystem = fileSystem,
        directory = parentDirectory / id.toString(),
        memorables = memorables
    )

    constructor(fileSystem: FileSystem, directory: Path, memorables: Memorables) : this(
        fileSystem = fileSystem,
        directory = directory,
        detailsFile = JsonFile(fileSystem, directory / "details"),
        momentsDirectory = directory / "moments",
        memorables = memorables
    )

    override fun isNewlyCreated(): Boolean {
        return detailsFile.exists().not()
    }

    override fun update(title: String) {
        fileSystem.createDirectories(dir = directory, mustCreate = false)
        detailsFile.put("title", JsonPrimitive(title))
    }

    override fun update(synopsis: TokenableString) {
        fileSystem.createDirectories(dir = directory, mustCreate = false)
        detailsFile.put("synopsis", JsonPrimitive(synopsis.toString()))
    }

    override fun new(): EditableMoment {
        fileSystem.createDirectories(dir = momentsDirectory, mustCreate = false)
        return FilesystemMoment(fileSystem, momentsDirectory, uuid4(), memorables)
    }

    override fun forget() {
        fileSystem.deleteRecursively(directory)
    }

    override fun compareTo(other: Story): Int {
        return title.compareTo(other.title)
    }
}
