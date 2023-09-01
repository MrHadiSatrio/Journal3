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

package com.hadisatrio.apps.kotlin.journal3.story

import com.benasher44.uuid.Uuid
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.Place

class EditableMomentInStory(
    private val storyId: Uuid,
    private val targetId: Uuid,
    private val stories: Stories
) : EditableMoment {

    private val origin: EditableMoment by lazy {
        if (targetId.isValid()) {
            stories.findMoment(targetId).first() as EditableMoment
        } else {
            val story = stories.findStory(storyId).first() as EditableStory
            story.new()
        }
    }

    override val id: Uuid get() = origin.id
    override val timestamp: Timestamp get() = origin.timestamp
    override val description: TokenableString get() = origin.description
    override val sentiment: Sentiment get() = origin.sentiment
    override val place: Place get() = origin.place
    override val attachments: Iterable<Uri> get() = origin.attachments

    override fun isNewlyCreated(): Boolean = origin.isNewlyCreated()
    override fun update(timestamp: Timestamp) = origin.update(timestamp)
    override fun update(description: TokenableString) = origin.update(description)
    override fun update(sentiment: Sentiment) = origin.update(sentiment)
    override fun update(place: Place) = origin.update(place)
    override fun update(attachments: Iterable<Uri>) = origin.update(attachments)
    override fun compareTo(other: Moment): Int = origin.compareTo(other)
    override fun forget() = origin.forget()

    private fun Uuid.isValid(): Boolean {
        return this.toString() != "00000000-0000-0000-0000-000000000000"
    }
}
