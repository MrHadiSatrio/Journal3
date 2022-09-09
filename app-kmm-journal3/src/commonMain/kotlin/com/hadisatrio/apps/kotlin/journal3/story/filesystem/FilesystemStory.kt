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
import com.benasher44.uuid.uuidFrom
import com.hadisatrio.apps.kotlin.journal3.json.JsonFile
import com.hadisatrio.apps.kotlin.journal3.moment.Moments
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMoments
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import kotlinx.serialization.json.JsonPrimitive
import okio.FileSystem
import okio.Path

class FilesystemStory : Story {

    private val fileSystem: FileSystem
    private val directory: Path
    private val detailsFile: JsonFile
    private val momentsDirectory: Path

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
        return FilesystemMoments(fileSystem, momentsDirectory)
    }

    constructor(fileSystem: FileSystem, parentDirectory: Path, id: Uuid) {
        this.fileSystem = fileSystem
        this.directory = parentDirectory / id.toString()
        this.detailsFile = JsonFile(fileSystem, directory / "details")
        this.momentsDirectory = directory / "moments"
    }

    constructor(fileSystem: FileSystem, directory: Path) {
        this.fileSystem = fileSystem
        this.directory = directory
        this.detailsFile = JsonFile(fileSystem, directory / "details")
        this.momentsDirectory = directory / "moments"
    }

    override fun update(title: String) {
        fileSystem.createDirectories(dir = directory, mustCreate = false)
        detailsFile.put("title", JsonPrimitive(title))
    }

    override fun update(synopsis: TokenableString) {
        fileSystem.createDirectories(dir = directory, mustCreate = false)
        detailsFile.put("synopsis", JsonPrimitive(synopsis.toString()))
    }

    override fun forget() {
        fileSystem.deleteRecursively(directory)
    }
}
