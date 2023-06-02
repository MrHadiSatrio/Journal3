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
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.moment.Memorable
import com.hadisatrio.apps.kotlin.journal3.moment.MemorableFile
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables
import com.hadisatrio.libs.kotlin.io.Sources
import okio.FileSystem
import okio.Path
import okio.buffer

class FilesystemMemorableFiles(
    private val sources: Sources,
    private val fileSystem: FileSystem,
    private val path: Path
) : Memorables {

    override fun relate(momentId: Uuid, thing: Any) {
        val paths = normalize(thing)
        val idToPath = paths.associateBy { it.toContentAwareUuid() }

        val oldIds = relevantTo(momentId).map { (it as MemorableFile).id }.toSet()
        val newIds = idToPath.keys.toSet()

        // If it exists in old, but no longer in new, means it's "unmentioned".
        (oldIds - newIds).asSequence()
            .flatMap { find(it) }
            .forEach { it.unlink(momentId) }

        // If it exists in new, but not in old, then it's a new attachment.
        // It can either be for an existing/equivalent file, or a new one.
        (newIds - oldIds).asSequence()
            .map { find(it).firstOrNull() ?: remember(it, idToPath[it]!!) }
            .forEach { it.link(momentId) }
    }

    override fun find(id: Uuid): Iterable<Memorable> {
        val candidatePath = path / id.toString()
        if (fileSystem.exists(candidatePath)) {
            return listOf(FilesystemMemorableFile(fileSystem, candidatePath))
        } else {
            return emptyList()
        }
    }

    override fun relevantTo(momentId: Uuid): Iterable<Memorable> {
        return filter { it.relevantTo(momentId) }
    }

    override fun iterator(): Iterator<Memorable> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path)
            .filterNot { it.name.startsWith('.') }
            .map { path -> FilesystemMemorableFile(fileSystem, path) }
            .iterator()
    }

    private fun normalize(thing: Any): Iterable<Uri> {
        val asUris = thing.tryCast<Iterable<Uri>>()
        if (asUris != null) return asUris
        val asUri = thing.tryCast<Uri>()
        if (asUri != null) return listOf(asUri)
        throw IllegalArgumentException("Could not establish relation to ${thing::class.qualifiedName}.")
    }

    /**
     * A function that would convert Path into a UUID in a content-aware manner.
     * Two files would have the same UUID if their content is the same, even if
     * their names are different.
     *
     * This is done through the following steps:
     *  1. Read the file as a byte string through a buffered stream.
     *  2. Calculate the SHA-256 hash of the byte string.
     *  3. Generate a UUID from the MD5 hash.
     */
    private fun Uri.toContentAwareUuid(): Uuid {
        return sources.open(this).buffer().use { source ->
            val content = source.readByteString()
            Uuid.nameUUIDFromBytes(content.md5().toByteArray())
        }
    }

    private fun remember(id: Uuid, uri: Uri): MemorableFile {
        val desiredPath = this.path / id.toString()
        sources.open(uri).buffer().use { sourceBuffer ->
            fileSystem.sink(desiredPath).buffer().use { sinkBuffer ->
                sinkBuffer.writeAll(sourceBuffer)
            }
        }
        return FilesystemMemorableFile(fileSystem, desiredPath)
    }

    private inline fun <reified T> Any.tryCast(): T? {
        return if (this is T) this else null
    }
}
