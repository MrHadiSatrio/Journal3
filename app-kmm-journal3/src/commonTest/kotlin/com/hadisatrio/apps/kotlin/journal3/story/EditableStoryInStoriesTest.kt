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

import com.hadisatrio.apps.kotlin.journal3.id.INVALID_UUID
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EditableStoryInStoriesTest {

    private val stories = SelfPopulatingStories(1, 1, FakeStories())
    private val story = stories.first() as EditableStory

    @Test
    fun `Finds and delegates to an existing story given a valid target ID`() {
        val editableStory = EditableStoryInStories(
            storyId = story.id,
            stories = stories
        )
        editableStory.id.shouldBe(story.id)
        editableStory.title.shouldBe(story.title)
        editableStory.synopsis.shouldBe(story.synopsis)
        editableStory.moments.shouldBe(story.moments)
        editableStory.isNewlyCreated().shouldBe(story.isNewlyCreated())
        val newTitle = "Foo"
        editableStory.update(newTitle)
        story.title.shouldBe(newTitle)
        val newSynopsis = TokenableString("Foo")
        editableStory.update(newSynopsis)
        story.synopsis.shouldBe(newSynopsis)
    }

    @Test
    fun `Delegates to a new story given an empty target ID`() {
        val editableStory = EditableStoryInStories(
            storyId = INVALID_UUID,
            stories = stories
        )
        editableStory.id
        stories.count().shouldBe(2)
    }
}
