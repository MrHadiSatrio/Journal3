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
import com.benasher44.uuid.uuidFrom
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.json.JsonFile
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.sentiment.DumbSentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import kotlinx.serialization.json.JsonPrimitive
import okio.FileSystem
import okio.Path

class FilesystemMoment : Moment {

    private val file: JsonFile

    override val id: Uuid get() {
        return uuidFrom(file.name)
    }

    override val timestamp: Timestamp get() {
        return Timestamp(file.getRaw("timestamp") ?: return Timestamp.DEFAULT)
    }

    override val description: TokenableString get() {
        return TokenableString(file.getRaw("description") ?: return TokenableString.EMPTY)
    }

    override val sentiment: Sentiment get() {
        return Sentiment(file.getRaw("sentiment") ?: return Sentiment.DEFAULT)
    }

    override val impliedSentiment: Sentiment get() {
        return DumbSentimentAnalyst.analyze(description.toString())
    }

    constructor(fileSystem: FileSystem, parentDirectory: Path, id: Uuid) {
        this.file = JsonFile(fileSystem, parentDirectory / id.toString())
    }

    constructor(fileSystem: FileSystem, file: Path) {
        this.file = JsonFile(fileSystem, file)
    }

    override fun update(timestamp: Timestamp) {
        file.put("timestamp", JsonPrimitive(timestamp.toString()))
    }

    override fun update(description: TokenableString) {
        file.put("description", JsonPrimitive(description.toString()))
    }

    override fun update(sentiment: Sentiment) {
        file.put("sentiment", JsonPrimitive(sentiment.value))
    }

    override fun forget() {
        file.delete()
    }
}
