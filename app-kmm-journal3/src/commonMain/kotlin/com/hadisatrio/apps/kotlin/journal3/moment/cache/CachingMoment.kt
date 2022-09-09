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

package com.hadisatrio.apps.kotlin.journal3.moment.cache

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString

class CachingMoment(
    override val id: Uuid,
    override val timestamp: Timestamp,
    override val description: TokenableString,
    override val sentiment: Sentiment,
    override val impliedSentiment: Sentiment,
    private val origin: Moment
) : Moment by origin {

    constructor(origin: Moment) : this(
        origin.id,
        origin.timestamp,
        origin.description,
        origin.sentiment,
        origin.impliedSentiment,
        origin
    )
}
