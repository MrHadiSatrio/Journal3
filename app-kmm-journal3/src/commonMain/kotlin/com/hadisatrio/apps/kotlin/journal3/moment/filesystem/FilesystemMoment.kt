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
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.MomentfulPlace
import com.hadisatrio.apps.kotlin.journal3.moment.MomentfulPlaces
import com.hadisatrio.apps.kotlin.journal3.sentiment.DumbSentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.NullIsland
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.json.JsonFile
import kotlinx.serialization.json.JsonPrimitive
import okio.FileSystem
import okio.Path

class FilesystemMoment(
    private val file: JsonFile,
    private val places: MomentfulPlaces
) : Moment {

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

    override val place: Place
        get() {
            return places.relevantTo(this).firstOrNull() ?: NullIsland
        }

    constructor(fileSystem: FileSystem, parentDirectory: Path, id: Uuid, places: MomentfulPlaces) : this(
        fileSystem = fileSystem,
        path = parentDirectory / id.toString(),
        places = places
    )

    constructor(fileSystem: FileSystem, path: Path, places: MomentfulPlaces) : this(
        file = JsonFile(fileSystem, path),
        places = places
    )

    override fun update(timestamp: Timestamp) {
        file.put("timestamp", JsonPrimitive(timestamp.toString()))
    }

    override fun update(description: TokenableString) {
        file.put("description", JsonPrimitive(description.toString()))
    }

    override fun update(sentiment: Sentiment) {
        file.put("sentiment", JsonPrimitive(sentiment.value))
    }

    override fun update(place: Place) {
        val old = this.place as? MomentfulPlace
        val new = places.find(place.id).firstOrNull() ?: places.remember(place)
        old?.unlink(this)
        if (place == NullIsland) return
        new.link(this)
    }

    override fun forget() {
        file.delete()
    }

    override fun compareTo(other: Moment): Int {
        return timestamp.compareTo(other.timestamp)
    }
}
