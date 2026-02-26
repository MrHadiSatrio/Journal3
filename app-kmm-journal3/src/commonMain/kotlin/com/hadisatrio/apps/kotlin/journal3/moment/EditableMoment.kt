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

import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.Place

/**
 * A [Moment] whose fields can be mutated and persisted via [commit].
 */
interface EditableMoment : Moment {

    /**
     * Returns `true` if this moment was just created and has not yet been committed.
     */
    fun isNewlyCreated(): Boolean

    /**
     * Stages a new [timestamp] for this moment.
     */
    fun update(timestamp: Timestamp)

    /**
     * Stages a new [description] for this moment.
     */
    fun update(description: TokenableString)

    /**
     * Stages a new [sentiment] score for this moment.
     */
    fun update(sentiment: Sentiment)

    /**
     * Stages a new [place] for this moment.
     */
    fun update(place: Place)

    /**
     * Stages a new set of media [attachments] for this moment.
     */
    fun update(attachments: Iterable<Uri>)

    /**
     * Stages a new notability flag for this moment.
     */
    fun update(isNotable: Boolean)

    /**
     * Returns `true` if any staged updates differ from the currently persisted values.
     */
    fun updatesMade(): Boolean

    /**
     * Persists all staged updates to the backing store.
     */
    fun commit()
}
