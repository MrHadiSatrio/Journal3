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
import com.hadisatrio.apps.kotlin.journal3.moment.Memorable
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables
import com.hadisatrio.apps.kotlin.journal3.moment.MentionedPerson
import com.hadisatrio.apps.kotlin.journal3.token.Token
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import okio.FileSystem
import okio.Path

class FilesystemMentionedPeople(
    private val fileSystem: FileSystem,
    private val path: Path
) : Memorables {

    override fun relate(momentId: Uuid, thing: Any) {
        require(thing is TokenableString) {
            "Could not establish relation to ${thing::class.qualifiedName}."
        }

        val old = relevantTo(momentId).map { (it as MentionedPerson).slug }.toSet()
        val new = thing.tokens().filter { it.toString().startsWith('@') }.toSet()

        // If it exists in old, but no longer in new, means it's "unmentioned".
        (old - new).asSequence()
            .flatMap { find(it) }
            .forEach { it.unlink(momentId) }

        // If it exists in new, but not in old, then it's a new mention.
        // It can either be for an existing person, or a new one.
        (new - old).asSequence()
            .map { find(it).firstOrNull() ?: remember(it) }
            .forEach { it.link(momentId) }
    }

    override fun find(id: Uuid): Iterable<Memorable> {
        val candidatePath = path / id.toString()
        if (fileSystem.exists(candidatePath)) {
            return listOf(FilesystemMentionedPerson(fileSystem, candidatePath))
        } else {
            return emptyList()
        }
    }

    private fun find(slug: Token): Iterable<Memorable> {
        return filter { (it as MentionedPerson).slug == slug }
    }

    override fun relevantTo(momentId: Uuid): Iterable<Memorable> {
        return filter { it.relevantTo(momentId) }
    }

    override fun iterator(): Iterator<Memorable> {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        return fileSystem.list(path).map { path -> FilesystemMentionedPerson(fileSystem, path) }
            .iterator()
    }

    internal fun remember(slug: Token): MentionedPerson {
        fileSystem.createDirectories(dir = path, mustCreate = false)
        val candidatePath = path / uuid4().toString()
        val candidate = FilesystemMentionedPerson(fileSystem, candidatePath)
        candidate.update(slug)
        candidate.update(slug.toString())
        return candidate
    }
}
