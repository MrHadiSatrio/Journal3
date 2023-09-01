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

import com.benasher44.uuid.uuidFrom
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.test.Test

class EditableMomentInStoryTest {

    private val stories = SelfPopulatingStories(1, 1, FakeStories())
    private val story = stories.first() as EditableStory
    private val moment = story.moments.first() as EditableMoment

    @Test
    fun `Finds and delegates to an existing moment given a valid target ID`() {
        val editableMoment = EditableMomentInStory(
            storyId = story.id,
            targetId = moment.id,
            stories = stories
        )
        editableMoment.id.shouldBe(moment.id)
        editableMoment.timestamp.shouldBe(moment.timestamp)
        editableMoment.description.shouldBe(moment.description)
        editableMoment.sentiment.shouldBe(moment.sentiment)
        editableMoment.place.shouldBe(moment.place)
        editableMoment.attachments.shouldBe(moment.attachments)
        val newTimestamp = LiteralTimestamp(61092L)
        editableMoment.update(newTimestamp)
        moment.timestamp.shouldBe(newTimestamp)
        val newDescription = TokenableString("Foo")
        editableMoment.update(newDescription)
        moment.description.shouldBe(newDescription)
        val newSentiment = Sentiment(0.61092F)
        editableMoment.update(newSentiment)
        moment.sentiment.shouldBe(newSentiment)
        val place = FakePlace()
        editableMoment.update(place)
        moment.place.shouldBe(place)
        val uris = listOf(mockk<Uri>())
        editableMoment.update(uris)
        moment.attachments.shouldBe(uris)
    }

    @Test
    fun `Delegates to a new moment given an empty target ID`() {
        val editableMoment = EditableMomentInStory(
            storyId = story.id,
            targetId = uuidFrom("00000000-0000-0000-0000-000000000000"),
            stories = stories
        )
        editableMoment.id
        stories.moments.count().shouldBe(2)
    }
}
