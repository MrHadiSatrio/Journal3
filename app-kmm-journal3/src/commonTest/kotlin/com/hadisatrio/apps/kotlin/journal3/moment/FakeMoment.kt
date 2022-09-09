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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString

class FakeMoment(
    override val id: Uuid,
    private val group: MutableList<Moment>
) : Moment {

    private var isForgotten: Boolean = false

    override var timestamp: Timestamp = Timestamp.DEFAULT
        private set
    override var description: TokenableString = TokenableString.EMPTY
        private set
    override var sentiment: Sentiment = Sentiment.DEFAULT
        private set
    override var impliedSentiment: Sentiment = Sentiment.DEFAULT
        private set

    override fun update(timestamp: Timestamp) {
        require(!isForgotten) { "This moment has already been forgotten." }
        this.timestamp = timestamp
    }

    override fun update(description: TokenableString) {
        require(!isForgotten) { "This moment has already been forgotten." }
        this.description = description
    }

    override fun update(sentiment: Sentiment) {
        require(!isForgotten) { "This moment has already been forgotten." }
        this.sentiment = sentiment
    }

    override fun forget() {
        isForgotten = true
        group.remove(this)
    }
}
