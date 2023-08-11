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

class UpdateDeferringMoment(
    private val origin: EditableMoment
) : EditableMoment by origin {

    private var timestampInEdit: Timestamp = origin.timestamp
    private var descriptionInEdit: TokenableString = origin.description
    private var sentimentInEdit: Sentiment = origin.sentiment
    private var placeInEdit: Place = origin.place
    private var attachmentsInEdit: Iterable<Uri> = origin.attachments

    override val timestamp: Timestamp get() = timestampInEdit
    override val description: TokenableString get() = descriptionInEdit
    override val sentiment: Sentiment get() = sentimentInEdit
    override val place: Place get() = placeInEdit
    override val attachments: Iterable<Uri> get() = attachmentsInEdit

    override fun update(timestamp: Timestamp) {
        timestampInEdit = timestamp
    }

    override fun update(description: TokenableString) {
        descriptionInEdit = description
    }

    override fun update(sentiment: Sentiment) {
        sentimentInEdit = sentiment
    }

    override fun update(place: Place) {
        placeInEdit = place
    }

    override fun update(attachments: Iterable<Uri>) {
        attachmentsInEdit = attachments.toList()
    }

    override fun compareTo(other: Moment): Int {
        return timestampInEdit.compareTo(other.timestamp)
    }

    fun updatesMade(): Boolean {
        return timestampInEdit != origin.timestamp ||
            descriptionInEdit != origin.description ||
            sentimentInEdit != origin.sentiment ||
            placeInEdit != origin.place ||
            attachmentsInEdit != origin.attachments
    }

    fun commit() {
        origin.update(timestampInEdit)
        origin.update(descriptionInEdit)
        origin.update(sentimentInEdit)
        origin.update(placeInEdit)
        origin.update(attachmentsInEdit)
    }
}
