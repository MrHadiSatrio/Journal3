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
import com.benasher44.uuid.uuidFrom
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.datetime.UnixEpoch
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.moment.MemorableFile
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.frontmatter.FrontmatterFile
import com.hadisatrio.libs.kotlin.geography.NullIsland
import com.hadisatrio.libs.kotlin.geography.Place
import okio.FileSystem
import okio.Path

@Suppress("TooManyFunctions")
class FrontmatterMoment(
    private val file: FrontmatterFile,
    private val memorables: Memorables
) : EditableMoment {

    override val id: Uuid get() {
        return uuidFrom(file.name.removeSuffix(".md"))
    }

    override val timestamp: Timestamp get() {
        return LiteralTimestamp(file.get("timestamp") ?: return UnixEpoch)
    }

    override val description: TokenableString get() {
        return TokenableString(file.body().ifEmpty { return TokenableString.EMPTY })
    }

    override val sentiment: Sentiment get() {
        return Sentiment(file.get("sentiment") ?: return Sentiment.DEFAULT)
    }

    override val place: Place get() {
        val relevantReferables = memorables.relevantTo(this.id)
        return relevantReferables.filterIsInstance<Place>().firstOrNull() ?: NullIsland
    }

    override val attachments: Iterable<Uri> get() {
        val relevantItems = memorables.relevantTo(this.id)
        return relevantItems.asSequence().filterIsInstance<MemorableFile>().map { it.uri }.toList()
    }

    override val isNotable: Boolean get() {
        return file.get("is_notable")?.toBooleanStrictOrNull() ?: false
    }

    constructor(fileSystem: FileSystem, parentDirectory: Path, id: Uuid, memorables: Memorables) : this(
        fileSystem = fileSystem,
        path = parentDirectory / "${id}.md",
        memorables = memorables
    )

    constructor(fileSystem: FileSystem, path: Path, memorables: Memorables) : this(
        file = FrontmatterFile(fileSystem, path),
        memorables = memorables
    )

    override fun update(timestamp: Timestamp) {
        file.put("timestamp", timestamp.toString())
    }

    override fun update(description: TokenableString) {
        memorables.relate(id, description)
        file.updateBody(description.toString())
    }

    override fun update(sentiment: Sentiment) {
        file.put("sentiment", sentiment.value.toString())
    }

    override fun update(place: Place) {
        memorables.relate(id, place)
    }

    override fun update(attachments: Iterable<Uri>) {
        memorables.relate(id, attachments)
    }

    override fun update(isNotable: Boolean) {
        file.put("is_notable", isNotable.toString())
    }

    override fun isNewlyCreated(): Boolean {
        return file.exists().not()
    }

    override fun updatesMade(): Boolean {
        return true
    }

    override fun commit() {
        // We are directly committing any updates, so no further action required.
    }

    override fun forget() {
        file.delete()
    }

    override fun compareTo(other: Moment): Int {
        return timestamp.compareTo(other.timestamp)
    }
}
