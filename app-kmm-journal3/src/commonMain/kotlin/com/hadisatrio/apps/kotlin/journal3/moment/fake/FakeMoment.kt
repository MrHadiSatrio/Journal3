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

package com.hadisatrio.apps.kotlin.journal3.moment.fake

import com.benasher44.uuid.Uuid
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.datetime.UnixEpoch
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.NullIsland
import com.hadisatrio.libs.kotlin.geography.Place

class FakeMoment(
    override val id: Uuid,
    private val group: MutableList<Moment>
) : EditableMoment {

    private var isForgotten: Boolean = false
    private var isNewlyCreated: Boolean = true

    override var timestamp: Timestamp = UnixEpoch
        private set
    override var description: TokenableString = TokenableString.EMPTY
        private set
    override var sentiment: Sentiment = Sentiment.DEFAULT
        private set
    override var place: Place = NullIsland
        private set
    override var attachments: Iterable<Uri> = mutableListOf()
        private set

    override fun update(timestamp: Timestamp) {
        require(!isForgotten) { "This moment has already been forgotten." }
        isNewlyCreated = false
        this.timestamp = timestamp
    }

    override fun update(description: TokenableString) {
        require(!isForgotten) { "This moment has already been forgotten." }
        isNewlyCreated = false
        this.description = description
    }

    override fun update(sentiment: Sentiment) {
        require(!isForgotten) { "This moment has already been forgotten." }
        isNewlyCreated = false
        this.sentiment = sentiment
    }

    override fun update(place: Place) {
        require(!isForgotten) { "This moment has already been forgotten." }
        isNewlyCreated = false
        this.place = place
    }

    override fun update(attachments: Iterable<Uri>) {
        require(!isForgotten) { "This moment has already been forgotten." }
        isNewlyCreated = false
        this.attachments = attachments
    }

    override fun isNewlyCreated(): Boolean {
        return isNewlyCreated
    }

    override fun forget() {
        isForgotten = true
        group.remove(this)
    }

    override fun compareTo(other: Moment): Int {
        return timestamp.compareTo(other.timestamp)
    }
}
